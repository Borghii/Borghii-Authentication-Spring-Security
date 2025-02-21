package com.authentication.borghi.security;

import com.authentication.borghi.filter.FilterChainExceptionHandler;
import com.authentication.borghi.security.handler.CustomAccessDeniedHandler;
import com.authentication.borghi.security.handler.CustomAuthenticationFailureOTT;
import com.authentication.borghi.security.handler.CustomAuthenticationSuccessHandler;
import com.authentication.borghi.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import org.springframework.security.web.webauthn.management.JdbcPublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.JdbcUserCredentialRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Value("${REMEMBER_ME_KEY}")
    private String rememberMeKey;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private FilterChainExceptionHandler filterChainExceptionHandler;

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandlerOTT() {
        return new CustomAuthenticationFailureOTT();
    }


    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }


    @Bean
    public JdbcPublicKeyCredentialUserEntityRepository jdbcPublicKeyCredentialRepository(JdbcOperations jdbc) {
        return new JdbcPublicKeyCredentialUserEntityRepository(jdbc);
    }

    @Bean
    public JdbcUserCredentialRepository jdbcUserCredentialRepository(JdbcOperations jdbc) {
        return new JdbcUserCredentialRepository(jdbc);
    }





    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
            //agregamos filtro para capturar exceptiones que ocurrent dentro de los filtros
            //y manejarlas con nuestro global exception handler
            .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)

            .csrf(Customizer.withDefaults())

            //.requiresChannel(channel -> channel.anyRequest().requiresSecure()) // Enforce HTTPS


//            .headers(headers -> headers
//                    .httpStrictTransportSecurity(hsts -> hsts
//                            .includeSubDomains(true)
//                            .preload(true)
//                            .maxAgeInSeconds(31536000) // 1 año
//                    )
//                    .contentSecurityPolicy(csp -> csp
//                            .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data: https://pngimg.com https://cdn1.iconfinder.com; font-src 'self';")
//                    )
//                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin // Permite que la página se cargue en un frame del mismo origen
//                    )
//                    .xssProtection(xss -> xss
//                            .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
//                    )
//                    .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable // O habilita con .enable()
//                    )
//            )

            .sessionManagement(session -> session
                    .sessionFixation().migrateSession() // Cambia el ID de sesión después del login
                    .maximumSessions(1) // Solo permite una sesión por usuario
                    .maxSessionsPreventsLogin(false) // Si el usuario ya tiene una sesión activa, la nueva sesión invalidará la anterior
                    .expiredUrl("/showMyCustomLogin?expiredSession") // Redirige a la página de login cuando la sesión caduca
            )

            .authorizeHttpRequests((authorize) ->
                    authorize
                        //gracias a esto me carga el css (no es necesario autenticacion para recurso estaticos)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/showCreateAccount",
                                        "/register/**",
                                        "/access-denied",
                                        "/showMyCustomLogin",
                                        "/oauth2/**",
                                        "/ott/sent",
                                        "/login/ott",
                                        "/ott/generate",
                                        "/favicon.ico",
                                        "/error/username-not-found",

                                        "/webauthn/authenticate/options",
                                        "/login/webauthn"


                                                        ).permitAll()
                        .requestMatchers("/home").authenticated()
                        .requestMatchers("/webauthn/register/*").authenticated()
                        .requestMatchers("/userinfo").hasAnyAuthority("ROLE_USER", "ROLE_OIDC_USER")
                        .anyRequest().authenticated()
            )

            .oauth2Login(form ->
                    form
                            .loginPage("/showMyCustomLogin")
                            .defaultSuccessUrl("/home",true)
                            .successHandler(customAuthenticationSuccessHandler())
                            .permitAll()

            )

            .formLogin(form->
                    form
                            .loginPage("/showMyCustomLogin")
                            .loginProcessingUrl("/authenticateTheUser")
                            .defaultSuccessUrl("/home",true)
                            .successHandler(customAuthenticationSuccessHandler())
                            .permitAll()
            )

            .logout(logout -> logout
                    .logoutUrl("/logout") // URL para el logout (por defecto es "/logout")
                    .logoutSuccessUrl("/showMyCustomLogin?logout") // A dónde redirigir tras cerrar sesión
                    .invalidateHttpSession(true) // Invalida la sesión de usuario
                    .deleteCookies("JSESSIONID") // Elimina la cookie JSESSIONID (sesión)
                    .clearAuthentication(true) // Limpia la autenticación
                    .permitAll() // Permite acceso público al endpoint de logout
            )

            .oneTimeTokenLogin(ott -> ott
                    .authenticationSuccessHandler((req, res, auth) -> res.sendRedirect("/home"))
                    .authenticationFailureHandler(customAuthenticationFailureHandlerOTT())
            )

            .rememberMe(rememberMe -> rememberMe
                    .tokenRepository(persistentTokenRepository())  // Configuración de almacenamiento
                    .tokenValiditySeconds(14 * 24 * 60 * 60)      // Duración del token (14 días)
                    .key(rememberMeKey)                    // Clave para la cookie
            )

            .webAuthn((webAuthn) -> webAuthn
                    .rpName("Spring Security Relying Party")
                    .rpId("localhost")
                    .allowedOrigins("http://localhost:8080")

            )

            .exceptionHandling(exception ->
                    exception
                            .accessDeniedHandler(customAccessDeniedHandler())
            );


    return http.build();
}






}
