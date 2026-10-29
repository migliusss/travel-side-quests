package com.example.travelsidequests.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Submitting is open to anyone; reviewing is not. The single admin account comes from
 * `spring.security.user.*`, which is a placeholder for real accounts rather than a
 * substitute for them.
 *
 * CORS is configured here rather than through [org.springframework.web.servlet.config.annotation.WebMvcConfigurer],
 * because the security filter chain runs first and ignores the MVC configuration unless
 * it is handed the same source.
 */
@Configuration
@EnableWebSecurity
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
class SecurityConfig {

    @Bean
    fun corsConfigurationSource(
        @Value("\${app.cors.allowed-origins}") allowedOrigins: List<String>
    ): CorsConfigurationSource =
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/api/**",
                CorsConfiguration().apply {
                    this.allowedOrigins = allowedOrigins
                    allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    allowedHeaders = listOf("*")
                    allowCredentials = true
                }
            )
        }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        // Spring MVC contributes a CorsConfigurationSource of its own, so name the one we want.
        @Qualifier("corsConfigurationSource") cors: CorsConfigurationSource,
    ): SecurityFilterChain =
        http
            .cors { it.configurationSource(cors) }
            // No cookies, no sessions: the admin client sends Basic credentials each time.
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
            }
            .httpBasic(Customizer.withDefaults())
            .build()
}
