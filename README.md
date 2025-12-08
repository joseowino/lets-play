# Spring Boot CRUD API with MongoDB
[joseowino](https://github.com/joseowino)
A secure RESTful API built with Spring Boot and MongoDB, featuring user management, product management, JWT authentication, and role-based access control.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Security Features](#security-features)
- [Testing](#testing)
- [Project Structure](#project-structure)

## ✨ Features

- **User Management**: Complete CRUD operations for user accounts
- **Product Management**: Full CRUD functionality for products
- **JWT Authentication**: Token-based authentication system
- **Role-Based Access Control**: Admin and User roles with different permissions
- **Security**: Password hashing, input validation, and MongoDB injection prevention
- **Error Handling**: Comprehensive exception handling with appropriate HTTP status codes
- **CORS Configuration**: Cross-Origin Resource Sharing policies
- **Rate Limiting**: Protection against brute force attacks

## 🛠 Technology Stack

- **Java**: 17 or higher
- **Spring Boot**: 3.x
- **Spring Security**: JWT-based authentication
- **MongoDB**: NoSQL database
- **Maven**: Dependency management
- **Lombok**: Reducing boilerplate code
- **BCrypt**: Password hashing

## 📦 Prerequisites

Before running this application, ensure you have the following installed:

- Java JDK 17 or higher
- Maven 3.6+
- MongoDB 4.4+ (running locally or accessible remotely)
- Git (for cloning the repository)

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-boot-crud-api
```

### 2. Configure MongoDB

Update the MongoDB connection settings in `src/main/resources/application.properties`:

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/crud_db
spring.data.mongodb.database=crud_db

# JWT Configuration
jwt.secret=your-secret-key-here-make-it-long-and-secure
jwt.expiration=86400000

# Server Configuration
server.port=8080
server.ssl.enabled=false

# Logging
logging.level.com.yourpackage=DEBUG
```

### 3. Install Dependencies

```bash
mvn clean install
```

## 🏃 Running the Application

### Option 1: Using Maven

```bash
mvn spring-boot:run
```

### Option 2: Using Java

```bash
mvn clean package
java -jar target/crud-api-0.0.1-SNAPSHOT.jar
```

### Option 3: Using the Provided Script

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

**Windows:**
```bash
run.bat
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### User Management Endpoints

#### Get All Users (Admin Only)
```http
GET /api/users
Authorization: Bearer <token>
```

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer <token>
```

#### Update User (Admin or Self)
```http
PUT /api/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "john_updated",
  "email": "john.new@example.com"
}
```

#### Delete User (Admin Only)
```http
DELETE /api/users/{id}
Authorization: Bearer <token>
```

### Product Management Endpoints

#### Get All Products (Public)
```http
GET /api/products
```

#### Get Product by ID (Public)
```http
GET /api/products/{id}
```

#### Create Product (Authenticated Users)
```http
POST /api/products
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "category": "Electronics",
  "stock": 50
}
```

#### Update Product (Admin Only)
```http
PUT /api/products/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Laptop",
  "price": 899.99,
  "stock": 45
}
```

#### Delete Product (Admin Only)
```http
DELETE /api/products/{id}
Authorization: Bearer <token>
```

## 🔒 Security Features

### Implemented Security Measures

1. **Password Security**
   - Passwords are hashed using BCrypt with salt
   - Minimum password strength requirements enforced
   - Passwords never returned in API responses

2. **Authentication & Authorization**
   - JWT token-based authentication
   - Role-based access control (ADMIN, USER)
   - Token expiration and validation

3. **Input Validation**
   - Request body validation using Bean Validation
   - MongoDB injection prevention through parameterized queries
   - XSS protection through input sanitization

4. **Error Handling**
   - Global exception handler preventing 5XX errors
   - Appropriate HTTP status codes for different scenarios
   - Sensitive information protected in error responses

5. **CORS Configuration**
   - Configurable allowed origins
   - Restricted HTTP methods
   - Credential support when needed

6. **Rate Limiting**
   - Protection against brute force attacks
   - Configurable limits per endpoint
   - IP-based tracking

7. **HTTPS Support**
   - SSL/TLS configuration ready
   - Secure data transmission

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Manual Testing with Postman

1. Import the provided Postman collection (`postman_collection.json`)
2. Set up environment variables:
   - `base_url`: http://localhost:8080
   - `token`: (will be set automatically after login)
3. Run the collection to test all endpoints

### Test User Accounts

The application seeds the following test accounts:

**Admin User:**
- Email: `admin@example.com`
- Password: `Admin123!`
- Role: ADMIN

**Regular User:**
- Email: `user@example.com`
- Password: `User123!`
- Role: USER

## 📁 Project Structure

```
src/main/java/com/yourpackage/
├── config/
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── RateLimitConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   └── ProductController.java
├── model/
│   ├── User.java
│   ├── Product.java
│   └── Role.java
├── repository/
│   ├── UserRepository.java
│   └── ProductRepository.java
├── service/
│   ├── UserService.java
│   ├── ProductService.java
│   └── AuthService.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── dto/
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   └── JwtResponse.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
└── Application.java
```

## 🔧 Configuration Options

### Application Properties

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/crud_db

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# Rate Limiting
rate.limit.requests=100
rate.limit.window=60000

# CORS
cors.allowed.origins=http://localhost:3000,http://localhost:4200
```

## 🐛 Troubleshooting

### MongoDB Connection Issues
- Ensure MongoDB is running: `mongod --version`
- Check MongoDB URI in application.properties
- Verify network connectivity

### Authentication Failures
- Verify JWT secret is properly configured
- Check token expiration time
- Ensure Authorization header format: `Bearer <token>`

### 403 Forbidden Errors
- Verify user role permissions
- Check if token is valid and not expired
- Ensure proper CORS configuration

## 📝 License

This project is licensed under the MIT License.

## 👥 Contributors

[joseowino](https://github.com/joseowino)

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Email: joeoowino@gmail.com.com

---

**Note**: Remember to change default configurations (JWT secret, database credentials, etc.) before deploying to production.