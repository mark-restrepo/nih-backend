FROM eclipse-temurin:11-alpine
COPY target/nih-1.0-SNAPSHOT.jar /nih-1.0-SNAPSHOT.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/nih-1.0-SNAPSHOT.jar"]