# TaskAsync - Microservices Task Management Backend

Welcome to **TaskAsync**, a microservices-based backend for a task management system built with **Spring Boot** and **Java**. This project highlights my skills as a junior developer in creating secure, resilient, and scalable backend services using modern patterns like event-driven architecture and containerization.

---

### Project Overview

TaskAsync powers task management with features like user authentication, task assignments, and real-time notifications. It leverages **Keycloak** for security, **RabbitMQ** for events, and **Docker** for deployment, showcasing my ability to build robust microservices.

- **Secure Authentication**: Uses OAuth2/OpenID Connect via Keycloak to protect APIs.
- **Task Management**: Enables task creation, assignment, and status tracking.
- **Real-Time Notifications**: Sends task and comment alerts with RabbitMQ.
- **Resilience**: Applies circuit breakers for fault tolerance.
- **Testing**: Features unit tests with JUnit and H2 for full coverage.

---

### Technologies Used

- **Spring Boot**: RESTful APIs and microservices
- **Spring Security**: OAuth2/OpenID Connect authentication
- **JPA/Hibernate**: Database operations
- **MySQL**: Production data storage
- **H2 Database**: Testing
- **RabbitMQ**: Event-driven messaging
- **Keycloak**: Identity management
- **Docker**: Containerized services
- **Spring Cloud Circuit Breaker**: Resilience
- **Spring Cloud Netflix Eureka**: Service discovery
- **Spring Cloud Gateway**: API routing
- **Spring Cloud Config**: Centralized configuration
- **JUnit/Mockito**: Unit testing
- **Maven**: Build management

---

### Project Structure

TaskAsync is a microservices system with multiple Spring Boot applications, each serving a specific role:
- **config-server**: Centralizes configuration for all services.
- **eureka-server**: Handles service discovery with Spring Cloud Netflix Eureka.
- **gateway-server**: Routes API requests using Spring Cloud Gateway.
- **notification-service**: Manages task and comment notifications with RabbitMQ.
- **task-service**: Handles task creation, assignment, and status updates.
- **user-service**: Manages user profiles and authentication logic.

---

### Getting Started

Follow these steps to run TaskAsync locally:

#### Prerequisites

- Java 17
- Maven 3.8+
- Docker
- Git

#### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/AbdelrahmanRagheb/WebDevPortfolio.git
   cd WebDevPortfolio/TaskAsync
   ```

2. Set up Docker containers:
   - MySQL for user-service:
     ```bash
     docker run -p 3001:3306 --name user-service -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=user-service -d mysql
     ```
   - MySQL for task-service:
     ```bash
     docker run -p 3000:3306 --name task-service -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=task-service -d mysql
     ```
   - RabbitMQ:
     ```bash
     docker run -d -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4.0-management
     ```
   - Keycloak:
     ```bash
     docker run -d -p 7080:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.1.3 start-dev
     ```

3. Configure the application:
   - Edit `user-service/src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3001/user-service
     spring.datasource.username=root
     spring.datasource.password=root
     spring.jpa.hibernate.ddl-auto=update
     spring.rabbitmq.host=localhost
     spring.rabbitmq.port=5672
     spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:7080/realms/master
     ```
   - Configure other services similarly, ensuring Eureka and Config Server settings.

4. Build all services:
   ```bash
   cd config-server && mvn clean install
   cd ../eureka-server && mvn clean install
   cd ../gateway-server && mvn clean install
   cd ../notification-service && mvn clean install
   cd ../task-service && mvn clean install
   cd ../user-service && mvn clean install
   ```

5. Run the services (in separate terminals):
   - Start Config Server:
     ```bash
     cd config-server && mvn spring-boot:run
     ```
   - Start Eureka Server:
     ```bash
     cd eureka-server && mvn spring-boot:run
     ```
   - Start Gateway Server:
     ```bash
     cd gateway-server && mvn spring-boot:run
     ```
   - Start Notification, Task, and User Services:
     ```bash
     cd notification-service && mvn spring-boot:run
     cd ../task-service && mvn spring-boot:run
     cd ../user-service && mvn spring-boot:run
     ```

6. Run tests for a specific service (e.g., user-service):
   ```bash
   cd user-service && mvn test
   ```

#### Keycloak Setup

- Access `http://localhost:7080` and log in with `admin/admin`.
- Create a realm (e.g., `taskasync`) and configure an OAuth2 client.
- Update `application.properties` with the realm’s issuer URI if needed.

---

### Upcoming Enhancements

I’m eager to improve TaskAsync by adding:
- Swagger for API documentation
- Kubernetes for orchestration
- GitHub Actions for CI/CD
- Monitoring with Prometheus and Grafana

---

*This project is part of my [WebDevPortfolio](https://github.com/AbdelrahmanRagheb/WebDevPortfolio), showcasing my backend development skills. Reach out for feedback or collaboration:  
GitHub: [AbdelrahmanRagheb](https://github.com/AbdelrahmanRagheb)  
LinkedIn: [abdelrahman-fathy-93303833a](https://www.linkedin.com/in/abdelrahman-fathy-93303833a)  
Email: [abdelrahman.ragheb01@gmail.com]*

