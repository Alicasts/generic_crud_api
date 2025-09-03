# ---- build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN ./mvnw -q -B -DskipTests package

# ---- run ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -u 1001 appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]