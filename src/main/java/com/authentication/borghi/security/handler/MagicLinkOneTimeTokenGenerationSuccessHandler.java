package com.authentication.borghi.security.handler;

import com.authentication.borghi.service.email.SendEmailService;
import com.authentication.borghi.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ott.OneTimeToken;
import com.authentication.borghi.entity.user.User;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class MagicLinkOneTimeTokenGenerationSuccessHandler implements OneTimeTokenGenerationSuccessHandler {

    @Autowired
    private SendEmailService emailService;

    @Autowired
    private UserService userService;

    private final OneTimeTokenGenerationSuccessHandler redirectHandler = new RedirectOneTimeTokenGenerationSuccessHandler("/ott/sent");

    // constructor omitted

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken) throws IOException, ServletException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .path("/login/ott")
                .queryParam("token", oneTimeToken.getTokenValue());

        String magicLink = builder.toUriString();

        sendTokenToUserEmail(oneTimeToken.getUsername(), magicLink);

        System.out.println(magicLink);

        this.redirectHandler.handle(request, response, oneTimeToken);
    }


    private void sendTokenToUserEmail(String username, String magicLink){
        User user = userService.findUserByUsername(username);
        String email = user.getEmail();
        String body = "Use the following link to sign in into the application: " + magicLink;
        String subject = "Your Spring Security One Time Token";
        emailService.sendEmail(email,subject,body);
    }






}
