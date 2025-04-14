FROM eclipse-temurin:21-jre
WORKDIR /app
LABEL maintainer="abdelrahman@example.com" version="1.0" description="Task Service"
RUN apt-get update && apt-get install -y --no-install-recommends curl && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
