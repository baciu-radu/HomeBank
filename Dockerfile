FROM openjdk:20
ADD target/homebank-app.jar homebank-app.jar
ENTRYPOINT ["java", "-jar","homebank-app.jar"]
EXPOSE 8080