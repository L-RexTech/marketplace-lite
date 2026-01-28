# Marketplace Lite

A microservices-based e-commerce marketplace built with Spring Boot 3.2.1 and Java 17.

##  Prerequisites & Requirements

Before you begin, ensure you have the following installed on your local machine:

- **Java Development Kit (JDK) 17**: Required for compilation and runtime.
- **Apache Maven 3.8+**: To manage dependencies and build the project.
- **MySQL 8.0+**: Primary relational database.
- **Git**: To clone the repository.
- **An IDE** (IntelliJ IDEA, Eclipse, or VS Code): With Lombok plugin support enabled.
- **Terminal/CLI**: To run build commands and curl requests.

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/marketplace-lite.git
cd marketplace-lite
```

### 2. Database Configuration

Create the necessary databases in your MySQL instance:
```sql
CREATE DATABASE marketplace_auth;
CREATE DATABASE marketplace_product;
CREATE DATABASE marketplace_order;
```

> [!IMPORTANT]  
> Update the `src/main/resources/application.properties` (or `application.yml`) in each service with your MySQL username and password if they differ from the defaults.

### 3. Build the Project

Run the Maven wrapper to install dependencies and build all modules (including the common-lib):
```bash
./mvnw clean install
```

### 4. Running the Services

To get the marketplace fully operational, start the services in the following recommended order:

1. **Auth Service**: Handles security context
2. **Product & Order Services**: Core business logic
3. **API Gateway**: The entry point for all traffic

**Command syntax:**
```bash
./mvnw spring-boot:run -pl <service-name>
```

**Example commands:**
```bash
# Auth Service
./mvnw spring-boot:run -pl auth-service

# Product Service
./mvnw spring-boot:run -pl product-service

# Order Service
./mvnw spring-boot:run -pl order-service

# API Gateway (start last)
./mvnw spring-boot:run -pl api-gateway
```

## Architecture

```
┌─────────────────┐
│   API Gateway   │ :8080
│  (Spring Cloud) │
└────────┬────────┘
         │
    ┌────┴────┬──────────────┐
    │         │              │
    ▼         ▼              ▼
┌───────┐ ┌─────────┐ ┌───────────┐
│ Auth  │ │ Product │ │   Order   │
│Service│ │ Service │ │  Service  │
│ :8081 │ │  :8082  │ │   :8083   │
└───────┘ └─────────┘ └───────────┘
    │         │              │
    ▼         ▼              ▼
┌───────┐ ┌─────────┐ ┌───────────┐
│ MySQL │ │  MySQL  │ │   MySQL   │
│ auth  │ │ product │ │   order   │
└───────┘ └─────────┘ └───────────┘
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| api-gateway | 8080 | Routes requests, JWT validation |
| auth-service | 8081 | User registration, login, JWT token generation |
| product-service | 8082 | Product CRUD operations |
| order-service | 8083 | Order management |
| common-lib | - | Shared DTOs, exceptions, utilities |

## Tech Stack

- **Framework**: Spring Boot 3.2.1
- **Gateway**: Spring Cloud Gateway
- **Security**: Spring Security + JWT (jjwt 0.12.3)
- **Database**: MySQL
- **ORM**: Spring Data JPA / Hibernate
- **Mapping**: MapStruct 1.5.5
- **Utilities**: Lombok

## API Endpoints

### Auth Service (`/api/auth`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |
| GET | `/api/auth/me` | Get current user info | Authenticated |
| GET | `/api/auth/users/{id}` | Get user by ID | Admin only |

### Product Service (`/api/products`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/products` | Get all active products | Public |
| GET | `/api/products/{id}` | Get product by ID | Public |
| GET | `/api/products/category/{category}` | Get products by category | Public |
| GET | `/api/products/seller` | Get seller's own products | Seller, Admin |
| POST | `/api/products` | Create new product | Seller, Admin |
| PUT | `/api/products/{id}` | Update product | Seller (own), Admin |
| DELETE | `/api/products/{id}` | Delete product | Seller (own), Admin |

### Order Service (`/api/orders`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/orders` | Create new order | Buyer, Seller, Admin |
| GET | `/api/orders` | Get orders | Own orders (Buyer/Seller), All (Admin) |
| GET | `/api/orders/{id}` | Get order by ID | Own order (Buyer/Seller), Any (Admin) |
| PATCH | `/api/orders/{id}/status` | Update order status | Admin only |

## RBAC Rules Summary

| Endpoint | ADMIN | SELLER | BUYER | PUBLIC |
|----------|:-----:|:------:|:-----:|:------:|
| Register | ✅ | ✅ | ✅ | ✅ |
| Login | ✅ | ✅ | ✅ | ✅ |
| Get All Products | ✅ | ✅ | ✅ | ✅ |
| Get Product by ID | ✅ | ✅ | ✅ | ✅ |
| Create Product | ✅ | ✅ | ❌ | ❌ |
| Update Product | ✅ (all) | ✅ (own) | ❌ | ❌ |
| Delete Product | ✅ (all) | ✅ (own) | ❌ | ❌ |
| Create Order | ✅ | ✅ | ✅ | ❌ |
| Get Orders | ✅ (all) | ✅ (own) | ✅ (own) | ❌ |
| Update Order Status | ✅ | ❌ | ❌ | ❌ |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Database Setup

Create the following databases in MySQL:

```sql
CREATE DATABASE marketplace_auth;
CREATE DATABASE marketplace_product;
CREATE DATABASE marketplace_order;
```

### Configuration

Each service uses the following default configuration:

```properties
# Database
spring.datasource.username=root
spring.datasource.password=your_password

# JWT (same secret across all services)
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345
```

### Build

```bash
./mvnw clean install
```

### Run Services

Start each service in separate terminals:

```bash
# Auth Service
./mvnw spring-boot:run -pl auth-service

# Product Service
./mvnw spring-boot:run -pl product-service

# Order Service
./mvnw spring-boot:run -pl order-service

# API Gateway (start last)
./mvnw spring-boot:run -pl api-gateway
```

## Usage Examples

### Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "seller@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "SELLER"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "seller@example.com",
    "password": "password123"
  }'
```

### Create a Product (with JWT)

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "name": "Product Name",
    "description": "Product description",
    "price": 99.99,
    "stock": 100,
    "category": "Electronics"
  }'
```

### Create an Order (with JWT)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "shippingAddress": "123 Main St, City",
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }'
```

## Project Structure

```
marketplace-lite/
├── api-gateway/          # Spring Cloud Gateway
├── auth-service/         # Authentication & User management
├── product-service/      # Product catalog management
├── order-service/        # Order processing
├── common-lib/           # Shared library
└── pom.xml              # Parent POM
```

## License

MIT License
