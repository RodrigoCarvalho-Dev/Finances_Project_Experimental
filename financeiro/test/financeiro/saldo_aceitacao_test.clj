(ns financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [cheshire.core :as json]
            [financeiro.auxiliares :refer :all]
            [clj-http.client :as http]))

(against-background [(before :facts (iniciar-servidor porta-padrao))
                     (after :facts (parar-servidor))]
                    (fact "O Saldo inicial é 0" :aceitacao
                          (json/parse-string (conteudo "/saldo") true) => {:saldo 0})

                    (fact "O saldo é 10 quando o única transação é uma receita de 10" :aceitacao
                          (http/post (endereco-para "/transacoes")
                                     {:content-type :json
                                      :body (json/generate-string {:valor 10 :tipo "receita"})})
                          (json/parse-string (conteudo "/saldo") true) => {:saldo 10}))