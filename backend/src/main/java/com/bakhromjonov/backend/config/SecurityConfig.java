package com.bakhromjonov.backend.config;

import com.bakhromjonov.backend.security.AuthEntryPoint;
import com.bakhromjonov.backend.security.CustomUserDetailsService;
import com.bakhromjonov.backend.security.RestAuthenticationEntryPoint;
import com.bakhromjonov.backend.security.TokenAuthenticationFilter;
import com.bakhromjonov.backend.security.oauth2.CustomOAuth2UserService;
import com.bakhromjonov.backend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.bakhromjonov.backend.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.bakhromjonov.backend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    private final AuthEntryPoint authEntryPoint;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookiesOAuth2AuthorizationRequestRepository;


    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                   .and()
                .sessionManagement()
                   .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and()
                .csrf()
                   .disable()
                .formLogin()
                   .disable()
                .httpBasic()
                   .disable()
                .exceptionHandling()
                   .authenticationEntryPoint(authEntryPoint)
                   .and()
                .authorizeRequests()
                   .antMatchers("/",
                        "/error",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                        .permitAll()
                   .antMatchers("/auth/**", "/oauth2/**")
                        .permitAll()
                   .anyRequest()
                        .authenticated()
                   .and()
                .oauth2Login()
                   .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(httpCookiesOAuth2AuthorizationRequestRepository)
                        .and()
                   .redirectionEndpoint()
                        .baseUri("/oauth2/callback/*")
                        .and()
                   .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                        .and()
                   .successHandler(oAuth2AuthenticationSuccessHandler)
                   .failureHandler(oAuth2AuthenticationFailureHandler);

        http.addFilterBefore(tokenAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
    }
}


