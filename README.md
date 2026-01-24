# 🎬 SimplyCinema — Movie Booking Backend  

A **scalable movie ticket booking backend** built with **Spring Boot**, **PostgreSQL**, and **Redis**, designed for real-time seat booking, secure authentication, and modular expansion.  
🚧 **Project Status**: Work in Progress  

---

## 🚀 Live Deployment (AWS EC2)

🌐 **Live API Base URL:** http://13.201.58.222:8080/  
📌 **Swagger UI:** http://13.201.58.222:8080/swagger-ui/index.html  

---

## 📄 Postman Documentation
Link : https://documenter.getpostman.com/view/39898850/2sB3Wnv1eV  

---

## 🔎 Overview  

SimplyCinema is a **movie booking backend service** that handles:  
- **User authentication & authorization**  
- **Movie and show management**  
- **Real-time seat booking with Redis locking**  

This backend will eventually power a **full-featured cinema booking system** with payments, coupons, and loyalty programs.  

---

## 🛠 Tech Stack  

- **Framework**: Spring Boot  
- **Database**: PostgreSQL (**AWS RDS**)  
- **Cache & Real-Time Locking**: Redis  
- **Authentication**: JWT & OAuth2  
- **API Style**: REST APIs  
- **Realtime Updates**: WebSocket  
- **Planned**: Kafka for event-driven communication  

---

## ☁️ Hosting / Deployment  

- Hosted on **AWS EC2**  
- Database hosted on **AWS RDS (PostgreSQL)**  
- APIs accessible publicly via **Swagger + Postman Docs**  

---

## ✨ Features  

### ✅ Implemented  
- JWT & OAuth2 authentication  
- Role-Based Access Control (RBAC)  
- Movie and show management basics  
- Real-time seat locking using Redis (in progress)  

### 🚧 Planned  
- Redis caching for performance  
- Kafka-based event streaming  
- Payment gateway integration (Stripe/Razorpay)  
- Coupons & loyalty program  
- Pre-booking system  
- Refund handling  

---

## 🏗 Architecture  

- **Authentication Layer** → JWT & OAuth2 with RBAC  
- **Persistence Layer** → PostgreSQL (movies, users, bookings)  
- **Caching Layer** → Redis (seat locks, temporary states)  
- **Communication Layer** → REST APIs & WebSockets  
- **Planned** → Kafka for async events (notifications, analytics)  
