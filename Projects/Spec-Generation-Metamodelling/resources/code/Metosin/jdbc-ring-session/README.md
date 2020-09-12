# jdbc-ring-session

A Clojure library for JDBC backed Ring sessions.

[![Clojars Project](http://clojars.org/jdbc-ring-session/latest-version.svg)](http://clojars.org/jdbc-ring-session)

## Usage

Use the `jdbc-ring-session.core/jdbc-store` function to create a new store. The function accepts
a `clojure.java.jdbc` datasource definition:

```clojure
(ns db.core
  (:require [jdbc-ring-session.core :refer [jdbc-store]]))

(def db
  {:subprotocol "postgresql"
   :subname "session"
   :user "admin"
   :password "admin"})

(def store (jdbc-store db))
```

### database configuration

The session will be stored as a byte array serialized using [nippy](https://github.com/ptaoussanis/nippy). The table formats are shown below.


PostgeSQL:

```sql
CREATE TABLE session_store
(
  session_id VARCHAR(36) NOT NULL PRIMARY KEY,
  idle_timeout BIGINT,
  absolute_timeout BIGINT,
  value BYTEA
)
```

Oracle:

```sql
CREATE TABLE SESSION_STORE
(
  session_id VARCHAR2(100 BYTE) NOT NULL PRIMARY KEY,
  absolute_timeout NUMBER,
  idle_timeout NUMBER,
  value BLOB
)
```

MySQL:

```sql
CREATE TABLE `session_store` (
  `session_id` VARCHAR(36) NOT NULL,
  `idle_timeout` DOUBLE DEFAULT NULL,
  `absolute_timeout` DOUBLE DEFAULT NULL,
  `value` BLOB,
  PRIMARY KEY (`session_id`)
)
```

H2:

```h2
CREATE TABLE session_store (
  session_id VARCHAR(36) NOT NULL,
  idle_timeout BIGINT DEFAULT NULL,
  absolute_timeout BIGINT DEFAULT NULL,
  value BINARY(10000),
  PRIMARY KEY (session_id)
)
```


### session store initialization

The `jdbc-store` function accepts an optional map with the keys called `:table`, `:serializer` and `:deserializer`. The `:table` defaults to `:session_store`, while the `:serializer` and `:deserializer` keys are used to specify how the session data should be serialized and deserialized for the specific database. The library will attempt to figure out the appropriate serializer/deserializer based on the connection type. MySQL, PostgeSQL and Oracle BLOB formats are supported out of the box.

```clojure
(jdbc-store db {:table :sessions})
```

### custom serialization

The serializer function accepts the session map and returns the serialized value that will be inserted
in the table, eg:

```clojure
(defn serialize-postgres [value]
  (nippy/freeze value))
```

The deserializer function receives the session value in the database and returns the deserialized session, eg:

```clojure
(defn deserialize-postgres [value]
  (when value
    (nippy/thaw value)))
```


### stale session cleanup

A cleaner thread is provided in the `ring-jdbc-session.cleaner` for removing expired sessions from the database. The `idle_timeout` and `absolute_timeout` keys are expected to be populated by the [ring-session-timeout](https://github.com/ring-clojure/ring-session-timeout) library. These keys are used by the cleaner to remove stale sessions. The cleaner can be started and stopped as follows:

```clojure
(ns db.core
  (:require [jdbc-ring-session.cleaner :refer [start-cleaner stop-cleaner]))

(start-cleaner db)

(stop-cleaner session-cleaner)
```

The `start-cleaner` function accepts an optional map with the `:interval` key that defaults to 60. This is the number of seconds to sleep between runs.

```clojure
(start-cleaner {:interval 120})
```

## Troubleshooting

### Running a database on a non standard port

Note that when running a database on a non-standard port, the jdbc datasource definition must be formatted as follows ([from jdbc source](https://github.com/clojure/java.jdbc/blob/master/src/main/clojure/clojure/java/jdbc.clj#L195)):

```
DriverManager (alternative):
    :dbtype      (required) a String, the type of the database (the jdbc subprotocol)
    :dbname      (required) a String, the name of the database
    :host        (optional) a String, the host name/IP of the database
                            (defaults to 127.0.0.1)
    :port        (optional) a Long, the port of the database
                            (defaults to 3306 for mysql, 1433 for mssql/jtds, else nil)
                            (others)     (optional) passed to the driver as properties.
```

Adding a `:port` key to the standard DriverManager map will result in the database connection failing with a `org.postgresql.util.PSQLException: FATAL: password authentication failed for user`, and it can be difficult to trace it back to the DriverManager format.

Using a string `subprotocol://user:password@host:post/subname`, instead of a DriverManager map will also work.

## License

Copyright © 2015 Yogthos

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
