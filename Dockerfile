FROM openjdk:8-jdk-alpine

RUN apk add --no-cache bash
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY target/deliveryapi-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
