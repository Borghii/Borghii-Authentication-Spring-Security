package com.authentication.borghi.service;


import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.Role;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserDetail;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.ui.Model;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Log
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void processAuthenticatedUser(Object principal, Model model) {

        // Caso: Usuario autenticado con Google (OIDC)
        if (principal instanceof OAuth2User oAuth2User) {
            try {
                saveOauthUser(oAuth2User);
            } catch (UserAlreadyExist e) {
                log.info(e.getMessage());
            }
            model.addAttribute("name", oAuth2User.getAttribute("name"));
        }

        // Caso: Usuario autenticado con detalles locales (UserDetails)
        else if (principal instanceof UserDetails userDetails) {
            log.info("Email: " + userDetails.getUsername());
            model.addAttribute("name", userDetails.getUsername());
        }
    }

    @Override
    @Transactional
    public void saveUserFromDTO(UserDTO userDTO){

        if (userRepository.existsByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail())){
            throw new UserAlreadyExist("Username or email already used");
        }

        User user = fromDTO(userDTO);

        user.setRole(new Role(user,"ROLE_USER"));

        if (! (user.getPassword() == null)) user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("User saved: "+userDTO.getEmail());

        userRepository.save(user);
    }

    @Override
    public void saveOauthUser(OAuth2User oAuth2User) {
        UserDTO userDTO = new UserDTO();

        if (oAuth2User instanceof OidcUser oidcUser) {
            userDTO = UserDTO.builder()
                    .email(oidcUser.getEmail())
                    .providerId(oidcUser.getSubject())
                    .provider(oidcUser.getIssuer().toString().split("\\.")[1])
                    .name(oidcUser.getName())
                    .surname(oidcUser.getFamilyName())
                    .build();
        } else if (oAuth2User instanceof DefaultOAuth2User oauth2User ) {
            Map<String, Object> values = oauth2User.getAttributes();

            userDTO = UserDTO.builder()
                    .provider("github")
                    .providerId(values.get("id").toString())
                    .email(values.getOrDefault("Email",values.get("id").toString()+"@gmail.com").toString())
                    .build();
        }

        saveUserFromDTO(userDTO);
    }

    private User fromDTO(UserDTO userDTO){
        User user = new User();

        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        //OAuth
        user.setProvider(userDTO.getProvider());
        user.setProviderId(userDTO.getProviderId());

        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        userDetail.setName(userDTO.getName());
        userDetail.setSurname(userDTO.getSurname());

        user.setUserDetail(userDetail);

        return user;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleName()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional
    public void updateLastLoginByEmail(String email, LocalDateTime currentTime) {
        userRepository.updateLastLoginByEmail(email,currentTime);
    }
}
