# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#using-boot-devtools)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Web Starter](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-developing-web-applications)
* [Rest Repositories](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#howto-use-exposing-spring-data-repositories-rest-endpoint)
* [Spring Security](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-security)
* [OAuth2 Client](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-security-oauth2-client)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-security-oauth2-server)
* [Spring LDAP](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-ldap)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Flyway Migration](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#howto-execute-flyway-database-migrations-on-startup)
* [JOOQ Access Layer](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-jooq)
* [Spring Data Redis (Access+Driver)](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-redis)
* [Spring Data Elasticsearch (Access+Driver)](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-elasticsearch)
* [Spring Integration](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-integration)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-kafka)
* [Spring Boot Admin (Client)](https://codecentric.github.io/spring-boot-admin/current/#getting-started)
* [Spring Boot Admin (Server)](https://codecentric.github.io/spring-boot-admin/current/#getting-started)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#production-ready)
* [Java Mail Sender](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-email)
* [WebSocket](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-websockets)
* [Spring for Apache ActiveMQ Artemis](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-artemis)
* [Spring Data Neo4j](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-neo4j)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing JPA Data with REST](https://spring.io/guides/gs/accessing-data-rest/)
* [Accessing Neo4j Data with REST](https://spring.io/guides/gs/accessing-neo4j-data-rest/)
* [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Messaging with Redis](https://spring.io/guides/gs/messaging-redis/)
* [Circuit Breaker](https://spring.io/guides/gs/circuit-breaker/)
* [Routing and Filtering](https://spring.io/guides/gs/routing-and-filtering/)
* [Using Spring Cloud Gateway](https://github.com/spring-cloud-samples/spring-cloud-gateway-sample)
* [Client Side Load Balancing with Ribbon and Spring Cloud](https://spring.io/guides/gs/client-side-load-balancing/)
* [Service Registration and Discovery](https://spring.io/guides/gs/service-registration-and-discovery/)
* [Integrating Data](https://spring.io/guides/gs/integration/)
* [Centralized Configuration](https://spring.io/guides/gs/centralized-configuration/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)
* [Messaging with JMS](https://spring.io/guides/gs/messaging-jms/)
* [Accessing Data with Neo4j](https://spring.io/guides/gs/accessing-data-neo4j/)

# Spring Cloud Netflix Maintenance Mode

The dependencies listed below are in maintenance mode. We do not recommend adding them to
new projects:

*  Hystrix Dashboard
*  Ribbon
*  Hystrix
*  Zuul

The decision to move most of the Spring Cloud Netflix projects to maintenance mode was
a response to Netflix not continuing maintenance of many of the libraries that we provided
support for.

Please see [this blog entry](https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode)
for more information on maintenance mode and a list of suggested replacements for those
libraries.
