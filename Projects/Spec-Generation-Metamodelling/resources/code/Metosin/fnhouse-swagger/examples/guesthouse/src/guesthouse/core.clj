(ns guesthouse.core
  "This is an example project that ties together all of the pieces of fnhouse:
    1. start with a namespace ('guesthouse.guestbook)
    2. suck up the handlers in the namespace with fnhouse.handlers library
    3. wrap each of them with schema coercion middleware from fnhouse.middleware
    4. compile all the handlers into a single router with fnhouse.routes
    5. wrap this root handler in standard ring middleware
    6. start a jetty server"
  (:use plumbing.core)
  (:require
   [ring.adapter.jetty :as jetty]
   [fnhouse.handlers :as handlers]
   [fnhouse.middleware :as middleware]
   [fnhouse.routes :as routes]
   [fnhouse.swagger2 :as swagger]
   [guesthouse.guestbook :as guestbook]
   [guesthouse.ring :as ring]
   [guesthouse.schemas :as schemas]))

(defn custom-coercion-middleware
  "Wrap a handler with the schema coercing middleware"
  [handler]
  (middleware/coercion-middleware
   handler
   (constantly nil)
   schemas/entry-coercer))

(defn attach-docs [resources prefix->ns-sym]
  (let [prefix->ns-sym (assoc prefix->ns-sym "" 'fnhouse.swagger2)
        proto-handlers (handlers/nss->proto-handlers prefix->ns-sym)
        swagger (swagger/collect-routes proto-handlers prefix->ns-sym)]
    (-> resources
        (assoc :swagger swagger)
        ((handlers/curry-resources proto-handlers)))))

(defn wrapped-root-handler
  "Take the resources, partially apply them to the handlers in
   the 'guesthouse.guestbook namespace, wrap each with a custom
   coercing middleware, and then compile them into a root handler
   that will route requests to the appropriate underlying handler.
   Then, wrap the root handler in some standard ring middleware.
   When served, the handlers will be hosted at the 'guestbook' prefix."
  [resources]
  (->> (attach-docs resources {"guestbook" 'guesthouse.guestbook})
       (map custom-coercion-middleware)
       routes/root-handler
       ring/ring-middleware
       swagger/wrap-swagger-ui))

(defn start-api
  "Take resources and server options, and spin up a server with jetty"
  [resources options]
  (-> resources
      wrapped-root-handler
      (jetty/run-jetty options)))

(defn start []
  (start-api {:guestbook (atom {})
              :index (atom 0)}
    {:port 8080 :join? false}))
