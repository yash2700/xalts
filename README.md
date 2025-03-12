# Multi-User Task Approval System

## Overview
This project implements a multi-user task approval system with authentication, task creation, and multi-signature approval processes. It includes:
- **PostgreSQL database** for storing user and task details.
- **Spring Boot REST APIs** for user authentication, task creation, and multi-approval workflows.
- **JWT-based authentication** for secure API access.
- **Email notifications** for task approval updates.

---

## 🚀 Features
### 1️⃣ **User Authentication & Database**
- Uses **PostgreSQL** to store user details (name, email, internal login ID).
- **Signup/Login APIs** for user authentication.
- Secure **JWT-based authentication** using Spring Security.

### 2️⃣ **Task Management & Multi-Approval Process**
- Users can **create tasks** with a status field.
- Tasks require **approval from three other users** before reaching an approved status.
- Dropdown selection for choosing approvers while creating a task.
- Approvers can **add comments** while approving tasks.
- **Email notifications** sent to:
  - Selected approvers when a task is created.
  - Task creator when an approver signs off.
  - All involved users when the task is fully approved.

### 3️⃣ **REST APIs**
- Fully modular **RESTful API endpoints** for seamless integration with any frontend.
- Designed for **scalability** and easy extension.

---

## 🔧 **API Endpoints**

### **1️⃣ Authentication APIs** (`/auth`)
| Method | Endpoint | Description |
|--------|------------|-------------|
| `POST` | `/auth/login` | Logs in a user and returns a JWT token |

### **2️⃣ User APIs** (`/users`)
| Method | Endpoint | Description |
|--------|------------|-------------|
| `POST` | `/users/register` | Registers a new user |
| `GET`  | `/users/{id}` | Fetches user details by ID |
| `GET`  | `/users/all` | Retrieves all registered users |

### **3️⃣ Task APIs** (`/tasks`)
| Method | Endpoint | Description |
|--------|------------|-------------|
| `POST` | `/tasks/create` | Creates a new task |
| `POST` | `/tasks/{taskId}/approve` | Approves a task and adds a comment |
| `GET`  | `/tasks/{id}` | Fetches task details by ID |

---

## 🔒 **Security & Authentication**
- **Spring Security with JWT**:
  - Users must authenticate using their credentials to receive a JWT token.
  - All secured endpoints require the JWT token in the `Authorization` header.
- **Role-based access control** (future enhancement potential).

---

## 📬 **Email Notifications**
- **New Task Creation**: Notifies selected approvers.
- **Task Approval**: Notifies the creator when an approver signs off.
- **Final Approval**: Notifies all participants when the task reaches an approved status.

---

## Standard Testcases
## Exception Handling

## 🛠️ **Setup & Installation**
### **Prerequisites**
- Java 17+
- PostgreSQL
- Maven

### **Installation Steps**
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/task-approval-system.git
   cd task-approval-system
   ```
2. Configure **PostgreSQL** database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/task_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

4. Access APIs via **Postman or frontend** integration.

---

## 📌 **Future Enhancements**
- **Admin Panel** for task monitoring.
- **Role-Based Access Control (RBAC)**.
- **Task Due Dates & Reminders**.
- **WebSocket notifications for real-time updates**.

---

## 📄 **License**
This project is licensed under the MIT License.

---

## 🤝 **Contributing**
1. Fork the repo.
2. Create a new branch (`feature/your-feature`).
3. Commit your changes.
4. Open a Pull Request.

---

### 📧 **Need Help?**
Feel free to open an issue in the repository or contact the maintainers!

