(ns ^:no-doc crux.soak.config
  (:require [clojure.string :as string]
            [nomad.config :as n])
  (:import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
           software.amazon.awssdk.services.secretsmanager.SecretsManagerClient))

(n/defconfig confluent-creds
  {:broker (n/decrypt :soak "bn7VyKL4pQHG8/s2cuCHrACDv5JH58oECsTGJCJ6vZsZD52kfo6+LEdT4vCuargNwH2cKC1aSK+JxNPk/pakVpUr94Ig4Nq4j8P0usUezuc=")
   :api-token (n/decrypt :soak "tEGUPMDugApD+WmM9jQ5Slke0Xobf+a62IMiy1MvtSoHIazSaOGbmeaGdjTc28cZqYOBsjrcye9OoX+E76YDZQ==")
   :api-secret (n/decrypt :soak "3f5TbRLZjfJ4sKBYonGGZr2ay0pS6j7WDmnTdKO/viyR6vfcdJcHc3YU9NSwcepu/6qCZqpkPVHARnx5sgNHOu7Kdb/UHA59pCcdKwPAXXrvnuiGhkHhUUAzDx8SH+iyINR+zAebHPzfG6FDsVykrA==")})

(n/defconfig weather-api-token
  (n/decrypt :soak "jZ6eDEieB5Z2CjOjSap7rMUNfTLZACKuZOJVs7cG2+VW4xFjU7ynNbMOY4i46GcRZPE2yfYYIpqEkCakPQHYiCEvY+nRtd0pMoStJwnNG1M="))

(defn kafka-properties []
  {"ssl.endpoint.identification.algorithm" "https"
   "sasl.mechanism" "PLAIN"
   "sasl.jaas.config" (str (->> ["org.apache.kafka.common.security.plain.PlainLoginModule"
                                 "required"
                                 (format "username=\"%s\"" (:api-token confluent-creds))
                                 (format "password=\"%s\"" (:api-secret confluent-creds))]
                                (string/join " "))
                           ";")
   "security.protocol" "SASL_SSL"})

(defn crux-node-config []
  {:crux.kafka/bootstrap-servers (:broker confluent-creds)
   :crux.kafka/replication-factor 3
   :crux.kafka/tx-topic "soak-transaction-log"
   :crux.kafka/doc-topic "soak-docs"
   :crux.kafka/doc-partitions 1
   :crux.kafka/kafka-properties-map (kafka-properties)})

(defn load-secret-key []
  (-> (SecretsManagerClient/create)
      (.getSecretValue (-> (GetSecretValueRequest/builder)
                           (.secretId "soak/nomad-key")
                           (.build)))
      (.secretString)))
