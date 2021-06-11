FROM openjdk:8-jre-alpine
VOLUME /tmp
COPY ./target/web-1.0.0.jar app.jar
ENV ADD_OPTS=""
ENV APP_OPTS="--spring.profiles.active=prod"
ENV JVM_OPTS="-Xmx256m -Xms256m -XX:MaxMetaspaceSize=192m -Xss512k -XX:+UseG1GC -XX:+PrintGCDetails -Djava.rmi.server.hostname=47.103.155.11 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=11111 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
#ENTRYPOINT ["java","-jar","/app.jar", "&"]
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS $ADD_OPTS -jar /app.jar $APP_OPTS"]
