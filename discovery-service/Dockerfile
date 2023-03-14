FROM adoptopenjdk/openjdk11:latest
COPY build/libs/*.jar discovery.jar
ENTRYPOINT ["java","-jar","/discovery.jar"]
