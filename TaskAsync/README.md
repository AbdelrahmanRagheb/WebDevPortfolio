```markdown
# TaskAsync - Microservices Task Management Backend

Welcome to **TaskAsync**, a microservices-based backend for a task management system built using **Spring Boot** and **Java**. This project showcases my skills in developing secure, resilient, and scalable backend services with modern architectural patterns. TaskAsync handles user authentication, task assignments, and real-time notifications, demonstrating my ability to build robust microservices as a junior developer.

### Project Overview

TaskAsync is designed to manage tasks and notifications in a distributed system. It features secure authentication with **Keycloak**, event-driven communication with **RabbitMQ**, and containerized deployment with **Docker**. The project incorporates the **circuit breaker pattern** for resilience and comprehensive testing to ensure reliability.

- **Secure Authentication**: Integrates OAuth2 and OpenID Connect via Keycloak for user login and API protection.
- **Task Management**: Supports task creation, assignment, and status tracking (To-Do, In Progress, Done).
- **Real-Time Notifications**: Delivers task and comment notifications using RabbitMQ and JSON metadata.
- **Resilience**: Implements circuit breakers to handle service failures gracefully.
- **Testing**: Includes unit tests with JUnit and H2 for 100% coverage of core services.

### Technologies Used
- **Spring Boot** for RESTful APIs and microservices
- **Spring Security** with OAuth2/OpenID Connect for authentication
- **JPA / Hibernate** for database operations
- **MySQL** for production data storage
- **H2 Database** for testing
- **RabbitMQ** for event-driven messaging
- **Keycloak** for identity and access management
- **Docker** for containerized databases and services
- **Spring Cloud Circuit Breaker** for resilience
- **JUnit** and **Mockito** for unit testing
- **Maven** for build management

### Project Structure
The project is organized to highlight microservices development:
- **REST API Design**: Endpoints for users, tasks, and notifications
- **Database Management**: MySQL and H2 with JPA
- **Authentication**: Keycloak with OAuth2 tokens
- **Event-Driven Architecture**: RabbitMQ for async notifications
- **Error Handling**: Circuit breakers for fault tolerance
- **Testing**: Unit tests for services like `INotificationServiceImpl`

```
TaskAsync/
├── src/
│   ├── main/
│   │   ├── java/com/taskasync/userservice/
│   │   │   ├── entity/           # Entities (User, UserNotification)
│   │   │   ├── repository/       # JPA repositories
│   │   │   ├── service/          # Services (e.g., INotificationServiceImpl)
│   │   │   └── dto/             # DTOs
│   │   └── resources/            # application.properties
│   └── test/
│       └── java/com/taskasync/userservice/
│           └── service/impl/     # Tests (e.g., INotificationServiceImplTest)
├── pom.xml                       # Maven dependencies
└── README.md                     # This file
```

### Getting Started

To run TaskAsync locally, follow these steps:

#### Prerequisites
Ensure you have the following installed:
- **Java 17**
- **Maven 3.8+**
- **Docker**
- **Git**

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
   - Update `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3001/user-service
     spring.datasource.username=root
     spring.datasource.password=root
     spring.jpa.hibernate.ddl-auto=update
     spring.rabbitmq.host=localhost
     spring.rabbitmq.port=5672
     spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:7080/realms/master
     ```

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

6. Run tests:
   ```bash
   mvn test
   ```

#### Keycloak Setup
- Visit `http://localhost:7080` and log in with `admin/admin`.
- Create a realm (e.g., `taskasync`) and a client for OAuth2.
- Update the issuer URI in `application.properties` if needed.

### Upcoming Enhancements
I’m excited to expand TaskAsync with:
- Swagger for API documentation
- Kubernetes for container orchestration
- GitHub Actions for CI/CD
- Monitoring with Prometheus and Grafana

---

*This project is part of my [WebDevPortfolio](https://github.com/AbdelrahmanRagheb/WebDevPortfolio), showcasing my backend development skills.
I’d love to hear your feedback or discuss collaboration:
GitHub: AbdelrahmanRagheb
LinkedIn: [https://www.linkedin.com/in/abdelrahman-fathy-93303833a]
Email: [abdelrahman.ragheb01@gmail.com]
```
---


