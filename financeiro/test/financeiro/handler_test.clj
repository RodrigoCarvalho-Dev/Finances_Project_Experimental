(ns financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [financeiro.handler :refer :all]
            [financeiro.db :as db]))


(facts "Da um 'Hello World' na rota raiz"
       (let [response (app (mock/request :get "/"))]
         (fact "o status da reposta é 200"
               (:status response) => 200)

         (fact "o texto do corpo é Hello World"
               (:body response) => "Ola mundo")))

(facts "Rota inválida não existe"
       (let [response (app (mock/request :get "/invalid"))]
         (fact "o codigo de erro é 404"
               (:status response) => 404)

         (fact "o texto do corpo é 'Recurso não encontrado'"
               (:body response) => "Recurso não encontrado")))

(facts "Saldo inicial é 0"
       (against-background [(json/geneFrate-string {:saldo 0}) => "{\"saldo\":0}"
                            (db/saldo) => 0]) 
       (let [response (app (mock/request :get "/saldo"))]
         (fact "o formato é 'application/json"
               (get-in response [:headers "Content-Type"])
               => "application/json; charset=utf-8")
         (fact "o status da resposta é 200"
               (:status response) => 200)
         (fact "o texto do corpo é um JSON cuja chave é saldo e o valor é 0"
               (:body response) => "{\"saldo\":0}")))

(facts "Registra uma receita no valor de 10"
       (against-background (db/registrar {:valor 10 :tipo "receita"}) => {:id 1 :valor 10 :tipo "receita"})
       (let [response (app (-> (mock/request :post "/transacoes") (mock/json-body {:valor 10 :tipo "receita"})))]
         (fact "o status da reposta é 201"
               (:status response) => 201)
         (fact "o texto do corpo é um JSON com o conteudo enviado e um id"
               (:body response) => "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")))

