Wicket.io - CAS Overlay Template
================================

> 2015-11-25T11:29-0500

Java CAS (Centralized Authentication Service) server implementing BCrypt capable validation.

# Deployment

```
$ vi etc/cas.properties 
$ rsync -Pavz etc/ /etc/cas/
$ chown tomcat8:tomcat8 -R /etc/cas
$ mkdir /etc/cas/logs
$ chmod  ug+rw,o-rwx -R /etc/cas
$ mvnw clean package
$ # deploy target/cas.war to tomcat
```

# CAS Original README 

Generic CAS maven war overlay to exercise the latest versions of CAS. This overlay could be freely used as a starting template for local CAS maven war overlays.

## Versions
```xml
<cas.version>4.1.2</cas.version>
```

## Requirements
* JDK 1.7+

## Configuration 
The `etc` directory contains the configuration files that need to be copied to `/cas/etc`. 

Current files are:

* `cas.properties`
* `log4j2.xml`

## Build

```bash
mvnw clean package
```

or

```bash
mvnw.bat clean package
```

## Deployment

### Embedded Jetty

* Create a Java keystore at `/etc/cas/jetty/thekeystore` with the password `changeit`. 
* Import your CAS server certificate inside this keystore.

```bash
mvnw jetty:run-forked
```

CAS will be available at:

* `http://cas.server.name:8080/cas`
* `https://cas.server.name:8443/cas`

### External
Deploy resultant `target/cas.war` to a Servlet container of choice.
