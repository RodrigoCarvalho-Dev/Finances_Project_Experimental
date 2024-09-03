(ns blockchain-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [blockchain-api.blockchain :as blockchain]))

(defn como-json [conteudo & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string conteudo)})

(defroutes app-routes
  (GET "/" [] "oiiiiiiiiiiiiii")
  (GET "/blockchain" [] (como-json {:blockchain (blockchain/visualizar-blockchain)}))
  (POST "/blockchain" []
    (-> (blockchain/gerar-bloco-na-blockchain)
    (como-json 201)))
  (POST "/blockchain/minerar" []
  (-> (blockchain/minerar-todos-os-blocos)
    (como-json 201)))
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))
