/*******************************************************************************
 * Copyright (c) 2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.connector.basic;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.UUID;

import org.eclipse.aether.spi.log.Logger;

/**
 * A partially downloaded file with optional support for resume. If resume is enabled, a well-known location is used for
 * the partial file in combination with a lock file to prevent concurrent requests from corrupting it (and wasting
 * network bandwith). Otherwise, a (non-locked) unique temporary file is used.
 */
final class PartialFile
    implements Closeable
{

    static final String EXT_PART = ".part";

    static final String EXT_LOCK = ".lock";

    interface RemoteAccessChecker
    {

        void checkRemoteAccess()
            throws Exception;

    }

    static class LockFile
    {

        private final File lockFile;

        private final FileLock lock;

        private final boolean concurrent;

        public LockFile( File partFile, int requestTimeout, RemoteAccessChecker checker, Logger logger )
            throws Exception
        {
            lockFile = new File( partFile.getPath() + EXT_LOCK );
            boolean[] concurrent = { false };
            lock = lock( lockFile, partFile, requestTimeout, checker, logger, concurrent );
            this.concurrent = concurrent[0];
        }

        private static FileLock lock( File lockFile, File partFile, int requestTimeout, RemoteAccessChecker checker,
                                      Logger logger, boolean[] concurrent )
            throws Exception
        {
            boolean interrupted = false;
            try
            {
                for ( long lastLength = -1, lastTime = 0;; )
                {
                    FileLock lock = tryLock( lockFile );
                    if ( lock != null )
                    {
                        return lock;
                    }

                    long currentLength = partFile.length();
                    long currentTime = System.currentTimeMillis();
                    if ( currentLength != lastLength )
                    {
                        if ( lastLength < 0 )
                        {
                            concurrent[0] = true;
                            /*
                             * NOTE: We're going with the optimistic assumption that the other thread is downloading the
                             * file from an equivalent repository. As a bare minimum, ensure the repository we are given
                             * at least knows about the file and is accessible to us.
                             */
                            checker.checkRemoteAccess();
                            logger.debug( "Concurrent download of " + partFile + " in progress, awaiting completion" );
                        }
                        lastLength = currentLength;
                        lastTime = currentTime;
                    }
                    else if ( requestTimeout > 0 && currentTime - lastTime > Math.max( requestTimeout, 3 * 1000 ) )
                    {
                        throw new IOException( "Timeout while waiting for concurrent download of " + partFile
                            + " to progress" );
                    }

                    try
                    {
                        Thread.sleep( 100 );
                    }
                    catch ( InterruptedException e )
                    {
                        interrupted = true;
                    }
                }
            }
            finally
            {
                if ( interrupted )
                {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private static FileLock tryLock( File lockFile )
            throws IOException
        {
            RandomAccessFile raf = new RandomAccessFile( lockFile, "rw" );
            try
            {
                FileLock lock = raf.getChannel().tryLock( 0, 1, false );
                if ( lock == null )
                {
                    close( raf );
                }
                return lock;
            }
            catch ( OverlappingFileLockException e )
            {
                close( raf );
                return null;
            }
            catch ( RuntimeException e )
            {
                close( raf );
                lockFile.delete();
                throw e;
            }
            catch ( IOException e )
            {
                close( raf );
                lockFile.delete();
                throw e;
            }
        }

        private static void close( Closeable file )
        {
            try
            {
                file.close();
            }
            catch ( IOException e )
            {
                // irrelevant
            }
        }

        public boolean isConcurrent()
        {
            return concurrent;
        }

        public void close()
        {
            close( lock.channel() );
            lockFile.delete();
        }

        @Override
        public String toString()
        {
            return lockFile + " - " + lock.isValid();
        }

    }

    static class Factory
    {

        private final boolean resume;

        private final long resumeThreshold;

        private final int requestTimeout;

        private final Logger logger;

        public Factory( boolean resume, long resumeThreshold, int requestTimeout, Logger logger )
        {
            this.resume = resume;
            this.resumeThreshold = resumeThreshold;
            this.requestTimeout = requestTimeout;
            this.logger = logger;
        }

        public PartialFile newInstance( File dstFile, RemoteAccessChecker checker )
            throws Exception
        {
            if ( resume )
            {
                File partFile = new File( dstFile.getPath() + EXT_PART );

                long reqTimestamp = System.currentTimeMillis();
                LockFile lockFile = new LockFile( partFile, requestTimeout, checker, logger );
                if ( lockFile.isConcurrent() && dstFile.lastModified() >= reqTimestamp - 100 )
                {
                    lockFile.close();
                    return null;
                }
                try
                {
                    if ( !partFile.createNewFile() && !partFile.isFile() )
                    {
                        throw new IOException( partFile.exists() ? "Path exists but is not a file" : "Unknown error" );
                    }
                    return new PartialFile( partFile, lockFile, resumeThreshold, logger );
                }
                catch ( IOException e )
                {
                    lockFile.close();
                    logger.debug( "Cannot create resumable file " + partFile.getAbsolutePath() + ": " + e );
                    // fall through and try non-resumable/temporary file location
                }
            }

            File tempFile =
                File.createTempFile( dstFile.getName() + '-' + UUID.randomUUID().toString().replace( "-", "" ), ".tmp",
                                     dstFile.getParentFile() );
            return new PartialFile( tempFile, logger );
        }

    }

    private final File partFile;

    private final LockFile lockFile;

    private final long threshold;

    private final Logger logger;

    private PartialFile( File partFile, Logger logger )
    {
        this( partFile, null, 0, logger );
    }

    private PartialFile( File partFile, LockFile lockFile, long threshold, Logger logger )
    {
        this.partFile = partFile;
        this.lockFile = lockFile;
        this.threshold = threshold;
        this.logger = logger;
    }

    public File getFile()
    {
        return partFile;
    }

    public boolean isResume()
    {
        return lockFile != null && partFile.length() >= threshold;
    }

    public void close()
    {
        if ( partFile.exists() && !isResume() )
        {
            if ( !partFile.delete() && partFile.exists() )
            {
                logger.debug( "Could not delete temorary file " + partFile );
            }
        }
        if ( lockFile != null )
        {
            lockFile.close();
        }
    }

    @Override
    public String toString()
    {
        return String.valueOf( getFile() );
    }

}
