# Estágio de compilação
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio de execução
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/todolist-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]