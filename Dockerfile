FROM openjdk:20
ADD target/homework-3.1.0.jar homework-3.1.0.jar
ENTRYPOINT ["java", "-jar","homework-3.1.0.jar"]
EXPOSE 8080