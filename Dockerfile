# Estágio de compilação
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio de execução (Usando uma imagem estável e atualizada)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/todolist-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]