package com.unicore.common.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.unicore.common.handler.UnicoreAuthenticationFailureHandler;
import com.unicore.common.handler.UnicoreLogoutSuccessHandler;
import com.unicore.common.handler.UnicoreLoginSuccessHandler;

import cn.hsa.hsaf.auth.security.filter.RefererFilter;
import cn.hsa.hsaf.auth.security.filter.UserContextFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(name = { "security.type" }, havingValue = "unicore-sso", matchIfMissing = true)
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityAuthenticationProvider provider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UnicoreLoginSuccessHandler loginSuccessHandler = new UnicoreLoginSuccessHandler();
        loginSuccessHandler.setAlwaysUseDefaultTargetUrl(true);
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/home", "/login", "/logout", "/user/forceLogout**", "/captcha", "/api/auth/**",
                        "/index.html", "/assets/**", "/favicon.ico",
                        "/doc.html",
                        "/swagger-ui.html",
                        "/swagger-ui.html/**",
                        "/webjars/**",
                        "/*/api-docs",
                        "/v2/**",
                        "/**/swagger-resources",
                        "/**/swagger-resources/configuration/ui",
                        "/**/swagger-resources/configuration/security",
                        "/api/sys/config/key/**")
                .permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .successHandler(loginSuccessHandler)
                .failureHandler(new UnicoreAuthenticationFailureHandler("/login?error=true"))
                .permitAll()
                .and()
                .headers().frameOptions().disable()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessHandler(new UnicoreLogoutSuccessHandler())
                .and()
                .addFilterAfter(new UserContextFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new RefererFilter(), UserContextFilter.class)
                // 配置没有权限的自定义处理类
                .exceptionHandling()
                .accessDeniedPage("/403");

        // 设置跨域问题
        http.cors().configurationSource(corsConfigurationSource()).and()
                // 禁用 CSRF（前后端分离项目通常不需要 CSRF）
                .csrf().disable();
        // 单用户登录，新登录会踢掉旧session
        http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/statics/assets/**", "/favicon.ico");
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(false);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private CsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
        cookieCsrfTokenRepository.setCookiePath("/");
        cookieCsrfTokenRepository.setCookieHttpOnly(false);
        cookieCsrfTokenRepository.setCookieName("XSRF-TOKEN");
        cookieCsrfTokenRepository.setHeaderName("XSRF-TOKEN");
        return cookieCsrfTokenRepository;
    }

    /**
     * 自定义验证逻辑
     * 
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(provider);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
