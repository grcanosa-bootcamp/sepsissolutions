FROM maven:3.6.0-jdk-8-slim AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:8
ARG VERSION=0.0.1
COPY --from=build /usr/src/app/target/sepsissolutions-$VERSION-SNAPSHOT-allinone.jar /sepsissolutions.jar
EXPOSE 8080  
WORKDIR /
ENTRYPOINT ["java","-jar","sepsissolutions.jar"]  
