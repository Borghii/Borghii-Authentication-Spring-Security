package com.authentication.borghi.dto;

import com.authentication.borghi.entity.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserDTO implements Serializable {

    public UserDTO(String username, String password, String name, String surname, String email, String provider, String providerId, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    @NotNull(message="Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotNull(message="Password is required")
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;

    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    @Size(max = 50, message = "Surname cannot exceed 50 characters")
    private String surname;

    @Email(message="Email isn't valid")
    @NotNull(message="is required")
    private String email;

    private String provider;
    private String providerId;

    private Role role;

}
