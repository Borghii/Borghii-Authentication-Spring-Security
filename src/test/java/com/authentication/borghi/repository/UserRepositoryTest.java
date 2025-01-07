package com.authentication.borghi.repository;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.Role;
import com.authentication.borghi.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindUserByUsername() {

        // Given
        String username = "oli";
        User user = new User(username, "password", "name", "surname", "email@example.com", new Role());
        user.setRole(new Role(user,"ROLE_USER"));

        underTest.save(user);

        // When
        Optional<User> userOptional = underTest.findByUsername(username);

        // Then
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(foundUser -> {
                    assertThat(foundUser.getUsername()).isEqualTo(username);
                    assertThat(foundUser.getName()).isEqualTo("name");
                    assertThat(foundUser.getSurname()).isEqualTo("surname");
                    assertThat(foundUser.getEmail()).isEqualTo("email@example.com");
                });
    }

    @Test
    void itShouldntFindUserByUsername() {

        // Given
        String username = "oli";

        // When
        Optional<User> userOptional = underTest.findByUsername(username);

        // Then
        assertThat(userOptional).isNotPresent();
    }
}