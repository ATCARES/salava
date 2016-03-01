(ns salava.core.handler
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [salava.core.session :refer [wrap-app-session]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.webjars :refer [wrap-webjars]]))


(defn get-route-def [ctx plugin]
  (let [sym (symbol (str "salava." (name plugin) ".routes/route-def"))]
    (require (symbol (namespace sym)) :reload)
    ((resolve sym) ctx)))


(defn resolve-routes [ctx]
  (apply routes (map (fn [p] (get-route-def ctx p)) (conj (get-in ctx [:config :core :plugins]) :core))))


(defn ignore-trailing-slash
  "Modifies the request uri before calling the handler.
  Removes a single trailing slash from the end of the uri if present.

  Useful for handling optional trailing slashes until Compojure's route matching syntax supports regex.
  Adapted from http://stackoverflow.com/questions/8380468/compojure-regex-for-matching-a-trailing-slash"
  [handler]
  (fn  [request]
    (let [uri (:uri request)]
      (handler (assoc request :uri (if (and (not (= "/" uri))
                                            (.endsWith uri "/"))
                                     (subs uri 0 (dec (count uri)))
                                     uri))))))

(defn wrap-middlewares [ctx routes]
  (let [config (get-in ctx [:config :core])]
    (-> routes
        (ignore-trailing-slash)
        (wrap-defaults (-> site-defaults
                           (assoc-in [:security :anti-forgery] false)
                           (assoc-in [:session] false)))
        (wrap-app-session config)
        (wrap-webjars))))


(defn handler [ctx]
    (wrap-middlewares
      ctx
      (api
        (swagger-routes {:ui "/swagger-ui"
                         :info  {:version "0.1.0"
                               :title "Salava REST API"
                               :description ""
                               :contact  {:name "Discendum Oy"
                                          :email "contact@openbadgepassport.com"
                                          :url "http://salava.org"}
                               :license  {:name "Apache 2.0"
                                          :url "http://www.apache.org/licenses/LICENSE-2.0"}}
                       :tags  [{:name "badge", :description "plugin"}
                               {:name "file", :description "plugin"}
                               {:name "gallery", :description "plugin"}
                               {:name "page", :description "plugin"}
                               {:name "translator", :description "plugin"}
                               {:name "user", :description "plugin"}]})
        (resolve-routes ctx)
        (route/not-found "404 Not found"))))

