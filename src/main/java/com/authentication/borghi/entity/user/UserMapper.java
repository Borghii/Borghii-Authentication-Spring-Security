package com.authentication.borghi.entity.user;

import com.authentication.borghi.dto.UserDTO;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserMapper {

    public User fromDTO(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        // OAuth
        user.setProvider(userDTO.getProvider());
        user.setProviderId(userDTO.getProviderId());

        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        userDetail.setName(userDTO.getName());
        userDetail.setSurname(userDTO.getSurname());

        user.setUserDetail(userDetail);
        return user;
    }

    public UserDTO fromOAuth2User(OAuth2User oAuth2User) {
        if (oAuth2User instanceof OidcUser oidcUser) {
            return UserDTO.builder()
                    .email(oidcUser.getEmail())
                    .providerId(oidcUser.getSubject())
                    .provider(getProviderNameFromIssuer(oidcUser.getIssuer().toString()))
                    .name(oidcUser.getGivenName())
                    .surname(oidcUser.getFamilyName())
                    .build();
        } else if (oAuth2User instanceof DefaultOAuth2User defaultOAuth2User) {
            Map<String, Object> attributes = defaultOAuth2User.getAttributes();
            return UserDTO.builder()
                    .provider("github")
                    .providerId(attributes.get("id").toString())
                    .email(attributes.get("id") + "@gmail.com")
                    .build();
        }
        throw new IllegalArgumentException("Unsupported OAuth2 user type.");
    }

    private String getProviderNameFromIssuer(String issuerUrl) {
        try {
            return issuerUrl.split("\\.")[1]; // Manejar posibles errores en caso de formato inesperado
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid issuer URL format: " + issuerUrl, e);
        }
    }
}
