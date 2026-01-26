FROM openjdk:17

WORKDIR /app

COPY target/simplyCinema-0.0.1-SNAPSHOT.jar simplyCinema.jar

EXPOSE 8080

CMD ["java", "-jar", "simplyCinema.jar"]
