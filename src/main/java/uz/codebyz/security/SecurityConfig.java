package uz.codebyz.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, OAuthProperties.class})
public class SecurityConfig {

    private final JwtService jwtService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;

    public SecurityConfig(JwtService jwtService,
                          OAuthSuccessHandler oAuthSuccessHandler,
                          OAuthFailureHandler oAuthFailureHandler) {
        this.jwtService = jwtService;
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.oAuthFailureHandler = oAuthFailureHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/auth/**",
                                "/files/**",
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN","STUDENT","TEACHER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuthSuccessHandler)
                        .failureHandler(oAuthFailureHandler)
                )
                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 🔥 Hamma domendan ruxsat (PROD uchun emas, faqat test/temporary)
        config.setAllowedOriginPatterns(List.of("*"));

        // 🔥 Hamma metodlarga ruxsat
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 🔥 Hamma headerlarga ruxsat
        config.setAllowedHeaders(List.of("*"));

        // 🔥 Cookie / Token yuborilishi uchun
        config.setAllowCredentials(true);

        // Expose Authorization header
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
