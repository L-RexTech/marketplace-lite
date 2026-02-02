# Marketplace Lite

A microservices-based e-commerce marketplace built with Spring Boot 3.2.1 and Java 17.

## Prerequisites & Requirements

Before you begin, ensure you have the following installed on your local machine:

- **Java Development Kit (JDK) 17**: Required for compilation and runtime.
- **Apache Maven 3.8+**: To manage dependencies and build the project.
- **MySQL 8.0+**: Primary relational database.
- **Apache Kafka 3.x+**: For event-driven messaging between services.
- **Git**: To clone the repository.
- **An IDE** (IntelliJ IDEA, Eclipse, or VS Code): With Lombok plugin support enabled.
- **Terminal/CLI**: To run build commands and curl requests.

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/L-RexTech/marketplace-lite.git
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

### 3. Kafka Setup

Ensure Kafka is running on `localhost:9092`. If using Docker:
```bash
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka:latest
```

### 4. Build the Project

Run the Maven wrapper to install dependencies and build all modules (including the common-lib):
```bash
./mvnw clean install
```

### 5. Running the Services

To get the marketplace fully operational, start the services in the following recommended order:

1. **Auth Service**: Handles security context
2. **Product & Order Services**: Core business logic
3. **Notification Service**: Email notifications
4. **API Gateway**: The entry point for all traffic

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

# Notification Service
./mvnw spring-boot:run -pl notification-service

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
    ┌────┴────┬──────────────┬────────────────┐
    │         │              │                │
    ▼         ▼              ▼                ▼
┌───────┐ ┌─────────┐ ┌───────────┐ ┌──────────────┐
│ Auth  │ │ Product │ │   Order   │ │ Notification │
│Service│ │ Service │ │  Service  │ │   Service    │
│ :8081 │ │  :8082  │ │   :8083   │ │    :8084     │
└───────┘ └─────────┘ └───────────┘ └──────────────┘
    │         │  ▲           │              │
    ▼         │  │           ▼              │
┌───────┐     │  │      ┌─────────┐         │
│ MySQL │     │  │      │  Kafka  │◄────────┘
│ auth  │     │  │      └─────────┘
└───────┘     │  │           │
              ▼  │           │
         ┌─────────┐         │
         │  MySQL  │◄────────┘
         │ product │   (stock-update events)
         └─────────┘
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| api-gateway | 8080 | Routes requests, JWT validation |
| auth-service | 8081 | User registration, login, JWT token generation |
| product-service | 8082 | Product CRUD operations, Kafka consumer for stock updates |
| order-service | 8083 | Order management, Kafka producer for events |
| notification-service | 8084 | Email notifications via Kafka events |
| common-lib | - | Shared DTOs, exceptions, events, utilities |

## Tech Stack

- **Framework**: Spring Boot 3.2.1
- **Gateway**: Spring Cloud Gateway
- **Security**: Spring Security + JWT (jjwt 0.12.3)
- **Messaging**: Apache Kafka
- **Email**: Spring Mail (Gmail SMTP)
- **Database**: MySQL
- **ORM**: Spring Data JPA / Hibernate
- **Mapping**: MapStruct 1.5.5
- **Utilities**: Lombok

## Kafka Events

The services communicate asynchronously via Kafka for event-driven architecture:

| Topic | Producer | Consumer | Description |
|-------|----------|----------|-------------|
| `stock-update` | order-service | product-service | Decreases product stock when order is created |
| `order-created` | order-service | notification-service | Sends email notification for new orders |
| `order-status-changed` | order-service | notification-service | Sends email when admin updates order status |

### Event Flow

```
┌───────────────┐                      ┌─────────────────┐
│ Order Service │──── stock-update ───►│ Product Service │
│   (Producer)  │                      │   (Consumer)    │
└───────┬───────┘                      └─────────────────┘
        │
        │ order-created
        │ order-status-changed
        ▼
┌───────────────────┐
│Notification Service│───► 📧 Email to user
│    (Consumer)      │
└───────────────────┘
```

## Email Notifications

The notification-service sends emails for:

1. **New Order Created**: When a customer places an order
2. **Order Status Changed**: When admin updates order status (e.g., PENDING → CONFIRMED → SHIPPED)

### Email Configuration

Update `notification-service/src/main/resources/application.properties`:

```properties
# Gmail SMTP Configuration
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password

# Recipient email for notifications
notification.recipient.email=your-email@gmail.com
```

> [!NOTE]
> For Gmail, you need to create an **App Password**:
> 1. Enable 2-Step Verification in your Google Account
> 2. Go to Security → App Passwords → Create new
> 3. Use the 16-character password in the config

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

## Security

Each microservice implements its own JWT validation using Spring Security:

- **JWT Token**: Generated by auth-service on login
- **Token Validation**: Each service validates JWT independently
- **Role-Based Access**: Endpoints protected based on user roles (ADMIN, SELLER, BUYER)
- **Stateless Sessions**: No server-side session storage

### JWT Configuration

All services share the same JWT secret (configure in each service's `application.properties`):

```properties
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345
```

## Configuration

### Default Service Ports

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| Auth Service | 8081 |
| Product Service | 8082 |
| Order Service | 8083 |
| Notification Service | 8084 |
| Kafka | 9092 |
| MySQL | 3306 |

### Environment Properties

```properties
# Database
spring.datasource.username=root
spring.datasource.password=your_password

# JWT (same secret across all services)
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
```

## Usage Examples

### Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "buyer@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "role": "BUYER"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "buyer@example.com",
    "password": "password123"
  }'
```

### Create a Product (Seller/Admin)

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "name": "Gaming Mouse",
    "description": "RGB Gaming Mouse with 6 buttons",
    "price": 49.99,
    "stock": 100,
    "category": "Electronics"
  }'
```

### Create an Order (Authenticated Users)

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

This will:
1. Create the order
2. Publish `stock-update` event → Product service decreases stock
3. Publish `order-created` event → Notification service sends email

### Update Order Status (Admin Only)

```bash
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=CONFIRMED" \
  -H "Authorization: Bearer <admin_jwt_token>"
```

This will:
1. Update order status
2. Publish `order-status-changed` event → Notification service sends email

## Project Structure

```
marketplace-lite/
├── api-gateway/              # Spring Cloud Gateway
│   └── filter/               # JWT Authentication Filter
├── auth-service/             # Authentication & User management
│   └── security/             # JWT Provider, Security Config
├── product-service/          # Product catalog management
│   ├── security/             # JWT Filter, Security Config
│   └── kafka/                # Stock Update Consumer
├── order-service/            # Order processing
│   ├── security/             # JWT Filter, Security Config
│   └── kafka/                # Event Producer
├── notification-service/     # Email notifications
│   ├── kafka/                # Order Event Consumer
│   └── service/              # Email Service
├── common-lib/               # Shared library
│   ├── dto/                  # Common DTOs
│   ├── event/                # Kafka Event Classes
│   └── exception/            # Common Exceptions
└── pom.xml                   # Parent POM
```

## License

MIT License
