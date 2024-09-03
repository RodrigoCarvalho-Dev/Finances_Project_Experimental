(ns financeiro.auxiliares
    (:require [financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-http.client :as http]))

(def servidor (atom nil))

(def porta-padrao 4000)

(defn endereco-para [rota] (str "http://localhost:" porta-padrao rota))

(def requisicao-para (comp http/get endereco-para))

(defn conteudo [rota] (:body (requisicao-para rota)))

(defn iniciar-servidor [porta]
    (swap! servidor
        (fn [_] (run-jetty app {:port porta :join? false}))))

(defn parar-servidor []
    (.stop @servidor))