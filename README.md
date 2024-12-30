# ShopEase - E-commerce Web Application

Welcome to the **ShopEase** repository! ShopEase is a dynamic, fully-featured e-commerce platform built using **Spring Boot** and **Java**. It allows users to browse, purchase, and manage products with a user-friendly interface. ShopEase also includes features for product management, user accounts, and order tracking.

## Features

- **User Management**: Users can register, log in, and manage their profiles.
- **Product Catalog**: Browse through a wide range of products across different categories and brands.
- **Shopping Cart & Checkout**: Add products to your cart, view the cart, and complete purchases.
- **Order Management**: View past orders and their details.
- **Wishlist**: Save favorite products for future purchase.
- **Product Reviews**: Users can leave reviews and ratings on products.
- **Admin Panel**: Admins can manage users, products, and orders.

## Prerequisites

Before getting started, make sure you have the following tools installed:

- **Java 11+**: Required for running the backend services.
- **Spring Boot**: The framework used for building the backend.
- **Maven**: Dependency management and build automation.
- **MySQL/PostgreSQL**: Database to store user and product data (can be configured in `application.properties`).
- **Git**: Version control tool to manage the source code.
- **IDE**: Such as IntelliJ IDEA, Eclipse, or Visual Studio Code.

## Getting Started

Follow these steps to set up ShopEase locally:

### Clone the Repository

```bash
git clone https://github.com/your-username/ShopEase.git
cd ShopEase

### Set Up the Database

1. Create a new database in your SQL server (MySQL/PostgreSQL).
2. Update the `application.properties` file with your database credentials:
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/shopEaseDB
spring.datasource.username=root
spring.datasource.password=your-password

### Install Dependencies
Make sure you have Maven installed on your machine. Run the following command to install the necessary dependencies:
```bash
mvn install
### Run the Application
To run the application, use the following command:
```bash
mvn spring-boot:run
The application will be accessible at `http://localhost:8080`.
