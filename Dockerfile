FROM amazoncorretto:17-al2023
MAINTAINER yourname@example.com
COPY target/sso-0.0.1-SNAPSHOT.jar sso.jar
ENTRYPOINT ["java", "-jar", "/sso.jar"]