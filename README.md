# TownSq-Backend-Tech-Challenge: A Secure and Well-Crafted RESTful API

This project implements a robust and secure RESTful API built with Java and Spring Boot for managing purchase orders and payments. It prioritizes both security and code quality for a dependable backend solution.

## Key Features:

* **Purchase Order Management:** Efficiently create, retrieve, update, and delete purchase orders.
* **Payment Processing:** Securely handle payments associated with purchase orders. Only users with the `ACCOUNT_MANAGER` role can process payments, while all users can retrieve payment information.
* **User Management:** Create user accounts, retrieve your own user information, and update specific details like username and role.
* **Security:** Enforces robust security measures to protect sensitive data.

## Getting Started

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/fabiokaz/TownSq-Backend-Tech-Challenge.git
   
2. **Navigate to the Project Directory:**
   cd TownSq-Backend-Tech-Challenge

3. **Install the required dependencies:**
   mvn install

4. **Build the project:**
   mvn clean package

5. **Run the application:**
   java -jar target/townsq-backend-tech-challenge-0.0.1-SNAPSHOT.jar

## API Documentation
The API documentation can be accessed using the following URLs:

* **Swagger UI: http://localhost:8080/swagger-ui/index.html**
* **API Documentation (OpenAPI 3.0): http://localhost:8080/v3/api-docs**

## Project Setup

This project utilizes the following technologies for its development:

* **Java 17**
* **Spring Boot 3**
* **API REST**
* **H2**
* **JWT**

## Project Creation

This project was created using the following tools:

   **Version Control**: Git is used for version control.
   **Integrated Development Environment (IDE)**: IntelliJ IDEA for coding and debugging.

# License

   **This project is distributed under the Apache License Version 2.0 (see LICENSE file for details).**






