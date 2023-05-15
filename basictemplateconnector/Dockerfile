FROM openjdk:17-alpine
VOLUME /d/tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
# CHANGE ME - EXPOSE = Port Number provided when uploading connector
EXPOSE 9154
RUN apk --no-cache add curl
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"] 

# current dockerfile template - should we just replace the existing entirely?
FROM openjdk:17-alpine
VOLUME /d/tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
EXPOSE 9090
USER root
COPY lib/dd-java-agent.jar $JAVA_HOME/lib
COPY lib/env.sh /usr/local/bin
ENV SERVICE_NAME ${connectorName}
ENV SERVICE_VERSION ${connectorVersion}
ENV CU All
RUN apk --no-cache add curl
ENTRYPOINT ["/bin/sh", "-c" , "chmod 755 /usr/local/bin/env.sh && . /usr/local/bin/env.sh && exec java -Djava.security.egd=file:/dev/./urandom $JAVA_AGENT -jar /app.jar"]