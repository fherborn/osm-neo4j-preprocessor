FROM openjdk:8-jdk-alpine
EXPOSE 8080
MAINTAINER Florian Herborn florian.herborn@th-koeln.de

COPY build/libs/host-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]