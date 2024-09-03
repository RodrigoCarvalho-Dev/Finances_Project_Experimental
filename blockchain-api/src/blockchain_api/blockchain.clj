(ns blockchain-api.blockchain
    (:require [digest :as digest]
            [clj-http.client :as client]))

(defn get-transacoes []
    (let [resposta (client/get "http://localhost:3006/transacoes" {:as :json})]
        (:body resposta)))

(defn calcular-hash [bloco]
    (digest/sha-256 (str (:id bloco) (:nonce bloco) (:hash-anterior bloco) (:dados bloco))))

(def blockchain (atom []))

(def bloco-geneses {:id 0
                    :nonce 0
                    :dados "Bloco Geneses: Historico de transacoes"
                    :hash-anterior "00000000000000000000000000000000000000000000000000000000000000000"
                    :hash (calcular-hash {:id 0 :nonce 0 :dados "Bloco Geneses: Historico de transacoes" :hash-anterior "00000000000000000000000000000000000000000000000000000000000000000"})})

(defn visualizar-blockchain []
    @blockchain)

(defn base-bloco [id nonce dados hash-anterior]
    (let [bloco {:id id
               :nonce nonce
               :dados dados
               :hash-anterior hash-anterior
               :hash (calcular-hash {:id id :nonce nonce :dados dados :hash-anterior hash-anterior})}]
    bloco))

(defn criar-bloco [dados]
    (if (= (count @blockchain) 0) (swap! blockchain conj bloco-geneses))
    (let [bloco-anterior (last @blockchain)
        hash-anterior (:hash bloco-anterior)
        id (inc (:id bloco-anterior))]
    (base-bloco id 0 dados hash-anterior)))

(defn adicionar-bloco-na-blockchain [bloco]
    (swap! blockchain conj bloco))

(defn gerar-bloco-na-blockchain []
    (let [transacoes (get-transacoes)
        novo-bloco (criar-bloco transacoes)]
        (println "teste 3")
        (adicionar-bloco-na-blockchain novo-bloco)
        novo-bloco))

(defn buscar-nonce [id dados hash-anterior]
    (loop [nonce 0]
        (let [hash (digest/sha-256 (str id nonce hash-anterior dados))]
            (if (.startsWith hash "0000")
                nonce
                (recur (inc nonce))))))

(defn minerar-bloco [bloco]
    (let [hash-anterior (if (= (:id bloco) 0) "00000000000000000000000000000000000000000000000000000000000000000" (:hash (nth @blockchain (- (:id bloco) 1))))
          nonce (buscar-nonce (:id bloco) (:dados bloco) hash-anterior)]
        (base-bloco (:id bloco) nonce (:dados bloco) hash-anterior)))

(defn validar-bloco? [bloco]
    (let [nonce (buscar-nonce (:id bloco) (:dados bloco) (:hash-anterior bloco))
          hash-calculado (calcular-hash {:id (:id bloco) :nonce nonce :dados (:dados bloco) :hash-anterior (:hash-anterior bloco)})]
        (= hash-calculado (:hash bloco))))
        
(defn minerar-todos-os-blocos []
    (doseq [bloco @blockchain]
        (println "teste 2")
        (when-not (validar-bloco? bloco)
            (println "teste 1")
            (let [bloco-minerado (minerar-bloco bloco)]
                (swap! blockchain assoc (:id bloco) bloco-minerado)))))

