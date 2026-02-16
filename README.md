# Spring Boot CRUD API with MongoDB

[joseowino](https://github.com/joseowino)

A secure RESTful API built with Spring Boot and MongoDB, featuring user management, product management, JWT authentication, and role-based access control.

## 📋 Table of Contents

- [Overview](#overview)
- [Quick Start](#quick-start)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Data Model](#data-model)
- [Permissions Matrix](#permissions-matrix)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Error Handling](#error-handling)
- [Security Features](#security-features)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🎯 Overview

Let's Play is a RESTful CRUD API designed for a small e-commerce-like platform. It demonstrates backend development best practices including secure coding, error handling, REST design principles, and role-based access management.

**Key Concepts:**
- Users can create and manage their own products
- Admins have full control over all users and products
- Public access to view products without authentication
- Secure JWT-based authentication and authorization

## 🚀 Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/joseowino/spring-boot-crud-api.git
cd spring-boot-crud-api

# 2. Ensure MongoDB is running
mongod --version

# 3. Configure application (optional - defaults work for local development)
# Edit src/main/resources/application.properties if needed

# 4. Run the application
mvn spring-boot:run

# 5. Test the API
curl http://localhost:8080/api/products
```

**Default test accounts:**
- Admin: `admin@example.com` / `Admin123!`
- User: `user@example.com` / `User123!`

## ✨ Features

- **User Management**: Complete CRUD operations for user accounts
- **Product Management**: Full CRUD functionality with ownership tracking
- **User-Product Relationship**: One-to-many (each user owns multiple products)
- **JWT Authentication**: Token-based authentication system
- **Role-Based Access Control**: Admin and User roles with different permissions
- **Ownership Enforcement**: Users can only modify their own products
- **Security**: Password hashing (BCrypt), input validation, MongoDB injection prevention
- **Error Handling**: Comprehensive exception handling with appropriate HTTP status codes
- **CORS Configuration**: Cross-Origin Resource Sharing policies
- **Public API Access**: Product listings available without authentication

## 🛠 Technology Stack

- **Java**: 17 or higher
- **Spring Boot**: 3.x
- **Spring Security**: JWT-based authentication
- **MongoDB**: NoSQL database
- **Maven**: Dependency management
- **Lombok**: Reducing boilerplate code
- **BCrypt**: Password hashing
- **Jakarta Validation**: Input validation

## 📊 Data Model

The application uses two main entities with a one-to-many relationship:

```
User (1) ────── owns ────── (*) Product
```

### User Entity
```json
{
  "id": "507f1f77bcf86cd799439011",
  "username": "john_doe",
  "email": "john@example.com",
  "password": "hashed_password_here",
  "role": "USER"
}
```

### Product Entity
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "category": "Electronics",
  "stock": 50,
  "userId": "507f1f77bcf86cd799439011"
}
```

**Key Relationships:**
- Each product belongs to exactly one user (via `userId`)
- Users can own multiple products
- Only the product owner or an admin can modify/delete a product
- The `userId` field is automatically set from the authenticated user's token

## 🔑 Permissions Matrix

| Endpoint | Public | User | Admin |
|----------|--------|------|-------|
| **Products** |
| GET /api/products | ✅ | ✅ | ✅ |
| GET /api/products/{id} | ✅ | ✅ | ✅ |
| POST /api/products | ❌ | ✅ (creates as owner) | ✅ |
| PUT /api/products/{id} | ❌ | ✅ (own only) | ✅ (all) |
| DELETE /api/products/{id} | ❌ | ✅ (own only) | ✅ (all) |
| **Users** |
| POST /api/auth/register | ✅ | ✅ | ✅ |
| POST /api/auth/login | ✅ | ✅ | ✅ |
| GET /api/users | ❌ | ❌ | ✅ |
| GET /api/users/{id} | ❌ | ✅ (self) | ✅ (all) |
| PUT /api/users/{id} | ❌ | ✅ (self) | ✅ (all) |
| DELETE /api/users/{id} | ❌ | ❌ | ✅ |

**Legend:**
- ✅ = Allowed
- ❌ = Forbidden (returns 401/403)
- "own only" = Can only access resources they created
- "self" = Can only access their own user account

## 📦 Prerequisites

Before running this application, ensure you have:

- **Java JDK 17 or higher** ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **MongoDB 4.4+** running locally or accessible remotely ([Download](https://www.mongodb.com/try/download/community))
- **Git** ([Download](https://git-scm.com/downloads))

**Verify installations:**
```bash
java -version    # Should show Java 17+
mvn -version     # Should show Maven 3.6+
mongod --version # Should show MongoDB 4.4+
```

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/joseowino/spring-boot-crud-api.git
cd spring-boot-crud-api
```

### 2. Start MongoDB

**Local MongoDB:**
```bash
# Start MongoDB service
mongod

# Or using brew (macOS)
brew services start mongodb-community
```

**Docker MongoDB:**
```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### 3. Configure Application

Update `src/main/resources/application.properties` if needed:

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/letsplay_db
spring.data.mongodb.database=letsplay_db

# JWT Configuration
jwt.secret=your-256-bit-secret-key-change-this-in-production-make-it-very-long-and-random
jwt.expiration=86400000

# Server Configuration
server.port=8080

# Logging
logging.level.com.letsplay=DEBUG
logging.level.org.springframework.security=DEBUG
```

**⚠️ Security Warning:** Change the `jwt.secret` before deploying to production!

### 4. Install Dependencies

```bash
mvn clean install
```

## 🏃 Running the Application

### Option 1: Using Maven (Recommended for Development)

```bash
mvn spring-boot:run
```

### Option 2: Using Java JAR

```bash
mvn clean package
java -jar target/letsplay-api-0.0.1-SNAPSHOT.jar
```

### Option 3: Using Docker

```bash
# Build Docker image
docker build -t letsplay-api .

# Run container
docker run -p 8080:8080 --name letsplay-api letsplay-api
```

### Option 4: Using the Provided Scripts

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

**Verify it's running:**
```bash
curl http://localhost:8080/api/products
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

---

### Authentication Endpoints

#### Register New User
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

**Response (201 Created):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Validation Rules:**
- Username: 3-20 characters, alphanumeric with underscores
- Email: Valid email format
- Password: Min 8 characters, at least 1 uppercase, 1 lowercase, 1 digit, 1 special char
- Role: Either "USER" or "ADMIN" (defaults to "USER")

---

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MDdmMWY3N2JjZjg2Y2Q3OTk0MzkwMTEiLCJlbWFpbCI6ImpvaG5AZXhhbXBsZS5jb20iLCJyb2xlIjoiVVNFUiIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxNTE2MzI1NDIyfQ.4Adcj0mJ_5KFqDJJnB5T8H7fXwLfZqYJQm0X7pXq9aU",
  "type": "Bearer",
  "userId": "507f1f77bcf86cd799439011",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Usage:**
Include the token in subsequent requests:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### Product Management Endpoints

#### Get All Products (Public)
```http
GET /api/products
```

**Response (200 OK):**
```json
[
  {
    "id": "507f1f77bcf86cd799439012",
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "category": "Electronics",
    "stock": 50,
    "userId": "507f1f77bcf86cd799439011",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
]
```

---

#### Get Product by ID (Public)
```http
GET /api/products/507f1f77bcf86cd799439012
```

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "category": "Electronics",
  "stock": 50,
  "userId": "507f1f77bcf86cd799439011",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

---

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

**Note:** The `userId` is automatically extracted from the JWT token. You cannot manually set it.

**Response (201 Created):**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "category": "Electronics",
  "stock": 50,
  "userId": "507f1f77bcf86cd799439011",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

**Validation Rules:**
- Name: Required, 1-100 characters
- Description: Optional, max 500 characters
- Price: Required, must be positive
- Category: Required, 1-50 characters
- Stock: Required, must be non-negative

---

#### Update Product (Owner or Admin)
```http
PUT /api/products/507f1f77bcf86cd799439012
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Laptop",
  "description": "New description",
  "price": 899.99,
  "stock": 45
}
```

**Authorization:**
- Regular users can only update their own products
- Admins can update any product

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "name": "Updated Laptop",
  "description": "New description",
  "price": 899.99,
  "category": "Electronics",
  "stock": 45,
  "userId": "507f1f77bcf86cd799439011",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T11:45:00"
}
```

---

#### Delete Product (Owner or Admin)
```http
DELETE /api/products/507f1f77bcf86cd799439012
Authorization: Bearer <token>
```

**Authorization:**
- Regular users can only delete their own products
- Admins can delete any product

**Response (204 No Content):**
```
(empty body)
```

---

### User Management Endpoints

#### Get All Users (Admin Only)
```http
GET /api/users
Authorization: Bearer <admin-token>
```

**Response (200 OK):**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2025-01-15T10:00:00"
  }
]
```

**Note:** Password field is never returned in responses.

---

#### Get User by ID (Self or Admin)
```http
GET /api/users/507f1f77bcf86cd799439011
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2025-01-15T10:00:00"
}
```

---

#### Update User (Self or Admin)
```http
PUT /api/users/507f1f77bcf86cd799439011
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "john_updated",
  "email": "john.new@example.com"
}
```

**Authorization:**
- Users can only update their own account
- Admins can update any account
- Regular users cannot change roles (admin-only operation)

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "username": "john_updated",
  "email": "john.new@example.com",
  "role": "USER",
  "createdAt": "2025-01-15T10:00:00",
  "updatedAt": "2025-01-15T12:00:00"
}
```

---

#### Delete User (Admin Only)
```http
DELETE /api/users/507f1f77bcf86cd799439011
Authorization: Bearer <admin-token>
```

**Response (204 No Content):**
```
(empty body)
```

**Note:** Deleting a user also deletes all their products (cascade delete).

---

## ❌ Error Handling

The API uses standard HTTP status codes and returns consistent error responses.

### Error Response Format

```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired JWT token",
  "path": "/api/products"
}
```

### Common HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Successful GET, PUT request |
| 201 | Created | Successful POST request |
| 204 | No Content | Successful DELETE request |
| 400 | Bad Request | Invalid input, validation failure |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate email/username |
| 500 | Internal Server Error | Should never happen (proper error handling) |

### Error Examples

#### 400 Bad Request - Validation Error
```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    "password: must be at least 8 characters",
    "email: must be a valid email address"
  ],
  "path": "/api/auth/register"
}
```

#### 401 Unauthorized - Invalid Token
```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired JWT token",
  "path": "/api/products"
}
```

#### 403 Forbidden - Insufficient Permissions
```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to modify this product",
  "path": "/api/products/507f1f77bcf86cd799439012"
}
```

#### 404 Not Found - Resource Missing
```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 507f1f77bcf86cd799439012",
  "path": "/api/products/507f1f77bcf86cd799439012"
}
```

#### 409 Conflict - Duplicate Entry
```json
{
  "timestamp": "2025-01-15T10:30:00.000+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "User already exists with email: john@example.com",
  "path": "/api/auth/register"
}
```

### Global Exception Handler

The application uses `@RestControllerAdvice` to handle all exceptions globally:

- **No 5XX errors** are returned to clients (all exceptions are caught and handled)
- **Meaningful error messages** for debugging
- **Sensitive information** (stack traces, internal paths) is never exposed
- **Consistent format** across all error responses

---

## 🔒 Security Features

### 1. Password Security

**Implementation:**
- Passwords are hashed using **BCrypt** with automatic salt generation
- Minimum strength requirements enforced via validation
- Passwords **never** appear in API responses or logs

**Password Requirements:**
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 lowercase letter
- At least 1 digit
- At least 1 special character (!@#$%^&*)

**Example:**
```java
// BCrypt hashing (handled automatically)
String rawPassword = "SecurePass123!";
String hashedPassword = passwordEncoder.encode(rawPassword);
// Result: $2a$10$N9qo8uLOickgx2ZMRZoMye...
```

---

### 2. Authentication & Authorization

**JWT Token-Based Authentication:**
- Stateless authentication using JSON Web Tokens
- Token contains user ID, email, and role
- Default expiration: 24 hours (configurable)

**Token Structure:**
```
Header.Payload.Signature
```

**Token Payload:**
```json
{
  "sub": "507f1f77bcf86cd799439011",
  "email": "john@example.com",
  "role": "USER",
  "iat": 1516239022,
  "exp": 1516325422
}
```

**Role-Based Access Control:**
- `ADMIN`: Full access to all resources
- `USER`: Limited access to own resources

---

### 3. Input Validation & Sanitization

**Bean Validation:**
```java
@NotBlank(message = "Name is required")
@Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
private String name;

@Email(message = "Must be a valid email")
private String email;

@Positive(message = "Price must be positive")
private Double price;
```

**MongoDB Injection Prevention:**
- All queries use parameterized MongoDB queries (no string concatenation)
- Input sanitization removes special MongoDB operators (`$`, `.`)
- Query validation through Spring Data MongoDB repositories

**XSS Protection:**
- Input escaping for special characters
- Content-Type validation
- Output encoding for JSON responses

---

### 4. CORS Configuration

**Default CORS Policy:**
```properties
cors.allowed.origins=http://localhost:3000,http://localhost:4200
cors.allowed.methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed.headers=*
cors.allow.credentials=true
```

**Configuration in Code:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowCredentials(true);
    return source;
}
```

---

### 5. HTTPS Support (Production-Ready)

**Enable HTTPS:**
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

**Generate Self-Signed Certificate (Development):**
```bash
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

**Note:** For production, use certificates from a trusted CA (Let's Encrypt, DigiCert, etc.)

---

### 6. Security Headers

Automatically applied by Spring Security:

```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
```

---

### 7. Additional Security Measures

- **Token Expiration**: Automatic token invalidation after 24 hours
- **Sensitive Data Exclusion**: `@JsonIgnore` on password fields
- **Audit Logging**: Track all authentication attempts
- **Request Throttling**: Rate limiting on authentication endpoints (optional)
- **Database Security**: MongoDB authentication and authorization enabled in production

---

## 🧪 Testing

### Test User Accounts

The application automatically seeds test accounts on startup:

| Email | Password | Role |
|-------|----------|------|
| `admin@example.com` | `Admin123!` | ADMIN |
| `user@example.com` | `User123!` | USER |

### Manual Testing with cURL

#### 1. Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!",
    "role": "USER"
  }'
```

#### 2. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

Save the token from the response.

#### 3. Create a Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "Test Product",
    "description": "A test product",
    "price": 99.99,
    "category": "Testing",
    "stock": 10
  }'
```

#### 4. Get All Products (No Auth Required)
```bash
curl http://localhost:8080/api/products
```

#### 5. Update a Product (Owner Only)
```bash
curl -X PUT http://localhost:8080/api/products/PRODUCT_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "Updated Product",
    "price": 79.99
  }'
```

#### 6. Try to Update Someone Else's Product (Should Fail)
```bash
curl -X PUT http://localhost:8080/api/products/OTHER_USER_PRODUCT_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "Hacked Product"
  }'
```

Expected: 403 Forbidden

---

### Testing with Postman

#### Import Collection

1. Download the [Postman Collection](./postman_collection.json)
2. Import into Postman: `File → Import → Upload Files`
3. Set up environment variables:
   - `base_url`: `http://localhost:8080`
   - `token`: (automatically set after login)

#### Collection Structure

```
📁 Let's Play API
├── 📁 Authentication
│   ├── Register User
│   └── Login
├── 📁 Products
│   ├── Get All Products
│   ├── Get Product by ID
│   ├── Create Product
│   ├── Update Product
│   └── Delete Product
└── 📁 Users
    ├── Get All Users (Admin)
    ├── Get User by ID
    ├── Update User
    └── Delete User (Admin)
```

#### Automated Tests

The collection includes automated tests for:
- ✅ Response status codes
- ✅ Response time < 1000ms
- ✅ JSON schema validation
- ✅ Authorization checks
- ✅ Data integrity

Run all tests: `Collections → Run Collection`

---

### Unit Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run with coverage
mvn test jacoco:report
```

**Test Coverage Goal:** 80%+

---

### Integration Testing

```bash
# Run integration tests
mvn verify

# Run specific integration test
mvn verify -Dit.test=ProductControllerIT
```

**Integration tests cover:**
- Full HTTP request/response cycle
- Database operations
- Authentication/authorization flows
- Error handling

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/letsplay/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java          # Spring Security configuration
│   │   │   ├── CorsConfig.java              # CORS settings
│   │   │   └── MongoConfig.java             # MongoDB configuration
│   │   ├── controller/
│   │   │   ├── AuthController.java          # /api/auth endpoints
│   │   │   ├── UserController.java          # /api/users endpoints
│   │   │   └── ProductController.java       # /api/products endpoints
│   │   ├── model/
│   │   │   ├── User.java                    # User entity
│   │   │   ├── Product.java                 # Product entity
│   │   │   └── Role.java                    # Role enum (USER, ADMIN)
│   │   ├── repository/
│   │   │   ├── UserRepository.java          # MongoDB user repository
│   │   │   └── ProductRepository.java       # MongoDB product repository
│   │   ├── service/
│   │   │   ├── UserService.java             # User business logic
│   │   │   ├── ProductService.java          # Product business logic
│   │   │   └── AuthService.java             # Authentication logic
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java        # JWT generation/validation
│   │   │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   │   └── UserDetailsServiceImpl.java  # User details for auth
│   │   ├── dto/
│   │   │   ├── LoginRequest.java            # Login request DTO
│   │   │   ├── SignupRequest.java           # Signup request DTO
│   │   │   ├── JwtResponse.java             # Login response DTO
│   │   │   ├── ProductRequest.java          # Product request DTO
│   │   │   └── UserResponse.java            # User response DTO
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java  # Global error handler
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── UnauthorizedException.java
│   │   │   └── ForbiddenException.java
│   │   └── Application.java                 # Main Spring Boot class
│   └── resources/
│       ├── application.properties           # Configuration
│       ├── application-dev.properties       # Dev environment
│       ├── application-prod.properties      # Production environment
│       └── static/                          # Static resources
├── test/
│   ├── java/com/letsplay/
│   │   ├── controller/
│   │   │   ├── ProductControllerTest.java
│   │   │   └── UserControllerTest.java
│   │   ├── service/
│   │   │   ├── ProductServiceTest.java
│   │   │   └── UserServiceTest.java
│   │   └── integration/
│   │       ├── ProductControllerIT.java
│   │       └── AuthenticationIT.java
│   └── resources/
│       └── application-test.properties      # Test configuration
├── pom.xml                                  # Maven dependencies
├── Dockerfile                               # Docker configuration
├── docker-compose.yml                       # Docker Compose setup
└── README.md                                # This file
```

---

## ⚙️ Configuration

### Environment Variables

You can override application.properties using environment variables:

```bash
# Database
export MONGODB_URI=mongodb://localhost:27017/letsplay_db
export MONGODB_DATABASE=letsplay_db

# JWT
export JWT_SECRET=your-secret-key-here
export JWT_EXPIRATION=86400000

# Server
export SERVER_PORT=8080

# Run application
mvn spring-boot:run
```

---

### Configuration Files

#### application.properties (Default)
```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/letsplay_db
spring.data.mongodb.database=letsplay_db

# JWT
jwt.secret=${JWT_SECRET:default-secret-key-change-in-production}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Server
server.port=${SERVER_PORT:8080}

# Logging
logging.level.com.letsplay=INFO
```

#### application-dev.properties (Development)
```properties
# MongoDB (Local)
spring.data.mongodb.uri=mongodb://localhost:27017/letsplay_dev

# Logging (Verbose)
logging.level.com.letsplay=DEBUG
logging.level.org.springframework.security=DEBUG

# Show SQL
logging.level.org.springframework.data.mongodb.core=DEBUG
```

#### application-prod.properties (Production)
```properties
# MongoDB (Remote)
spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=${MONGODB_DATABASE}

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# SSL
server.ssl.enabled=true
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}

# Logging (Minimal)
logging.level.com.letsplay=WARN
```

**Run with specific profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 🚀 Deployment

### Deploy to Heroku

```bash
# Login to Heroku
heroku login

# Create app
heroku create letsplay-api

# Add MongoDB
heroku addons:create mongolab:sandbox

# Set environment variables
heroku config:set JWT_SECRET=your-production-secret-key
heroku config:set SPRING_PROFILES_ACTIVE=prod

# Deploy
git push heroku main

# Open app
heroku open
```

---

### Deploy with Docker

#### Build Image
```bash
docker build -t letsplay-api:latest .
```

#### Run with Docker Compose
```bash
docker-compose up -d
```

**docker-compose.yml:**
```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:latest
    container_name: letsplay-mongodb
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      MONGO_INITDB_DATABASE: letsplay_db

  api:
    build: .
    container_name: letsplay-api
    ports:
      - "8080:8080"
    environment:
      MONGODB_URI: mongodb://mongodb:27017/letsplay_db
      JWT_SECRET: your-secret-key
    depends_on:
      - mongodb

volumes:
  mongodb_data:
```

---

### Deploy to AWS (EC2)

```bash
# SSH into EC2 instance
ssh -i your-key.pem ec2-user@your-ec2-ip

# Install Java 17
sudo yum install java-17-amazon-corretto

# Install MongoDB
# (Follow AWS MongoDB installation guide)

# Clone repository
git clone https://github.com/joseowino/spring-boot-crud-api.git
cd spring-boot-crud-api

# Build
mvn clean package

# Run as service
sudo systemctl start letsplay-api
```

---

## 🐛 Troubleshooting

### MongoDB Connection Issues

**Problem:** `com.mongodb.MongoTimeoutException: Timed out after 30000 ms`

**Solutions:**
```bash
# 1. Check if MongoDB is running
sudo systemctl status mongod

# 2. Start MongoDB
sudo systemctl start mongod

# 3. Check MongoDB is listening
netstat -tuln | grep 27017

# 4. Verify connection string
mongo mongodb://localhost:27017/letsplay_db
```

---

### Authentication Failures

**Problem:** `401 Unauthorized - Invalid or expired token`

**Solutions:**
1. Check token expiration (default: 24 hours)
2. Verify `Authorization` header format: `Bearer <token>`
3. Ensure JWT secret matches in both encoding and decoding
4. Check if token was generated with the same secret

**Debug:**
```bash
# Decode JWT token (without verification)
echo "YOUR_TOKEN" | cut -d'.' -f2 | base64 -d | jq
```

---

### 403 Forbidden Errors

**Problem:** `403 Forbidden - You do not have permission`

**Solutions:**
1. Verify user role (USER vs ADMIN)
2. Check resource ownership (for products)
3. Ensure token contains correct role claim

**Debug:**
```java
// Log user details in controller
@GetMapping("/debug")
public String debug(@AuthenticationPrincipal UserDetails userDetails) {
    return "User: " + userDetails.getUsername() + 
           ", Authorities: " + userDetails.getAuthorities();
}
```

---

### Product Ownership Issues

**Problem:** User can't modify their own product

**Solutions:**
1. Verify `userId` in product matches token's user ID
2. Check if product ownership is set correctly on creation
3. Ensure ownership check in service layer

**Debug:**
```bash
# Get product and check userId
curl http://localhost:8080/api/products/PRODUCT_ID

# Compare with token payload
echo "YOUR_TOKEN" | cut -d'.' -f2 | base64 -d | jq .sub
```

---

### CORS Errors

**Problem:** `No 'Access-Control-Allow-Origin' header`

**Solutions:**
1. Add frontend origin to `cors.allowed.origins`
2. Enable credentials if needed
3. Check preflight OPTIONS requests

**Fix in application.properties:**
```properties
cors.allowed.origins=http://localhost:3000,http://localhost:4200,https://your-frontend.com
```

---

### Port Already in Use

**Problem:** `Port 8080 is already in use`

**Solutions:**
```bash
# 1. Find process using port 8080
lsof -i :8080

# 2. Kill process
kill -9 <PID>

# 3. Or use different port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

---

### Build Failures

**Problem:** `mvn clean install` fails

**Solutions:**
```bash
# 1. Check Java version
java -version  # Should be 17+

# 2. Update Maven
mvn -version

# 3. Clear Maven cache
rm -rf ~/.m2/repository

# 4. Rebuild
mvn clean install -U
```

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### How to Contribute

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Code Standards

- Follow Java naming conventions (camelCase, PascalCase)
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages
- Keep code coverage above 80%

### Pull Request Process

1. Ensure all tests pass (`mvn test`)
2. Update README if needed
3. Add yourself to Contributors section
4. Request review from maintainers

---

## 📄 License

This project is licensed under the **MIT License**.

```
MIT License

Copyright (c) 2025 Jose Owino

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👥 Contributors

- **Jose Owino** - *Initial work* - [joseowino](https://github.com/joseowino)

---

## 📞 Support

For issues, questions, or suggestions:

- **GitHub Issues**: [Create an issue](https://github.com/joseowino/spring-boot-crud-api/issues)
- **Email**: joeoowino@gmail.com
- **Documentation**: [Wiki](https://github.com/joseowino/spring-boot-crud-api/wiki)

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [MongoDB Manual](https://docs.mongodb.com/manual/)
- [JWT Introduction](https://jwt.io/introduction)
- [REST API Best Practices](https://restfulapi.net/)
- [BCrypt Explained](https://auth0.com/blog/hashing-in-action-understanding-bcrypt/)

---

## 🔄 Changelog

### v1.0.0 (2025-01-15)
- ✨ Initial release
- ✅ User management CRUD
- ✅ Product management CRUD
- ✅ JWT authentication
- ✅ Role-based authorization
- ✅ Product ownership enforcement
- ✅ Comprehensive error handling
- ✅ MongoDB integration
- ✅ BCrypt password hashing

---

**⚠️ Important Security Reminders:**

1. **Change the default JWT secret** before deploying to production
2. **Enable HTTPS** in production environments
3. **Use environment variables** for sensitive configuration
4. **Regularly update dependencies** to patch security vulnerabilities
5. **Monitor authentication attempts** for suspicious activity
6. **Implement rate limiting** on authentication endpoints
7. **Back up your MongoDB database** regularly

---

**Made with ❤️ by [Jose Owino](https://github.com/joseowino)**