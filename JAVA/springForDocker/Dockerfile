FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew build -x test


FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
VOLUME /tmp
EXPOSE 8080
COPY --from=builder /app/build/libs/springForDocker-0.0.1-SNAPSHOT.jar springForDocker.jar
ENTRYPOINT ["java", "-jar", "springForDocker.jar"]

# Docker commands:
# docker build -t spring .
# docker run -p 8080:8080 -d spring
# Access: http://localhost:8080/message