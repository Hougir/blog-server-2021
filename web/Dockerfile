FROM openjdk:8-jre-alpine
VOLUME /tmp
COPY ./target/web-1.0.0.jar app.jar
ENV ADD_OPTS=""
ENV APP_OPTS="--spring.profiles.active=prod"
ENV JVM_OPTS="-Xmx512m -Xms512m -Xmn256m -XX:MaxMetaspaceSize=192m -Xss1024k -XX:+UseG1GC -XX:+PrintGCDetails -Xloggc:/data/logs/blog/gc-%t.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=20M -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCDateStamps -XX:+PrintGCCause -Djava.rmi.server.hostname=47.103.155.11 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=11111 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
#ENTRYPOINT ["java","-jar","/app.jar", "&"]
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS $ADD_OPTS -jar /app.jar $APP_OPTS"]
