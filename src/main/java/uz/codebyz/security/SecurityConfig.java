package uz.codebyz.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.*;

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

    /**
     * 1) API SECURITY (JWT) — NO REDIRECT, RETURN 401/403
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🔥 API da redirect bo‘lmasin!
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((req, res, ex) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                        .accessDeniedHandler((req, res, ex) ->
                                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "STUDENT", "TEACHER")
                        .requestMatchers("/api/users/me").hasAnyRole("ADMIN", "STUDENT", "TEACHER")
                        .anyRequest().authenticated()
                )

                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class)

                // API uchun OAuth2 loginni umuman qo‘ymaymiz
                .oauth2Login(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 2) OAUTH2 SECURITY — redirect BO‘LADI (bu normal)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain oauthChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/**", "/swagger-ui/**", "/v3/api-docs/**", "/files/**")
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/files/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuthSuccessHandler)
                        .failureHandler(oAuthFailureHandler)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ✅ FRONTEND QAYERDAN KELISHINI BILMASAK HAM ISHLAYDI
        config.setAllowedOriginPatterns(List.of("*"));

        // ✅ HAMMA HTTP METHODLAR
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // ✅ HAMMA HEADERLAR
        config.setAllowedHeaders(List.of("*"));

        // ✅ FRONTEND Authorization headerni o‘qiy oladi
        config.setExposedHeaders(List.of("Authorization"));

        // ❗ MUHIM: COOKIE ISHLATILMAYDI → false
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
