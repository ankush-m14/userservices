package scaler.com.userservices.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//"@Bean" in a Spring application, it means you're declaring a special object (a bean)
// that Spring will create and manage for you. This can be handy when you need more control
// over how objects are created or when you want to integrate with external libraries
// that aren't automatically managed by Spring.

// when you mark a class with "@Configuration", you're telling Spring that
// it contains bean definitions (usually defined by methods annotated with @Bean).
// These beans are managed by Spring's IoC (Inversion of Control) container and
// can be used throughout your application.

@Configuration
public class SpringSecurity {
//    @Bean
//    public SecurityFilterChain filteringCriteria(HttpSecurity http) throws Exception {
//        http.cors().disable();
//        http.csrf().disable();
//
//
//        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//
//        return http.build();
//    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

