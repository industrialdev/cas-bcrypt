FROM jeanblanchard/java:8

WORKDIR /srv/wicket-cas
COPY ./target/dependency/jetty-runner.jar \
 ./target/cas.war \
 ./etc/jetty/jetty-runner-config.xml \
 ./run.sh \
 ./

COPY ./etc/cas.properties /etc/cas/cas.properties
	
ENV CAS_KEYSTORE_PATH /etc/cas/keystore

EXPOSE 8090 8443

CMD ./run.sh