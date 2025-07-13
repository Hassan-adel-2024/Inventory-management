# Inventory Management System

A comprehensive JavaFX-based inventory management application designed for efficient product tracking, stock management, and purchase order processing. Built with modern Java technologies following SOLID principles and industry-standard design patterns.

## Overview

This enterprise-grade inventory management system provides a complete solution for businesses to track products, manage stock levels, and automate reorder processes. The application features a modern JavaFX interface with robust backend architecture designed for scalability and maintainability.

## Key Features

### Core Business Functionality
- **Product Management**: Complete CRUD operations for product catalog with detailed stock tracking
- **Real-time Stock Monitoring**: Live tracking of current inventory levels against configurable reorder thresholds
- **Automated Purchase Orders**: Intelligent restock order generation with supplier integration
- **Bulk Consumption Tracking**: Streamlined processing of large quantity transactions with validation
- **Supplier Relationship Management**: Comprehensive supplier database with product associations
- **Smart Alert System**: Proactive low-stock notifications with customizable threshold settings


## Technology Stack

Java, Version: 17
JavaFX, Version: 17.0.6
Maven, Version: maven-3.9.9
Database, Technology: Microsoft SQL Server, Version: Latest
Testing, Technology: JUnit 5, Version: Latest
Mocking, Technology: Mockito, Version: Latest

## Quick Start Guide

### Prerequisites
- Java 17 or higher
- Maven 3.9.9 or higher
- Microsoft SQL Server (Express or higher)
- IDE with JavaFX support (IntelliJ IDEA, Eclipse, or VS Code)

### Installation Steps

#### 1. Database Setup
```sql

-- Run the provided SQL scripts to create tables
-- (Script available in com/inventoryapp/inventorymanagement/db directory)
```

#### 2. Configure Database Connection
Update `src/main/java/com/inventoryapp/inventorymanagement/db/DatabaseConfig.java`:

```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=InventoryManagement;encrypt=false";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

#### 3. Build & Run
```bash

# Build the project
mvn clean install

# Run the application
mvn javafx:run

# Alternative: Run from IDE
# Main class: com.inventoryapp.inventorymanagement.InventoryManagement
```

## Testing & Quality Assurance

### Running Tests
```bash
# Execute all tests
mvn test

```

### Test Coverage
Our comprehensive test suite includes:
- **Service Layer Testing**: Business logic validation with mocked dependencies
- **Factory Pattern Testing**: Dependency injection and object creation
- **Data Access Layer**: Repository pattern implementation testing

## Architecture & Design

### Design Patterns Implementation
- **Factory Pattern**: Centralized service and DAO instantiation with dependency injection
- **Repository Pattern**: Clean data access abstraction layer
- **Service Layer Pattern**: Business logic separation and encapsulation
- **Singleton Pattern**: Efficient factory instance management

### SOLID Principles Adherence
- **Single Responsibility**: Each class has one clearly defined purpose
- **Open/Closed**: Extensible architecture without modifying existing code
- **Liskov Substitution**: Proper interface implementation and substitutability
- **Interface Segregation**: Focused interfaces with minimal dependencies
- **Dependency Inversion**: High-level modules independent of low-level implementations

### Project Structure
```
src/main/java/com/inventoryapp/inventorymanagement/
├── beanfactory/          # Dependency injection & service factories
├── dao/                  # Data access objects & interfaces
│   └── impl/             # Concrete DAO implementations
├── model/                # Domain entities & business objects
├── service/              # Business logic services & interfaces
│   └── impl/             # Service implementations
├── ui/                   # JavaFX UI components & controllers
└── db/                   # Database configuration & utilities
```

