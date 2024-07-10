FROM amazoncorretto:21-alpine-jdk
COPY target/*.jar shareit-server-app.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
ENTRYPOINT ["java","-jar","/shareit-server-app.jar"]