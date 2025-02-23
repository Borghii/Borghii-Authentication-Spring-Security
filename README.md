# 🔒 Borghii-Authentication-Spring-Security

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
│ │ ├── controller/ # Controladores para manejar solicitudes
│ │ ├── dto/ # Objetos de Transferencia de Datos (DTOs)
│ │ ├── entity/ # Entidades JPA
│ │ │ ├── onetimeToken/ # Entidades relacionadas con tokens de un solo uso
│ │ │ └── user/ # Entidades relacionadas con usuarios
│ │ ├── exceptions/ # Excepciones personalizadas
│ │ ├── filter/ # Filtros personalizados
│ │ ├── repository/ # Repositorios para acceso a datos
│ │ ├── security/ # Configuraciones y manejadores de seguridad
│ │ │ └── handler/ # Manejadores personalizados de seguridad
│ │ ├── service/ # Lógica de negocio y servicios
│ │ │ ├── email/ # Servicios relacionados con correos electrónicos
│ │ │ ├── onetimeToken/ # Servicios para tokens de un solo uso
│ │ │ └── user/ # Servicios relacionados con usuarios
│ │ └── strategy/ # Estrategias de autenticación
│ │ └── admin/ # Estrategias relacionadas con administradores
│ ├── resources/ # Recursos estáticos y plantillas
│ │ ├── static/ # Archivos estáticos (CSS, JS, etc.)
│ │ │ ├── css/ # Archivos CSS
│ │ │ └── js/ # Archivos JavaScript
│ │ ├── templates/ # Plantillas Thymeleaf
│ │ └── application.properties # Archivo de configuración principal
│ └── test/ # Pruebas
│ ├── java/ # Código fuente de pruebas
│ │ └── com/authentication/borghi/
│ │ ├── constants/ # Constantes para pruebas
│ │ ├── controller/ # Pruebas de controladores
│ │ ├── integration/ # Pruebas de integración
│ │ ├── repository/ # Pruebas de repositorios
│ │ ├── security/ # Pruebas de seguridad
│ │ └── service/ # Pruebas de servicios
│ └── resources/ # Recursos para pruebas
│ └── application-test.properties # Configuración para pruebas
└── BorginApplication.java # Clase principal de la aplicación
</pre>



