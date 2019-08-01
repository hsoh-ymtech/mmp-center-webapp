#
# docker build --tag mmp-center-webapp:0.0.1 .
# docker images
# docker run -t -p 8080:9000 mmp-center-webapp:0.0.1
#
FROM openjdk:12-jdk-alpine

#maintainer
MAINTAINER diyune@gmail.com

ARG PACKAGED_JAR=target/webapp-0.0.1.jar

ADD ${PACKAGED_JAR} app.jar

ENTRYPOINT ["java","-jar","-Xdebug","-Xrunjdwp:server=y,transport=dt_socket,address=8001,suspend=n","/app.jar"]

EXPOSE 8080:9000
EXPOSE 8001:8001
