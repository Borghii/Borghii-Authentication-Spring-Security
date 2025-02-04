package com.authentication.borghi.service.onetimetoken;

import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.repository.OneTimeTokenRepository;
import com.authentication.borghi.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ott.*;
import org.springframework.stereotype.Service;
import com.authentication.borghi.entity.onetimetoken.OneTimeToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PersistentOneTimeTokenService implements OneTimeTokenService {

    private static final int TOKEN_VALIDITY_MINUTES = 15;


    private final OneTimeTokenRepository tokenRepository;

    private final UserService userService;

    @Autowired
    public PersistentOneTimeTokenService(UserService userService, OneTimeTokenRepository tokenRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public org.springframework.security.authentication.ott.OneTimeToken generate(GenerateOneTimeTokenRequest request){
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();


        User user = userService.findUserByUsername(request.getUsername());



        OneTimeToken token = new OneTimeToken();

        token.setTokenValue(tokenValue);
        token.setUser(user);
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusMinutes(TOKEN_VALIDITY_MINUTES));
        token.setUsed(false);


        tokenRepository.save(token);

        return new DefaultOneTimeToken(token.getTokenValue(),token.getUser().getUsername(), Instant.now());

    }

    @Override
    public org.springframework.security.authentication.ott.OneTimeToken consume(OneTimeTokenAuthenticationToken authenticationToken) {
        OneTimeToken token = tokenRepository.findByTokenValueAndUsedFalse(authenticationToken.getTokenValue())
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Token has expired");
        }


        token.setUsed(true);
        tokenRepository.save(token);


        return new DefaultOneTimeToken(token.getTokenValue(),token.getUser().getUsername(), Instant.now());

    }

    @Scheduled(cron = "@midnight")
    public void removeExpiredTokens(){
        System.out.println("Tokens deleted");
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }


}
