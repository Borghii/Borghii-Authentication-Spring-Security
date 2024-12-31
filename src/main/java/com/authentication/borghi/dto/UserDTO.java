package com.authentication.borghi.dto;

import com.authentication.borghi.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO implements Serializable {

    @NotNull(message="Username is required")
    private String username;

    @NotNull(message="Password is required")
    private String password;

    private String name;

    private String surname;

    @Email(message="Email isn't valid")
    @NotNull(message="is required")
    private String email;

    private Role role;

}
