package com.authentication.borghi.repository;

import com.authentication.borghi.entity.user.Role;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserDetail;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindUserByUsername() {

        // Given
        String username = "oli";
        User user = new User(username, "password", "email@example.com", null, null, new Role(), new UserDetail());
        user.setRole(new Role(user,"ROLE_USER"));
        user.setUserDetail(new UserDetail("Tomas","Borghi",user));

        underTest.save(user);

        // When
        Optional<User> userOptional = underTest.findByUsername(username);

        // Then
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(foundUser -> {
                    assertThat(foundUser.getUsername()).isEqualTo(username);
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

    @Test
    void existByUsernameOrEmail(){

        //GIVEN

        String username = "oli";
        User user = new User(username, "password", "email@example.com", null, null, new Role(), new UserDetail());
        user.setRole(new Role(user,"ROLE_USER"));
        user.setUserDetail(new UserDetail("Tomas","Borghi",user));

        underTest.save(user);

        //WHEN and THEN
        assertThat(underTest.existsByUsernameOrEmail("oli","email@example.com")).isTrue();
        assertThat(underTest.existsByUsernameOrEmail("oli","as")).isTrue();
        assertThat(underTest.existsByUsernameOrEmail("ewr","email@example.com")).isTrue();
        assertThat(underTest.existsByUsernameOrEmail("asas","as")).isFalse();



    }

    @Test
    @Transactional
    void itShouldUpdateLastLoginByEmail() {
        // GIVEN
        String email = "email@example.com";
        User user = new User("oli", "password", email, null, null, new Role(), new UserDetail());
        user.setRole(new Role(user, "ROLE_USER"));
        user.setUserDetail(new UserDetail("Tomas", "Borghi", user));

        underTest.save(user);

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        // WHEN
        underTest.updateLastLoginByEmail(email,now);
        entityManager.clear(); // Limpia el contexto de persistencia

        // THEN
        User updatedUser = underTest.findByUsername("oli").orElseThrow(() -> new IllegalStateException("User not found"));
        assertThat(updatedUser.getLastLogin()).isNotNull();
        assertThat(updatedUser.getLastLogin()).isEqualTo(now); // Verifica que el tiempo es reciente
    }



}