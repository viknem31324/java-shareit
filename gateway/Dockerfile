FROM amazoncorretto:21-alpine-jdk
COPY target/*.jar shareit-gateway-app.jar

ENTRYPOINT ["java","-jar","/shareit-gateway-app.jar"]