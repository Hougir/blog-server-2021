FROM openjdk:8-jre-alpine
VOLUME /tmp
COPY ./target/web-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar", "&"]
