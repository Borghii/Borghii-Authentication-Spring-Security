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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void itShouldFindUserByUsername() {
        // Given
        String username = "oli";
        String email = "email@example.com";
        User user = createUser(username, email);

        userRepository.save(user);

        // When
        Optional<User> optionalUser = userRepository.findByUsername(username);

        // Then
        assertThat(optionalUser).isPresent()
                .hasValueSatisfying(foundUser -> {
                    assertThat(foundUser.getUsername()).isEqualTo(username);
                    assertThat(foundUser.getEmail()).isEqualTo(email);
                });
    }

    @Test
    void itShouldNotFindUserByUsername() {
        // Given
        String username = "nonexistent";

        // When
        Optional<User> optionalUser = userRepository.findByUsername(username);

        // Then
        assertThat(optionalUser).isNotPresent();
    }

    @Test
    void itShouldCheckExistenceByUsernameOrEmail() {
        // Given
        String username = "oli";
        String email = "email@example.com";
        User user = createUser(username, email);

        userRepository.save(user);

        // When & Then
        assertThat(userRepository.existsByUsernameOrEmail(username, email)).isTrue();
        assertThat(userRepository.existsByUsernameOrEmail(username, "nonexistent@example.com")).isTrue();
        assertThat(userRepository.existsByUsernameOrEmail("nonexistent", email)).isTrue();
        assertThat(userRepository.existsByUsernameOrEmail("nonexistent", "nonexistent@example.com")).isFalse();
    }

    @Test
    @Transactional
    void itShouldUpdateLastLoginByEmail() {
        // Given
        String username = "oli";
        String email = "email@example.com";
        User user = createUser(username, email);

        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        // When
        userRepository.updateLastLoginByEmail(email, now);
        entityManager.clear(); // Clear persistence context to avoid caching issues

        // Then
        User updatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        assertThat(updatedUser.getLastLogin()).isNotNull();
        assertThat(updatedUser.getLastLogin()).isEqualTo(now);
    }

    // Helper method to create a User
    private User createUser(String username, String email) {
        User user = new User(username, "password", email, null, null, new Role(), new UserDetail());
        user.setRole(new Role(user, "ROLE_USER"));
        user.setUserDetail(new UserDetail("Tomas", "Borghi", user));
        return user;
    }
}
