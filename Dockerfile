# ---- build stage: compile the jar with Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---- run stage: small JRE image with only the jar ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/collabdesk-*.jar app.jar
ENV TZ=Asia/Singapore
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
