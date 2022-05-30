package com.swp.backend.security;

import com.swp.backend.constance.ApiEndpointProperties;
import com.swp.backend.entity.UserEntity;
import com.swp.backend.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    RestAccessDenyEntryPoint restAccessDenyEntryPoint;
    RestUnauthorizedEntryPoint restUnauthorizedEntryPoint;
    UserService userService;
    JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(RestAccessDenyEntryPoint restAccessDenyEntryPoint, RestUnauthorizedEntryPoint restUnauthorizedEntryPoint, UserService userService, JwtTokenFilter jwtTokenFilter) {
        this.restAccessDenyEntryPoint = restAccessDenyEntryPoint;
        this.restUnauthorizedEntryPoint = restUnauthorizedEntryPoint;
        this.userService = userService;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> {
            UserEntity userEntity = userService.findUserByUsername(username);
            if(userEntity == null){
                throw new UsernameNotFoundException("Username " + username + " notfound!");
            }else {
                return org.springframework.security.core.userdetails.User.withUsername(String.valueOf(userEntity.getUserId()))
                        .password(userEntity.getPassword())
                        .authorities(new SimpleGrantedAuthority(userEntity.getRole()))
                        .build();
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Enable CORS
        http.cors();
        //Disable CSRF
        http.csrf().disable();

        //Enable stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        //Set access denied handler
        http.exceptionHandling().accessDeniedHandler(restAccessDenyEntryPoint);
        //Set unauthorized handler
        http.exceptionHandling().authenticationEntryPoint(restUnauthorizedEntryPoint);
        //Disable form login default spring security 5.0
        http.formLogin().disable();
        //Disable login http basic
        http.httpBasic().disable();

        //Public endpoints
        http.authorizeHttpRequests().antMatchers(ApiEndpointProperties.publicEndpoint).permitAll();
        //Permission endpoints
        http.authorizeHttpRequests().anyRequest().authenticated();
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // Remove the ROLE_ prefix default spring security generate
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
