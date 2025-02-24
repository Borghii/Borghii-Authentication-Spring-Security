# 🔒 Project-Multi-Auth-Spring-Security

The main purpose of this project is to implement various user authentication methods using **Spring Security**, including:

- 🔑 Username/password authentication with encryption using `PasswordEncoder`.
- 🕒 Session management to handle user state persistence.
- 🌐 **OpenID (OAuth 2.0)** authentication as a client for external authentication.
- 🔐 **Passkey (FIDO2/WebAuthn)** authentication for enhanced security.
- 🎟️ **One-Time Token** authentication for temporary and secure access.
- 🧠 **"Remember Me"** functionality to improve user experience.
- 🧪 Unit and integration testing to ensure code quality, using the **H2** in-memory database.
- 🎨 Dynamic templates built with **Thymeleaf**.
- 📢 Validation messages and notifications for actions such as user logout, user creation, and more.


## 🎥 Preview on YouTube

[![Video Thumbnail](https://img.youtube.com/vi/287Uao4CTqc/0.jpg)](https://www.youtube.com/watch?v=287Uao4CTqc)



## 🖼️ Login Preview

![image](https://github.com/user-attachments/assets/f90035dd-e38b-42e2-816b-d299687e266d)



## 🗄️ Database Diagram for User and Token Persistence

![image](https://github.com/user-attachments/assets/9c2ef2a8-5c8f-4f35-b57c-7d52b2242e93)



### 🛠️ Technologies Used:
- **Spring Security** 🛡️
- **Thymeleaf** 🍃
- **H2 Database** 🗃️
- **OAuth 2.0** 🌐
- **FIDO2/WebAuthn** 🔐
- **JUnit** 🧪
- **HTML/CSS/JS** 🎨
- **Spring Boot** 🚀
- **Spring Data JPA** 📦
- **WebAuthn4J** 🔑
- **Lombok** ⚙️
- **Spring MVC** 🌍
- **Thymeleaf Extras** 🍂
- **Spring Testing** 🧪
- **Spring Actuator** 📊
- **Spring DevTools** 🔧


---

### 📂 Project Structure:

<pre>
src/
├── main/
│ ├── java/
│ │ └── com/authentication/borghi/
│ │ ├── controller/ # Controllers for handling requests
│ │ ├── dto/ # Data Transfer Objects (DTOs)
│ │ ├── entity/ # JPA entities
│ │ │ ├── onetimeToken/ # One-Time Token related entities
│ │ │ └── user/ # User-related entities
│ │ ├── exceptions/ # Custom exceptions
│ │ ├── filter/ # Custom filters
│ │ ├── repository/ # Data access repositories
│ │ ├── security/ # Security configurations and handlers
│ │ │ └── handler/ # Custom security handlers
│ │ ├── service/ # Business logic and services
│ │ │ ├── email/ # Email-related services
│ │ │ ├── onetimeToken/ # One-Time Token services
│ │ │ └── user/ # User-related services
│ │ └── strategy/ # Authentication strategies
│ │ └── admin/ # Admin-related strategies
│ ├── resources/ # Static resources and templates
│ │ ├── static/ # Static files (CSS, JS, etc.)
│ │ │ ├── css/ # CSS files
│ │ │ └── js/ # JavaScript files
│ │ ├── templates/ # Thymeleaf templates
│ │ └── application.properties # Main configuration file
│ └── test/ # Tests
│ ├── java/ # Test source code
│ │ └── com/authentication/borghi/
│ │ ├── constants/ # Test constants
│ │ ├── controller/ # Controller tests
│ │ ├── integration/ # Integration tests
│ │ ├── repository/ # Repository tests
│ │ ├── security/ # Security tests
│ │ └── service/ # Service tests
│ └── resources/ # Test resources
│ └── application-test.properties # Test configuration file
└── BorginApplication.java # Main application class
</pre>



