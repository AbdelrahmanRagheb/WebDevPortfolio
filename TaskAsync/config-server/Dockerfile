FROM eclipse-temurin:21-jre
WORKDIR /app
LABEL maintainer="abdelrahman@example.com" version="1.0" description="Config Server"
RUN apt-get update && apt-get install -y --no-install-recommends curl && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY target/configserver-0.0.1-SNAPSHOT.jar /app/configserver.jar
EXPOSE 8071
ENTRYPOINT ["java", "-jar", "/app/configserver.jar"]