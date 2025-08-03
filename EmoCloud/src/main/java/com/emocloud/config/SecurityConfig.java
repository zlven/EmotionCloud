package com.emocloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 允许日记和社交相关API无需认证访问（开发测试阶段）
                        .requestMatchers(
                                "/companion/users/{userId}/dialogues",
                                "/companion/dialogues",
                                "/companion/messages/with-response",
                                "/emotion-log/diary/**", // 允许所有日记API
                                "/api/auth/register",
                                "/api/auth/login",
                                "/social/posts",
                                "/social/posts/{postId}",
                                "/social/posts/{postId}/images",
                                "/social/posts/{postId}/images/batch",
                                "/emotion-log/**",
                                "/social/getposts",
                                "/social/posts/{postId}/images/resources",
                                "/social/images/**",
                                "/social/posts/{postId}",
                                "/social/comments",
                                "/companion/messages",
                                "/companion/messages/{messageId}",
                                "/companion/dialogues/{dialogueId}/messages",
                                "/companion/dialogues/{dialogueId}/scene-mode",
                                "/companion/dialogues/{dialogueId}/summary",
                                "/social/posts/{postId}/comments"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}