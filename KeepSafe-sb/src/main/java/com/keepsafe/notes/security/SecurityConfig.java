package com.keepsafe.notes.security;

import com.keepsafe.notes.config.OAuth2LoginSuccessHandler;
import com.keepsafe.notes.models.AppRole;
import com.keepsafe.notes.models.Role;
import com.keepsafe.notes.models.User;
import com.keepsafe.notes.repositories.RoleRepository;
import com.keepsafe.notes.repositories.UserRepository;
import com.keepsafe.notes.security.JWT.AuthEntryPointJwt;
import com.keepsafe.notes.security.JWT.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; //handler just in case the authentication fails.

    @Autowired
    @Lazy
    OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/public/**").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated()).oauth2Login(oauth2 -> {
                    oauth2.successHandler(oAuth2LoginSuccessHandler);
        });
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)); //The default exception handling mechanism is the unauthorized handler.
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.csrf(AbstractHttpConfigurer::disable);
        //http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //creating some default dummy users at time of application startup using Commandline Runner.
//    @Bean
//    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//
//            //if the role exists, get the role otherwise create the new role into the database.
//            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
//                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));
//
//            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
//                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));
//
//            if (!userRepository.existsByUserName("admin")) {
//                User admin = new User("admin", "admin@gmail.com", passwordEncoder.encode("securepass@123"));
//                admin.setAccountNonLocked(true);
//                admin.setAccountNonExpired(true);
//                admin.setCredentialsNonExpired(true);
//                admin.setEnabled(true);
//                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
//                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
//                admin.setTwoFactorEnabled(false);
//                admin.setSignUpMethod("email");
//                admin.setRole(adminRole);
//                userRepository.save(admin);
//            }
//          };
//    }
}
