package com.authentication.borghi.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {



    //add suppor for jdbd no more hardorded users
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests((authorize) -> authorize

                    //gracias a esto me carga el css (no es necesario autenticacion para recurso estaticos)
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form->
                    form
                            .loginPage("/showMyCustomLogin")
                            .loginProcessingUrl("/authenticateTheUser")
                            .permitAll()
            );

    return http.build();
}




}
