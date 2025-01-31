package com.authentication.borghi.repository;


import com.authentication.borghi.entity.onetimetoken.OneTimeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeToken, Long> {
    Optional<OneTimeToken> findByTokenValueAndUsedFalse(String tokenValue);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
