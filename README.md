# **Customer Banking Service Documentation**

---

## **1. Overview**
The **Customer Banking Service** is a RESTful API built using **Java and Spring Boot** that allows customers to manage the following banking operations:
- **Listing customer accounts and their balances**
- **Depositing money into an account**
- **Withdrawing money from an account**
- **Transferring money between two accounts**

This service enforces business rules for **account types, transfer limits, and access restrictions**. It uses an **in-memory datastore** and requires customers to **login** before performing any actions.

---

## **2. Business Requirements**

### **Account Types and Operations**
1. **Regular Account**
    - ✅ All operations allowed (deposit, withdraw, transfer).

2. **Savings Account**
    - ✅ Can deposit money. 
    - ❌ Cannot withdraw money.
    - ❌ Cannot transfer money to another account.

---

### **Money Transfer Rules**
1. **Between Own Accounts**
    - ✅ Allowed if amount is **up to 100,000 EUR**.

2. **To Another Customer’s Account**
    - ✅ Allowed if amount is **up to 15,000 EUR**.

---

### **Access Control**
- Customers can **only view their own accounts and balances**.
- Customers can **only deposit, withdraw, or transfer** money from **their own accounts**.
- Transfers are allowed **to any existing account**, subject to business rules.

---

## **3. Technical Specifications**
- Built with **Java** using **Spring Boot**.
- Uses **in-memory datastore H2** for simplicity.
- **Authentication** implemented for all actions using **JWT token**.
- **Tests** included.

---

## **4. Initialized Data**
The in-memory datastore has been **initialized with 2 customers**, each having **3 accounts**:

### **1. Customer: Bob**
- **Login Details:**
   - Username: `bob`
   - Password: `password1`

- **Accounts:**
   - 1 Regular Account with a balance of 20,000 EUR
   - 1 Savings Account with a balance of 50,000 EUR
   - 1 Regular Account with a balance of 10,000 EUR

---

### **2. Customer: John**
- **Login Details:**
   - Username: `john`
   - Password: `password2`

- **Accounts:**
   - 1 Regular Account with a balance of 15,000 EUR
   - 1 Savings Account with a balance of 40,000 EUR
   - 1 Savings Account with a balance of 5,000 EUR

These customers and their accounts are **preloaded** when the application starts, allowing you to **test the API endpoints** without additional setup.

---



## **5. Getting Started**

### **Prerequisites**
- **Java 17+**
- **Maven** (for building and running the application)

---

### **Cloning the Repository**
```bash
git clone https://github.com/surbhisaraf/customer-banking-service.git
cd customer-banking-service
```

---

### **Running the Application**
You can run the application using **Maven**:
```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at:
```
http://localhost:8080
```

---

## **5. Authentication and Authorization**

### **Customer Login**
**API Endpoint:**
```
POST /api/auth/signin
```

**Request Body:**
```json
{
   "username": "john",
   "password": "password2"
}
```

**Response:**
```json
{
   "message": "Successfully logged in",
   "data": {
       "token": "jwt-token",
       "username": "john",
       "role": "customer",
       "type": "Bearer"
   }
}
```

**Description:**
- On successful login, a **JWT token** is returned.
- This token should be included in the **Authorization header** for all subsequent API calls:
```
Authorization: Bearer <jwt-token>
```

---

## **6. API Endpoints**

### **1. List Customer Accounts and Balances**
**API Endpoint:**
```
GET /api/v1/customer/accounts
```

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Description:** Retrieves all accounts and their balances for the authenticated customer.

**Response:**
```json
{
   "message": "Successfully fetched customer accounts",
   "data": [
      {
         "accountNo": "717171717",
         "accountType": "REGULAR",
         "balance": 15000.00,
         "currency": "EUR"
      },
      {
         "accountNo": "828282828",
         "accountType": "SAVING",
         "balance": 40000.00,
         "currency": "EUR"
      },
      {
         "accountNo": "555555555",
         "accountType": "SAVING",
         "balance": 5000.00,
         "currency": "EUR"
      }
   ]
}
```

---

### **2. Deposit Money to Account**
**API Endpoint:**
```
POST /api/v1/customer/deposit
```

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
   "toAccountNo": "717171717",
   "amount": 2000
}
```

**Description:** Deposits money into the specified account.
- ✅ Allowed for **Regular** and **Savings** accounts.

**Response:**
```json
{
   "message": "Deposit successful",
   "data": {
       "accountNo": "717171717",
       "accountType": "REGULAR",
       "balance": 17000.00,
       "currency": "EUR"
   }
}
```

---

### **3. Withdraw Money from Account**
**API Endpoint:**
```
POST /api/v1/customer/withdraw
```

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
   "fromAccountNo": "717171717",
   "amount": 2000
}
```

**Description:** Withdraws money from the specified account.
- ✅ Allowed for **Regular** accounts.
- ❌ Not allowed for **Savings** accounts.

**Response:**
```json
{
   "message": "Withdrawal successful",
   "data": {
       "accountNo": "717171717",
       "accountType": "REGULAR",
       "balance": 13000.00,
       "currency": "EUR"
   }
}
```

---

### **4. Transfer Money Between Accounts**
**API Endpoint:**
```
POST /api/v1/customer/transfer
```

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
   "fromAccountNo": "717171717",
   "toAccountNo": "828282828",
   "amount": 4000
}
```

**Description:** Transfers money from one account to another.
- ✅ **Between own accounts:** Up to **100,000 EUR**.
- ✅ **To another customer’s account:** Up to **15,000 EUR**.
- ❌ Not allowed from **Savings** accounts.

**Response:**
```json
{
   "message": "Transfer successful",
   "data": 4000
}
```

---

## **7. Running Tests**
To run all **unit tests**:
```bash
mvn test
```

---

## **8. Technologies Used**
- **Java 17** – Core programming language
- **Spring Boot** – REST API framework
- **Spring Security (JWT)** – Authentication and Authorization
- **H2 Database** – In-memory datastore
- **JUnit and Mockito** – Testing framework
- **Maven** – Build and dependency management

---
