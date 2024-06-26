package com.kalsym.userservice.configs;

import com.kalsym.userservice.filters.SessionRequestFilter;
import com.kalsym.userservice.services.MySQLUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 *
 * @author Sarosh
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MySQLUserDetailsService mySQLUserDetailsService;

    @Autowired
    private SessionAuthenticationEntryPoint sessionAuthenticationEntryPoint;

    @Autowired
    SessionRequestFilter sessionRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(mySQLUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests().antMatchers(
                        "/guest/**",
                        "/clients/session/refresh",
                        "/clients/getToken",
                        "/clients/authenticate",
                        "/clients/loginoauth",
                        "/clients/applecallback/**",
                        "/clients/**/email-verification/**/verify",
                        "/clients/register",
                        "/clients/**/password_reset",
                        "/clients/**/password/**/reset",
                        "/customers/session/refresh",
                        "/customers/authenticate",
                        "/customers/register",
                        "/customers/loginoauth",
                        "/customers/applecallback/**",
                        "/customers/{id}/email-verification/{code}/verify",
                        "/administrators/session/refresh",
                        "/administrators/authenticate",
                        "/administrators/register",
                        "/authorities/bulk",
                        "/sessions/details",
                        "/error",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/swagger-config",
                        "/docs/**",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(sessionAuthenticationEntryPoint).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and().csrf().disable();;
        httpSecurity.addFilterBefore(sessionRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
