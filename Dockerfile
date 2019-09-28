#
# docker build --tag mmp-center-webapp:0.0.1 .
# docker images
# docker run -t -p 8080:9000 mmp-center-webapp:0.0.1
#
FROM openjdk:12-jdk-alpine

#maintainer
MAINTAINER diyune@gmail.com
USER root

ENV container docker
ENV NQ_HOME=/root/HOME/

#RUN yum clean all \
# && yum repolist \
# && yum install -y crontabs

RUN mkdir ${NQ_HOME}
RUN mkdir ${NQ_HOME}/bin
RUN mkdir ${NQ_HOME}/data
RUN mkdir ${NQ_HOME}/template

ARG PACKAGED_JAR=target/webapp-0.0.1.jar
ADD ${PACKAGED_JAR} ${NQ_HOME}/bin/app.jar
ADD ./target/classes/application.properties ${NQ_HOME}/bin/application.properties

EXPOSE 8080:80
EXPOSE 8001:8001

#ENTRYPOINT ["java","-jar","-Xdebug","-Xrunjdwp:server=y,transport=dt_socket,address=8001,suspend=n","--spring.config.location=/root/HOME/bin/application.properties","/app.jar"]

VOLUME ["/sys/fs/cgroup"]
