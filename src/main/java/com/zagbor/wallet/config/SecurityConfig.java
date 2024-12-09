package com.zagbor.wallet.config;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

//    @Bean
//    @Primary
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers("/error").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .permitAll()
//                        .defaultSuccessUrl("/", true)
//                )
//                .logout(logout -> logout.permitAll())
//                .build();
//    }

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(requests -> requests
                        .anyRequest().permitAll()  // Allow all requests without authentication
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Custom login page
                        .permitAll()  // Allow access to the login page for everyone
                        .defaultSuccessUrl("/", true)  // Redirect after successful login
                )
                .logout(logout -> logout.permitAll())  // Allow logout for everyone
                .csrf().disable()  // Disable CSRF if it's not required (could be causing issues with POST requests)
                .build();
    }


    @Bean
    public CommandLineRunner loginAutomatically() {
        return args -> {
            // Создаем аутентификацию с заданными данными
            User principal = new User("josh@example.com", "pw",
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));  // Пример с ролью

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal, "password", principal.getAuthorities());

            // Устанавливаем аутентификацию в SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

        };
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var josh = User.withUsername("anonymousUser")
                .password(passwordEncoder.encode("pw"))
                .roles("USER")
                .build();
        var rob = User.withUsername("rob@example.com")
                .password(passwordEncoder.encode("pw"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(josh, rob);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


