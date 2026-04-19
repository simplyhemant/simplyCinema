# 🎬 SimplyCinema – Highly Scalable Movie Ticketing Ecosystem

"Experience Cinema, Simplified."
SimplyCinema is a production-grade, full-stack movie ticket booking platform designed to handle complex role-based workflows, high-concurrency seat reservations, and seamless payment integrations.

---

## 🔗 Project Ecosystem

[![Live Demo](https://img.shields.io/badge/LIVE_DEMO-brightgreen?style=for-the-badge&logo=vercel)](https://simply-cinema-frontend.vercel.app/)
[![Frontend Repo](https://img.shields.io/badge/FRONTEND_REPO-blue?style=for-the-badge&logo=github)](https://github.com/simplyhemant/simplyCinema-frontend)
[![Swagger API](https://img.shields.io/badge/SWAGGER_DOCS-orange?style=for-the-badge&logo=swagger)](http://13.201.58.222:8080/swagger-ui/index.html)
[![Postman](https://img.shields.io/badge/POSTMAN_SPEC-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://documenter.getpostman.com/view/39898850/2sB3Wnv1eV)

---

### 🚀 Quick Access
*   **Web App:** [simply-cinema-frontend.vercel.app](https://simply-cinema-frontend.vercel.app/)
*   **API Specs:** [Swagger Documentation](http://13.201.58.222:8080/swagger-ui/index.html)
*   **Dev Resources:** [Postman Collection](https://documenter.getpostman.com/view/39898850/2sB3Wnv1eV)

---

## 🎥 Role-Based Video Walkthroughs

To see the platform in action, check out the demonstration videos for each role:

| Role | Demonstration Video |
| :--- | :--- |
| **🛡️ System Admin** | [Watch Admin Workflow](https://www.loom.com/share/6a64e412f4f34a51bd962e3a58871a19) |
| **🏨 Theatre Owner** | [Watch Owner Workflow](https://www.loom.com/share/3082f221b7de4635a095c8963d920db6) |
| **🎫 Theatre Staff** | [Watch Staff Workflow](https://www.loom.com/share/18feffee61284a37b61feff8615a0ccf) |
| **👤 Customer** | [Watch Customer Workflow](https://www.loom.com/share/53ae449ead224e0b8a59a9cf8d2d7d5f) |

---

## 🏗️ System Architecture

SimplyCinema follows a decoupled client-server architecture optimized for performance and security.

```mermaid
graph LR
    A[Frontend - Vercel] <--> B[Nginx Reverse Proxy]
    B <--> C[Spring Boot Backend - AWS EC2]
    C <--> D[(Postgres - Persistence)]
    C <--> E[(Redis - Seat Locking)]
    C <--> G[WebSocket - App State Push]
    C <--> F[Razorpay AP]
```

---

## 🔥 Features

### ✨ Key Features & Demos

#### 👤 For Customers (Users)
> 📺 **Video Demo:** [Customer Booking Journey](https://www.loom.com/share/53ae449ead224e0b8a59a9cf8d2d7d5f)
*   **Real-Time Seat selection**: Live seat map powered by WebSockets (STOMP) for instant availability updates across all client sessions.
*   **Zero-Latency Sync**: Multiple users see seat locks/releases in real-time without refreshing.
*   **Search & Filter:** Find movies by city, language, genre, and dynamic formats (2D, 3D, IMAX).
*   **Live Seat Selection:** Interactive seat map with real-time status updates.
*   **One-Click Booking:** Fast and secure ticket checkout with integrated payments.
*   **Social Auth:** Instant login via Google or GitHub OAuth2.

#### 🏨 For Theatre Owners
> 📺 **Video Demo:** [Theatre Management & Analytics](https://www.loom.com/share/3082f221b7de4635a095c8963d920db6)
*   **Venue Management:** Add and manage multiple cinema locations and physical screen layouts.
*   **Show Scheduling:** Full control over movie timings, screen selection, and tier-based pricing.
*   **Business Analytics:** Real-time revenue tracking and occupancy reports for all theatres.

#### 🎫 For Theatre Staff
> 📺 **Video Demo:** [Box Office & Validation](https://www.loom.com/share/18feffee61284a37b61feff8615a0ccf)
*   **Ticket Verification:** Rapid lookup and validation of customer booking IDs.
*   **Occupancy Monitoring:** View real-time seat filling status for upcoming shows.
*   **Counter Bookings:** Manual seat reservation support for on-ground box office sales.

#### 🛡️ For Administrators
> 📺 **Video Demo:** [Global Oversight](https://www.loom.com/share/6a64e412f4f34a51bd962e3a58871a19)
*   **Global Oversight:** Approve/Decline theatre owner registrations and maintain movie catalogs.
*   **Metadata Control:** Manage master data for cities, languages, and technical movie formats.
*   **System Security:** Monitor platform-wide activity and ensure operational integrity.

---

## 🛠️ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | JavaScript (ES6+), Tailwind CSS, HTML5 |
| **Backend** | Java, Spring Boot, Spring WebSocket, Spring Security (JWT), Hibernate |
| **Database** | Postgres , Redis (Caching/Locking) |
| **DevOps** | AWS EC2, Nginx, Vercel, Git/GitHub |
| **Integration**| Razorpay Payment Gateway, OAuth2 |

---

## 🧠 Backend Highlights & Engineering Solutions

### 1. Real-Time Synchronization (WebSockets)
**Challenge**: Users previously relied on HTTP polling (5s intervals), leading to race conditions where two users might select the same seat between polls.
**Solution**: Migrated to a Push-Based WebSocket architecture. Using STOMP/SockJS, the backend now broadcasts Redis lock/release events to specific showId topics, delivering near-instant status updates to all customers.

### 2. Concurrency Control (The "First-To-Book" Problem)
**Challenge:** Multiple users selecting the same seat at the exact same millisecond.
**Solution:** Integrated **Redis-Based Distributed Locking**. Before any database write, the system sets a Redis key for the seat with a 5-minute TTL. This ensures seats are temporarily reserved during payment and released automatically if the transaction fails, reducing DB latency by 40%.

### 3. Granular RBAC
A sophisticated **Role-Based Access Control** system using Spring Security. Permissions are mapped down to specific actions (e.g., `MANAGE_SHOWS`, `VERIFY_TICKETS`), ensuring a secure multi-tenant environment.                           

---

## 💾 Database Schema Overview
The system manages complex relationships across **12+ tables**:
* **Users & Roles:** Managed via a Many-to-Many relationship.
* **Theatre Hierarchy:** Theatre → Screen → Seat Type → Seats.
* **Mapping:** Movie ↔ Show ↔ Screen (supporting multiple formats).
* **Transaction Flow:** Booking → Payment → Coupon.

---

## 🚀 Local Development Setup

### **Frontend**
1. Clone the repository:
   ```bash
   git clone https://github.com/simplyhemant/simplyCinema-frontend.git
   cd simplyCinema-frontend
   ```
2. Open `index.html` or serve with any local HTTP server.

### **Backend**
*   **Requirements:** JDK 17+, MySQL 8.0+, Redis Server (6379)
1.  **Clone the Repo:**
    ```bash
    git clone https://github.com/simplyhemant/simplyCinema-backend.git
    cd simplyCinema-backend
    ```
2.  **Configure Environment:**
    Set the following in your `application.properties` or environment variables:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/simplycinema
    spring.datasource.username=ROOT_USER
    spring.datasource.password=ROOT_PASSWORD
    spring.data.redis.host=localhost
    spring.data.redis.port=6379
    jwt.secret=YOUR_JWT_SECRET
    razorpay.key_id=YOUR_RAZORPAY_KEY
    razorpay.key_secret=YOUR_RAZORPAY_SECRET
    ```
3.  **Run Application:**
    ```bash
    ./mvnw spring-boot:run
    ```

---

## 📁 Project Structure
```text
src/main/java/com/simply/Cinema/
├── core/               # Main domain (Booking, Shows, Movies)
│   ├── controller/     # REST Controllers
│   ├── service/        # Business Logic Implementations
│   ├── repository/     # Data Persistence Layer
│   └── dto/            # Data Transfer Objects
├── security/           # JWT Filters, OAuth2 Handlers, & Security Config
├── common/             # Global exceptions, Utils, & Base classes
└── config/             # Redis, Swagger, & Payment configurations
```
