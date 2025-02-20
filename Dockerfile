FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080
ADD target/pagamento-0.0.1-SNAPSHOT.jar pagamento-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","pagamento-0.0.1-SNAPSHOT.jar"]
