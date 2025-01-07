package com.authentication.borghi.entity;

import com.authentication.borghi.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {

    public User(String username, String password, String name, String surname, String email, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }

    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser;


    @NotNull(message="Username is required")
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull(message="Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Email(message="Email isn't valid")
    @NotNull(message="is required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Role role;


}
