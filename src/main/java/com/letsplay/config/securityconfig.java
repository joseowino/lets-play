package com.letsplay.config;

import com.letsplay.security.JwtAuthenticationEntryPoint;
import com.letsplay.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the Let's Play API.
 * 
 * This configuration implements:
 * - JWT-based authentication
 * - Role-based access control (ADMIN, USER)
 * - Stateless session management
 * - CORS policies
 * - Public endpoints for product viewing and authentication
 * - Protected endpoints for product/user management
 * 
 * @author Jose Owino
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    /**
     * Creates the JWT authentication filter bean.
     * This filter intercepts requests and validates JWT tokens.
     * 
     * @return JwtAuthenticationFilter instance
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * Configures the password encoder using BCrypt.
     * BCrypt automatically handles salt generation and provides strong hashing.
     * 
     * Strength: 10 rounds (default) - good balance between security and performance
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication provider with custom UserDetailsService and password encoder.
     * 
     * @return DaoAuthenticationProvider configured with UserDetailsService and BCrypt
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposes the AuthenticationManager bean for use in authentication operations.
     * Required for manual authentication in login endpoint.
     * 
     * @param authConfig Spring's authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * 
     * Allows:
     * - Specific origins (localhost:3000, localhost:4200 for development)
     * - Standard HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
     * - All headers
     * - Credentials (cookies, authorization headers)
     * 
     * For production, update allowed origins to your frontend domain.
     * 
     * @return CorsConfigurationSource with defined CORS rules
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (update for production)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",     // React default
            "http://localhost:4200",     // Angular default
            "http://localhost:8081",     // Alternative port
            "https://your-frontend.com"  // Production frontend (update this)
        ));
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        // Expose headers to client
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type",
            "X-Total-Count"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Main security filter chain configuration.
     * 
     * Security Rules:
     * 1. Public endpoints (no authentication required):
     *    - POST /api/auth/register - User registration
     *    - POST /api/auth/login - User login
     *    - GET /api/products - View all products
     *    - GET /api/products/{id} - View single product
     * 
     * 2. Protected endpoints (authentication required):
     *    - POST /api/products - Create product (USER, ADMIN)
     *    - PUT /api/products/{id} - Update product (owner or ADMIN)
     *    - DELETE /api/products/{id} - Delete product (owner or ADMIN)
     *    - GET /api/users - List users (ADMIN only)
     *    - Other user management endpoints (restricted)
     * 
     * Session Management: Stateless (no server-side sessions)
     * CSRF: Disabled (not needed for JWT-based APIs)
     * 
     * @param http HttpSecurity object for configuration
     * @return SecurityFilterChain with configured security rules
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (not needed for stateless JWT authentication)
            .csrf(csrf -> csrf.disable())
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure exception handling
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler)
            )
            
            // Set session management to stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                
                // Admin-only endpoints
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                
                // User management - self or admin
                .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                
                // Product management - authenticated users
                .requestMatchers(HttpMethod.POST, "/api/products").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        // Set custom authentication provider
        http.authenticationProvider(authenticationProvider());
        
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}