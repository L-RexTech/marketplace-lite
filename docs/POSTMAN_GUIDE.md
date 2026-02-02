# Postman API Testing Guide

This guide provides step-by-step instructions for testing all API endpoints using Postman.

## Setup

### Base URL
```
http://localhost:8080
```

### Headers (for all requests)
| Header | Value |
|--------|-------|
| Content-Type | application/json |

### Authorization Header (for protected endpoints)
| Header | Value |
|--------|-------|
| Authorization | Bearer `<your_jwt_token>` |

---

## 1. Auth Service Endpoints

### 1.1 Register a New User

**Endpoint:** `POST /api/auth/register`

**Authorization:** None required

**Headers:**
| Header | Value |
|--------|-------|
| Content-Type | application/json |

**Body (raw JSON):**

**Register as BUYER:**
```json
{
    "email": "buyer@example.com",
    "password": "password123",
    "fullName": "John Buyer",
    "role": "BUYER"
}
```

**Register as SELLER:**
```json
{
    "email": "seller@example.com",
    "password": "password123",
    "fullName": "Jane Seller",
    "role": "SELLER"
}
```

**Register as ADMIN:**
```json
{
    "email": "admin@example.com",
    "password": "password123",
    "fullName": "Admin User",
    "role": "ADMIN"
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Registration successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "type": "Bearer",
        "userId": 1,
        "email": "buyer@example.com",
        "fullName": "John Buyer",
        "roles": ["BUYER"]
    }
}
```

> 💡 **Tip:** Save the `token` from the response for subsequent authenticated requests.

---

### 1.2 Login

**Endpoint:** `POST /api/auth/login`

**Authorization:** None required

**Headers:**
| Header | Value |
|--------|-------|
| Content-Type | application/json |

**Body (raw JSON):**
```json
{
    "email": "buyer@example.com",
    "password": "password123"
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "type": "Bearer",
        "userId": 1,
        "email": "buyer@example.com",
        "fullName": "John Buyer",
        "roles": ["BUYER"]
    }
}
```

> 💡 **Tip:** Copy the `token` and use it in the Authorization header for protected endpoints.

---

### 1.3 Get Current User Info

**Endpoint:** `GET /api/auth/me`

**Authorization:** ✅ Required (Any authenticated user)

**Headers:**
| Header | Value |
|--------|-------|
| Authorization | Bearer `<your_jwt_token>` |

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": {
        "id": 1,
        "email": "buyer@example.com",
        "fullName": "John Buyer",
        "phone": null,
        "roles": ["BUYER"]
    }
}
```

---

### 1.4 Get User by ID (Admin Only)

**Endpoint:** `GET /api/auth/users/{id}`

**Authorization:** ✅ Required (ADMIN only)

**Headers:**
| Header | Value |
|--------|-------|
| Authorization | Bearer `<admin_jwt_token>` |

**URL Example:** `GET /api/auth/users/1`

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": {
        "id": 1,
        "email": "buyer@example.com",
        "fullName": "John Buyer",
        "phone": null,
        "roles": ["BUYER"]
    }
}
```

---

## 2. Product Service Endpoints

### 2.1 Get All Products (Public)

**Endpoint:** `GET /api/products`

**Authorization:** None required

**Headers:** None required

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": [
        {
            "id": 1,
            "name": "Gaming Mouse",
            "description": "RGB Gaming Mouse",
            "price": 49.99,
            "stock": 100,
            "category": "Electronics",
            "imageUrl": "https://example.com/mouse.jpg",
            "sellerId": 1,
            "status": "ACTIVE",
            "createdAt": "2026-01-27T16:25:55",
            "updatedAt": "2026-01-27T16:25:55"
        }
    ]
}
```

---

### 2.2 Get Product by ID (Public)

**Endpoint:** `GET /api/products/{id}`

**Authorization:** None required

**URL Example:** `GET /api/products/1`

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": {
        "id": 1,
        "name": "Gaming Mouse",
        "description": "RGB Gaming Mouse",
        "price": 49.99,
        "stock": 100,
        "category": "Electronics",
        "imageUrl": "https://example.com/mouse.jpg",
        "sellerId": 1,
        "status": "ACTIVE"
    }
}
```

---

### 2.3 Get Products by Category (Public)

**Endpoint:** `GET /api/products/category/{category}`

**Authorization:** None required

**URL Example:** `GET /api/products/category/Electronics`

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": [
        {
            "id": 1,
            "name": "Gaming Mouse",
            "category": "Electronics",
            ...
        }
    ]
}
```

---

### 2.4 Get Seller's Own Products

**Endpoint:** `GET /api/products/seller`

**Authorization:** ✅ Required (SELLER or ADMIN)

**Headers:**
| Header | Value |
|--------|-------|
| Authorization | Bearer `<seller_jwt_token>` |

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": [
        {
            "id": 1,
            "name": "Gaming Mouse",
            "sellerId": 1,
            ...
        }
    ]
}
```

---

### 2.5 Create Product

**Endpoint:** `POST /api/products`

**Authorization:** ✅ Required (SELLER or ADMIN)

**Headers:**
| Header | Value |
|--------|-------|
| Content-Type | application/json |
| Authorization | Bearer `<seller_jwt_token>` |

**Body (raw JSON):**
```json
{
    "name": "Gaming Keyboard",
    "description": "Mechanical RGB Gaming Keyboard with Cherry MX switches",
    "price": 129.99,
    "stock": 50,
    "category": "Electronics",
    "imageUrl": "https://example.com/keyboard.jpg"
}
```

**Expected Response (201 Created):**
```json
{
    "success": true,
    "message": "Product created successfully",
    "data": {
        "id": 2,
        "name": "Gaming Keyboard",
        "description": "Mechanical RGB Gaming Keyboard with Cherry MX switches",
        "price": 129.99,
        "stock": 50,
        "category": "Electronics",
        "imageUrl": "https://example.com/keyboard.jpg",
        "sellerId": 1,
        "status": "ACTIVE"
    }
}
```

---

### 2.6 Update Product

**Endpoint:** `PUT /api/products/{id}`

**Authorization:** ✅ Required (SELLER owns the product, or ADMIN)

**Headers:**
| Header | Value |
|--------|-------|
| Content-Type | application/json |
| Authorization | Bearer `<seller_jwt_token>` |

**URL Example:** `PUT /api/products/1`

**Body (raw JSON):**
```json
{
    "name": "Gaming Mouse Pro",
    "description": "Updated RGB Gaming Mouse with 8 buttons",
    "price": 59.99,
    "stock": 75,
    "category": "Electronics",
    "imageUrl": "https://example.com/mouse-pro.jpg"
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Product updated successfully",
    "data": {
        "id": 1,
        "name": "Gaming Mouse Pro",
        "price": 59.99,
        ...
    }
}
```

---

### 2.7 Delete Product

**Endpoint:** `DELETE /api/products/{id}`

**Authorization:** ✅ Required (SELLER owns the product, or ADMIN)

**Headers:**
| Header | Value |
|--------|-------|
| Authorization | Bearer `<seller_jwt_token>` |

**URL Example:** `DELETE /api/products/1`

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Product deleted successfully",
    "data": null
}
```

---

## 3. Order Service Endpoints

### 3.1 Create Order

**Endpoint:** `POST /api/orders`

**Authorization:** ✅ Required (BUYER, SELLER, or ADMIN)

**Headers:**
| Header | Value |
|--------|-------|
| Content-Type | application/json |
| Authorization | Bearer `<buyer_jwt_token>` |

**Body (raw JSON):**

**Single Item Order:**
```json
{
    "shippingAddress": "123 Main Street, City, State 12345",
    "notes": "Please leave at the door",
    "items": [
        {
            "productId": 1,
            "quantity": 2
        }
    ]
}
```

**Multiple Items Order:**
```json
{
    "shippingAddress": "456 Oak Avenue, Town, State 67890",
    "notes": "Gift wrap please",
    "items": [
        {
            "productId": 1,
            "quantity": 1
        },
        {
            "productId": 2,
            "quantity": 3
        }
    ]
}
```

**Expected Response (201 Created):**
```json
{
    "success": true,
    "message": "Order created successfully",
    "data": {
        "id": 1,
        "userId": 1,
        "items": [
            {
                "productId": 1,
                "quantity": 2,
                "productName": "Product 1",
                "price": 100,
                "subtotal": 200
            }
        ],
        "totalAmount": 200,
        "status": "PENDING",
        "shippingAddress": "123 Main Street, City, State 12345",
        "notes": "Please leave at the door",
        "createdAt": "2026-02-02T12:00:00",
        "updatedAt": "2026-02-02T12:00:00"
    }
}
```

> 📧 **Note:** This triggers Kafka events:
> - `stock-update` → decreases product stock
> - `order-created` → sends email notification

---

### 3.2 Get User's Orders

**Endpoint:** `GET /api/orders`

**Authorization:** ✅ Required

**Headers:**
| Header | Value |
|--------|-------|
| Authorization | Bearer `<jwt_token>` |

**Body:** None

**Behavior:**
- **BUYER/SELLER:** Returns only their own orders
- **ADMIN:** Returns all orders

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": [
        {
            "id": 1,
            "userId": 1,
            "totalAmount": 200,
            "status": "PENDING",
            "shippingAddress": "123 Main Street",
            ...
        }
    ]
}
```

---

### 3.3 Get Order by ID

**Endpoint:** `GET /api/orders/{id}`

**Authorization:** ✅ Required (Own order for BUYER/SELLER, any order for ADMIN)

**Headers:**
| Header | Value |
|--------|-------|
| Authorization | Bearer `<jwt_token>` |

**URL Example:** `GET /api/orders/1`

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Success",
    "data": {
        "id": 1,
        "userId": 1,
        "items": [...],
        "totalAmount": 200,
        "status": "PENDING",
        "shippingAddress": "123 Main Street",
        "createdAt": "2026-02-02T12:00:00"
    }
}
```

---

### 3.4 Update Order Status (Admin Only)

**Endpoint:** `PATCH /api/orders/{id}/status`

**Authorization:** ✅ Required (ADMIN only)

**Headers:**
| Header | Value |
|--------|-------|
| Authorization | Bearer `<admin_jwt_token>` |

**URL Example:** `PATCH /api/orders/1/status?status=CONFIRMED`

**Query Parameters:**
| Parameter | Value | Options |
|-----------|-------|---------|
| status | CONFIRMED | PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED |

**Body:** None

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Order status updated successfully",
    "data": {
        "id": 1,
        "status": "CONFIRMED",
        ...
    }
}
```

> 📧 **Note:** This triggers a Kafka event that sends an email notification about the status change.

---

## 4. Quick Test Flow

Follow this sequence to test the complete flow:

### Step 1: Register Users
1. Register a SELLER account
2. Register a BUYER account  
3. Register an ADMIN account

### Step 2: Create Products (as SELLER)
1. Login as SELLER
2. Copy the JWT token
3. Create 2-3 products

### Step 3: Place Orders (as BUYER)
1. Login as BUYER
2. Copy the JWT token
3. View available products (no auth needed)
4. Create an order

### Step 4: Manage Orders (as ADMIN)
1. Login as ADMIN
2. Copy the JWT token
3. View all orders
4. Update order status (PENDING → CONFIRMED → SHIPPED)

---

## 5. Postman Collection Setup

### Setting Up Environment Variables

Create a Postman Environment with these variables:

| Variable | Initial Value | Description |
|----------|---------------|-------------|
| `base_url` | `http://localhost:8080` | API Gateway URL |
| `buyer_token` | (empty) | Buyer JWT token |
| `seller_token` | (empty) | Seller JWT token |
| `admin_token` | (empty) | Admin JWT token |

### Auto-Save Token Script

Add this script to the **Tests** tab of Login/Register requests:

```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    var token = jsonData.data.token;
    var role = jsonData.data.roles[0];
    
    if (role === "BUYER") {
        pm.environment.set("buyer_token", token);
    } else if (role === "SELLER") {
        pm.environment.set("seller_token", token);
    } else if (role === "ADMIN") {
        pm.environment.set("admin_token", token);
    }
}
```

### Using Variables in Requests

**URL:** `{{base_url}}/api/products`

**Authorization Header:** `Bearer {{seller_token}}`

---

## 6. Error Responses

### 401 Unauthorized
```json
{
    "timestamp": "2026-02-02T12:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid or expired token",
    "path": "/api/products"
}
```

### 403 Forbidden
```json
{
    "timestamp": "2026-02-02T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access denied",
    "path": "/api/orders/1/status"
}
```

### 404 Not Found
```json
{
    "success": false,
    "message": "Product not found with id: 999",
    "data": null
}
```

### 400 Bad Request
```json
{
    "success": false,
    "message": "Insufficient stock for product ID: 1",
    "data": null
}
```

---

## 7. Summary Table

| Endpoint | Method | Auth Required | Roles Allowed |
|----------|--------|---------------|---------------|
| `/api/auth/register` | POST | ❌ | Public |
| `/api/auth/login` | POST | ❌ | Public |
| `/api/auth/me` | GET | ✅ | Any authenticated |
| `/api/auth/users/{id}` | GET | ✅ | ADMIN |
| `/api/products` | GET | ❌ | Public |
| `/api/products/{id}` | GET | ❌ | Public |
| `/api/products/category/{cat}` | GET | ❌ | Public |
| `/api/products/seller` | GET | ✅ | SELLER, ADMIN |
| `/api/products` | POST | ✅ | SELLER, ADMIN |
| `/api/products/{id}` | PUT | ✅ | SELLER (own), ADMIN |
| `/api/products/{id}` | DELETE | ✅ | SELLER (own), ADMIN |
| `/api/orders` | POST | ✅ | BUYER, SELLER, ADMIN |
| `/api/orders` | GET | ✅ | Own (BUYER/SELLER), All (ADMIN) |
| `/api/orders/{id}` | GET | ✅ | Own (BUYER/SELLER), Any (ADMIN) |
| `/api/orders/{id}/status` | PATCH | ✅ | ADMIN |
