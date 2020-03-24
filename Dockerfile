FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.6_10
COPY ./target/*.jar /app.jar
EXPOSE 8080

CMD ["java", "-jar", "/app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]