FROM adoptopenjdk/openjdk11:latest
ADD . /dmitriy
COPY build/libs/*.jar app.jar
#COPY docker/init.sql /docker-entrypoint-initdb.d/
ENTRYPOINT ["java","-jar","/app.jar"]
RUN ["apt-get", "update"]
RUN ["apt-get", "install", "-y", "vim"]
