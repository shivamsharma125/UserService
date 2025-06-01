# User Service

A robust and stateless Spring Boot-based User Service that provides complete user authentication and management capabilities, including JWT and OAuth2 (Google) based login. This service is designed with RESTful APIs, secure practices, and clear test coverage to be easily integrated into any larger application ecosystem.

---

## Features

* **User Registration** (Sign-Up)
* **Email & Password Based Login** with JWT Token generation
* **JWT Token Validation** endpoint
* **OAuth2 Login** support (Google)
* **Get User By ID** endpoint
* **Update User Profile**
* **Spring Security Integration** (Stateless + OAuth2)
* **Custom Exceptions & Validation**
* **JUnit + Mockito + MockMvc Test Coverage**

---

## Technologies Used

* Java 17
* Spring Boot 3
* Spring Security
* JWT (JJWT Library)
* OAuth2 Client
* JPA & Hibernate
* MySQL
* Maven
* JUnit 5, Mockito, MockMvc

---

## API Endpoints

### 🔐 `/auth`

#### POST `/signup`

Registers a new user.

* Request: `SignUpRequestDto { name, email, password }`
* Validates input fields.
* Returns: `UserDto` with HTTP 201 CREATED

#### POST `/login`

Logs in a user using email and password.

* Request: `LoginRequestDto { email, password }`
* Returns: JWT Token (String)

#### POST `/validate`

Validates a JWT token for a user.

* Request: `ValidateTokenDto { userId, token }`
* Returns: `Boolean` (true if token is valid)

---

### 👤 `/users`

#### GET `/{userId}`

Fetch user details by ID.

* Returns: `UserDto`

#### PUT `/{userId}`

Update user details.

* Request: `UpdateUserRequestDto`
* Returns: `UserDto`

---

## Security Configuration

* Stateless Session Management
* `/auth/**` and `GET /users/{id}` are public endpoints
* Other endpoints are secured endpoint
* OAuth2 Login via Google

    * After successful login, a JWT token is returned in JSON response body

---

## Token Handling

### JWT Token Generation

* Includes claims: `issuedAt`, `expiration`, `subject (userId)`, `issuer`, and `scope (roles)`
* Token is valid for **30 days**

### Token Validation

* Checks if token is tampered or expired

---

## OAuth2 Login Flow

* Configured via Spring Security
* Custom `OAuth2LoginSuccessHandler`:

    * Extracts user info (email, name)
    * Calls `oauthLogin()` in `AuthService`
    * Returns signed JWT in response

---

## Test Coverage ✅

### Unit Tests (Mockito)

* All service layer logic covered
* Includes:

    * Token validation & expiry
    * Signup/login logic
    * OAuth login flow

### Web Layer Tests (MockMvc)

* All endpoints covered
* Tests include:

    * Valid inputs
    * Invalid/missing fields
    * Edge cases
    * Response verification using `jsonPath`
    * `ArgumentCaptor` & `verify()` usage

---

## Project Structure

```
com.shivam.userservice
├── advices
├── configs
├── controllers
├── dtos
├── exceptions
├── models
├── repositories
├── security
├── services
├── utils
```

---

## 📌 Related Microservices

* 📂 **Product Service** – Manage product catalog
* 📧 **Email Service** – Send transactional emails via Kafka events
* 💳 **Payment Service** – Manage payments via Razorpay & Stripe
* 🔍 **Service Discovery** – Eureka-based service registry