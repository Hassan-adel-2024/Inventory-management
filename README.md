# Inventory Management System

A comprehensive JavaFX-based inventory management application designed for
efficient product tracking, stock management, and purchase order processing.
Built with modern Java technologies and following SOLID principles and design patterns.

## 🚀 Features

### Core Functionality
- **Product Management**: Add, edit, delete, and track products with stock levels
- **Stock Monitoring**: Real-time tracking of current stock vs. reorder thresholds
- **Purchase Order Management**: Automated generation and management of restock orders
- **Product Consumption**: Bulk consumption tracking with validation

## 🛠️ Technology Stack

- **Java**: 17
- **JavaFX**: 17.0.6 (UI Framework)
- **Maven**: Build and dependency management
- **SQL Server**: Database (Microsoft SQL Server)
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for testing
- **Maven 3.6** or higher installed

## 🗄️ Database Setup

-- Create database
CREATE DATABASE InventoryManagement;

-- Run the provided SQL scripts to create tables
-- (Scripts available in com/inventoryapp/inventorymanagement/db directory)

### 3. Configure Database Connection
Update the database configuration in `src/main/java/com/inventoryapp/inventorymanagement/db/DatabaseConfig.java`:

```java
// Update these values according to your SQL Server setup
// src/main/java/com/inventoryapp/inventorymanagement/db/DatabaseConfig.java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=InventoryManagement;encrypt=true;trustServerCertificate=true";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn javafx:run
```

### 4. Alternative: Run from IDE
- Open the project in your preferred IDE
- Run the main class: `com.inventoryapp.inventorymanagement.InventoryManagement` 

## 🧪 Running Tests

### Run All Tests
```bash
mvn test
```


### Test Coverage
The project includes simple unit tests covering:
- **Service Layer**: Business logic testing with mocked dependencies
- **Factory Pattern**: Dependency injection testing


## 🏗️ Architecture

### Design Patterns
- **Factory Pattern**: Service and DAO instantiation
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **Singleton Pattern**: Factory instances

### SOLID Principles
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Interfaces can be substituted with implementations
- **Interface Segregation**: Clients depend only on necessary interfaces
- **Dependency Inversion**: High-level modules don't depend on low-level modules

### Package Structure
```
src/main/java/com/inventoryapp/inventorymanagement/
├── beanfactory/          # Dependency injection factories
├── dao/                  # Data access objects
│   └── impl/            # DAO implementations
├── model/               # Domain entities
├── service/             # Business logic services
│   └── impl/           # Service implementations
├── ui/                  # JavaFX UI components
└── db/                  # Database configuration
```



