package com.authentication.borghi.security;

import com.authentication.borghi.service.UserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {


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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests((authorize) -> authorize

                    //gracias a esto me carga el css (no es necesario autenticacion para recurso estaticos)
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                    .requestMatchers("/showCreateAccount").permitAll()
                    .requestMatchers("/register/**").permitAll()
                    //si no agregas esto al enviar parametros como no tenes permisos se
                    // va a redirigir al login sin parametros

                    .requestMatchers("/showMyCustomLogin").permitAll()
                    .requestMatchers("/oauth2/**").permitAll()

                    .requestMatchers("/home").authenticated()
                    .requestMatchers("/userinfo").authenticated()
                    .anyRequest().authenticated()
            )
            .oauth2Login(form ->
                    form
                            .loginPage("/showMyCustomLogin")
                            .defaultSuccessUrl("/home",true)
                            .permitAll()

            )
            .formLogin(form->
                    form
                            .loginPage("/showMyCustomLogin")
                            .loginProcessingUrl("/authenticateTheUser")
                            .defaultSuccessUrl("/home",true)
                            .permitAll()
            );

    return http.build();
}




}
