#!/usr/bin/env sh

java \
 -Ddatabase.url="jdbc:postgresql://${DB_ENV_HOST}/${DB_ENV_POSTGRES_DB}" \
 -Ddatabase.user=${DB_ENV_POSTGRES_USER} \
 -Ddatabase.password=${DB_ENV_POSTGRES_PASSWORD} \
 -Djetty.ssl.keystore.path="${CAS_KEYSTORE_PATH}" \
 -Djetty.ssl.truststore.path="${CAS_KEYSTORE_PATH}" \
 -Dservice.registry.config.location="${CAS_JSON_REGISTRY_PATH:-classpath:services}" \
 -jar jetty-runner.jar \
 --config jetty-runner-config.xml \
 cas.war
