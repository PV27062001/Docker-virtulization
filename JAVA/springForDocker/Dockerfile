FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
VOLUME /tmp
EXPOSE 8080
COPY build/libs/springForDocker-0.0.1-SNAPSHOT.jar springForDocker.jar
ENTRYPOINT ["java", "-jar", "springForDocker.jar"]

# Docker commands:
# docker build -t spring .
# docker run -p 8080:8080 -d spring
# Access: http://localhost:8080/message