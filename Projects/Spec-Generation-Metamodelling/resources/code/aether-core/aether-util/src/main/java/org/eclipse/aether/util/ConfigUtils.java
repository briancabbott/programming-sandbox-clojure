/*******************************************************************************
 * Copyright (c) 2010, 2014 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.aether.RepositorySystemSession;

/**
 * A utility class to read configuration properties from a repository system session.
 * 
 * @see RepositorySystemSession#getConfigProperties()
 */
public final class ConfigUtils
{

    private ConfigUtils()
    {
        // hide constructor
    }

    /**
     * Gets the specified configuration property as is.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys are set, may be {@code null}.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a valid value is found.
     * @return The property value or {@code null} if none.
     */
    public static Object getObject( Map<?, ?> properties, Object defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value != null )
            {
                return value;
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as is.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys are set, may be {@code null}.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a valid value is found.
     * @return The property value or {@code null} if none.
     */
    public static Object getObject( RepositorySystemSession session, Object defaultValue, String... keys )
    {
        return getObject( session.getConfigProperties(), defaultValue, keys );
    }

    /**
     * Gets the specified configuration property as a string value.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a
     *            string/boolean/number, may be {@code null}.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a string value is found.
     * @return The property value or {@code null} if none.
     */
    public static String getString( Map<?, ?> properties, String defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value instanceof String || value instanceof Boolean || value instanceof Number )
            {
                return value.toString();
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as a string value.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a
     *            string/boolean/number, may be {@code null}.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a string value is found.
     * @return The property value or {@code null} if none.
     */
    public static String getString( RepositorySystemSession session, String defaultValue, String... keys )
    {
        return getString( session.getConfigProperties(), defaultValue, keys );
    }

    /**
     * Gets the specified configuration property as an integer number.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a number or numeric
     *            string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Number} or a string representation of an {@link Integer} is found.
     * @return The property value.
     */
    public static int getInteger( Map<?, ?> properties, int defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value instanceof Number )
            {
                return ( (Number) value ).intValue();
            }

            try
            {
                return Integer.valueOf( (String) value );
            }
            catch ( Exception e )
            {
                // try next key
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as an integer number.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a number or numeric
     *            string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Number} or a string representation of an {@link Integer} is found.
     * @return The property value.
     */
    public static int getInteger( RepositorySystemSession session, int defaultValue, String... keys )
    {
        return getInteger( session.getConfigProperties(), defaultValue, keys );
    }

    /**
     * Gets the specified configuration property as a long number.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a number or numeric
     *            string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Number} or a string representation of a {@link Long} is found.
     * @return The property value.
     */
    public static long getLong( Map<?, ?> properties, long defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value instanceof Number )
            {
                return ( (Number) value ).longValue();
            }

            try
            {
                return Long.valueOf( (String) value );
            }
            catch ( Exception e )
            {
                // try next key
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as a long number.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a number or numeric
     *            string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Number} or a string representation of a {@link Long} is found.
     * @return The property value.
     */
    public static long getLong( RepositorySystemSession session, long defaultValue, String... keys )
    {
        return getLong( session.getConfigProperties(), defaultValue, keys );
    }

    /**
     * Gets the specified configuration property as a single-precision floating point number.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a number or numeric
     *            string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Number} or a string representation of a {@link Float} is found.
     * @return The property value.
     */
    public static float getFloat( Map<?, ?> properties, float defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value instanceof Number )
            {
                return ( (Number) value ).floatValue();
            }

            try
            {
                return Float.valueOf( (String) value );
            }
            catch ( Exception e )
            {
                // try next key
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as a single-precision floating point number.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a number or numeric
     *            string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Number} or a string representation of a {@link Float} is found.
     * @return The property value.
     */
    public static float getFloat( RepositorySystemSession session, float defaultValue, String... keys )
    {
        return getFloat( session.getConfigProperties(), defaultValue, keys );
    }

    /**
     * Gets the specified configuration property as boolean flag.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a boolean or string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Boolean} or a string (to be {@link Boolean#parseBoolean(String) parsed as boolean}) is found.
     * @return The property value.
     */
    public static boolean getBoolean( Map<?, ?> properties, boolean defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value instanceof Boolean )
            {
                return (Boolean) value;
            }
            else if ( value instanceof String )
            {
                return Boolean.parseBoolean( (String) value );
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as a boolean flag.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a boolean or string.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a {@link Boolean} or a string (to be {@link Boolean#parseBoolean(String) parsed as boolean}) is found.
     * @return The property value.
     */
    public static boolean getBoolean( RepositorySystemSession session, boolean defaultValue, String... keys )
    {
        return getBoolean( session.getConfigProperties(), defaultValue, keys );
    }

    /**
     * Gets the specified configuration property as a list of objects.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a collection.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a collection is found.
     * @return The property value or {@code null} if none.
     */
    public static List<?> getList( Map<?, ?> properties, List<?> defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value instanceof List )
            {
                return (List<?>) value;
            }
            else if ( value instanceof Collection )
            {
                return Collections.unmodifiableList( new ArrayList<Object>( (Collection<?>) value ) );
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as a list of objects.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a collection.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a collection is found.
     * @return The property value or {@code null} if none.
     */
    public static List<?> getList( RepositorySystemSession session, List<?> defaultValue, String... keys )
    {
        return getList( session.getConfigProperties(), defaultValue, keys );
    }

    /**
     * Gets the specified configuration property as a map or key value pairs.
     * 
     * @param properties The configuration properties to read, must not be {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a map.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a map is found.
     * @return The property value or {@code null} if none.
     */
    public static Map<?, ?> getMap( Map<?, ?> properties, Map<?, ?> defaultValue, String... keys )
    {
        for ( String key : keys )
        {
            Object value = properties.get( key );

            if ( value instanceof Map )
            {
                return (Map<?, ?>) value;
            }
        }

        return defaultValue;
    }

    /**
     * Gets the specified configuration property as map of key value pairs.
     * 
     * @param session The repository system session from which to read the configuration property, must not be
     *            {@code null}.
     * @param defaultValue The default value to return in case none of the property keys is set to a map.
     * @param keys The property keys to read, must not be {@code null}. The specified keys are read one after one until
     *            a map is found.
     * @return The property value or {@code null} if none.
     */
    public static Map<?, ?> getMap( RepositorySystemSession session, Map<?, ?> defaultValue, String... keys )
    {
        return getMap( session.getConfigProperties(), defaultValue, keys );
    }

}
