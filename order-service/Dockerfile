FROM adoptopenjdk/openjdk11:latest
COPY build/libs/*.jar orderApp.jar
ENTRYPOINT ["java","-jar","/orderApp.jar"]
