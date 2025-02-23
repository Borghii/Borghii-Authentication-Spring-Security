# Borghii-Authentication-Spring-Security
The main purpose of this project is to implement different ways to authenticate a user using Spring Security. The project will serve as a comprehensive template for various authentication mechanisms, including:


 - Username/password with PasswordEncoder
 - OpenID (OAuth 2.0) with JSON Web Token (JWT)
 - Passkey authentication (FIDO2/WebAuthn)
 - Remember Me functionality
 - Manage sessions

## Preview Login

![image](https://github.com/user-attachments/assets/f90035dd-e38b-42e2-816b-d299687e266d)

## Diagrama de base de datos para persistencia de usuarios y tokens
![image](https://github.com/user-attachments/assets/9c2ef2a8-5c8f-4f35-b57c-7d52b2242e93)


## Username/password with PasswordEncoder

Realize la autentucaciony de usuarios con DaoAuthenticationProvider con un UserService que implementa UserDetailsService por lo tanto me obliga a implementar User findUserByUsername(String username); que permite obtener a el usuario de la base de datos y crear un UserDetails.

La persitencia de datos la realize atravas de Spring Data haciendo uso de JPA QUERY y todos los metodos prestablecios por spring data para hacer la persistencia y utilizando un UserService para separar capas de responsabilidas. 

Tambien agregue un UserDTO para juntar todos aquellos datos del usuario en una sola entidad para transferir en las diferentes capoas y para ocultar detallas al cliente por seguridad





