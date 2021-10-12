package com.fmi.reviews.config;

import com.fmi.reviews.service.UserService;
import com.fmi.reviews.web.rest.FilterChainExceptionHandlerFilter;
import com.fmi.reviews.web.rest.JwtAuthenticationEntryPoint;
import com.fmi.reviews.web.rest.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.stereotype.Controller;

import static com.fmi.reviews.model.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Controller
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(POST, "/api/login", "/api/register").permitAll()
                .antMatchers(GET, "/api/reviews").permitAll()
                .antMatchers(POST, "/api/reviews").hasAnyRole(REVIEWER.toString(), ADMINISTRATOR.toString(), MODERATOR.toString())
                .antMatchers(PUT, "/api/reviews/**").hasAnyRole(REVIEWER.toString(), ADMINISTRATOR.toString(), MODERATOR.toString())
                .antMatchers(DELETE, "/api/reviews/**").hasAnyRole(REVIEWER.toString(), ADMINISTRATOR.toString(), MODERATOR.toString())
                .antMatchers(GET, "/api/movies").permitAll()
                .antMatchers(POST, "/api/movies").hasAnyRole(ADMINISTRATOR.toString(), MODERATOR.toString())
                .antMatchers(PUT, "/api/movies/**").hasAnyRole(ADMINISTRATOR.toString(), MODERATOR.toString())
                .antMatchers(DELETE, "/api/movies/**").hasAnyRole(ADMINISTRATOR.toString(), MODERATOR.toString())
                .antMatchers("/api/users/**").hasRole(ADMINISTRATOR.toString())
                .antMatchers("/api/users").hasRole(ADMINISTRATOR.toString())
                .antMatchers("/**").permitAll()
                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .defaultSuccessUrl("/movies")
                .permitAll();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(filterChainExceptionHandlerFilter, LogoutFilter.class);
    }

    @Bean
    public UserDetailsService getUserDetailsService(UserService userService) {
        return userService::getUserByUsername;
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
