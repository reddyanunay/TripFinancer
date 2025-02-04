# TripFinancer

TripFinancer is a web application built using Angular and Spring Boot, designed to help users manage and analyze trip expenses. It is similar to Splitwise but specifically made for handling trip-related expenses. Users can create trips, add people, log bills, and get a detailed analysis of who owes whom, how much each person spent, and even a comparative analysis of expenses by category.

## Features:
- **Create Trips**: Users can create a trip and add people.
- **Add Bills**: Users can log bills and specify who paid for each.
- **Expense Analysis**: After adding all the bills, the application provides a comprehensive analysis, including:
  - Who owes whom and how much.
  - How much each person spent overall.
  - Comparative analysis on how much each person spent across different categories (e.g., food, accommodation, etc.).

## Prerequisites:
Before running the application, ensure that the following are installed:
- **MySQL**: You will need MySQL installed on your system.
- **Angular**: Make sure you have Angular CLI installed.
- **Spring Boot**: Install Java and Spring Boot.

## Setup Instructions:

## Frontend (Angular):

1. Install the necessary packages:

    ```bash
    npm install
    ```

2. Run the Angular application:

    ```bash
    ng serve --open
    ```

    This will start the frontend application and open it in your default browser.

## Backend (Spring Boot):

1. Go to the backend folder.

2. Open the `application.properties` file and configure your MySQL username and password:

    ```properties
    spring.datasource.username=<your_mysql_username>
    spring.datasource.password=<your_mysql_password>
    ```

3. Run the Spring Boot application:

    ```bash
    mvn spring-boot:run
    ```

    This will start the backend server.

## Database Setup:

1. Make sure MySQL is installed and running.

2. Create a new database for the application:

    ```sql
    CREATE DATABASE tripfinancer;
    ```

    The backend will automatically create tables when it runs for the first time.

# License:

This project is licensed under the MIT License - see the LICENSE file for details.