FROM amazoncorretto:17-alpine-jdk
VOLUME /d/tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
EXPOSE 9090
USER root
COPY lib/dd-java-agent.jar $JAVA_HOME/lib
COPY lib/env.sh /usr/local/bin
ENV SERVICE_NAME myconnector
ENV SERVICE_VERSION 1.0
ENV CU All
RUN apk --no-cache add curl
ENTRYPOINT ["/bin/sh", "-c" , "chmod 755 /usr/local/bin/env.sh && . /usr/local/bin/env.sh && exec java -Djava.security.egd=file:/dev/./urandom $JAVA_AGENT -jar /app.jar" ]