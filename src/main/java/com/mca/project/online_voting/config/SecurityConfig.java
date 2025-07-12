package com.mca.project.online_voting.config;
import com.mca.project.online_voting.entity.Company;
import com.mca.project.online_voting.entity.UserRole;
import com.mca.project.online_voting.entity.Users;
import com.mca.project.online_voting.repository.CompanyRepository;
import com.mca.project.online_voting.repository.UserRepository;
import com.mca.project.online_voting.repository.UserRoleRepository;
import com.mca.project.online_voting.service.CompanyService;
import com.mca.project.online_voting.service.UserDetailsServiceImpl;
import com.mca.project.online_voting.service.UserRoleService;
import com.mca.project.online_voting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize etc.
public class SecurityConfig {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserRoleRepository userRoleRepository;

    private  final  UserRepository userRepository;

    SecurityConfig(UserRepository userRepository){
        this.userRepository = userRepository;
    }



    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Inject your custom UserDetailsService

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure AuthenticationManager for Basic Auth
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // You might need this if you want to expose AuthenticationManager directly
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    //     return authConfig.getAuthenticationManager();
    // }

    // --- SECURITY FILTER CHAIN 1: For OAuth2 (Admin) and public access ---
    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http, OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) throws Exception {
        http
                .securityMatcher(
                        "/",
                        "/error",
                        "/webjars/**",
                        "/oauth2/**",
                        "/login**", // Keep this for any default Spring login pages
                        "/login/oauth2/code/**", // <--- ADD THIS LINE FOR GOOGLE CALLBACK
                        "/api/public",
                        "/admin/**",

                        "/api/login",
                        "/api/companies/**",
                        "/api/elections/**",
                        "/api/electionGroup/**",
                        "/api/user/*",
                        "/api/hello",
                        "/api/logout",
                        "/api/roles",
                        "/api/users/*"
                )
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/",
                                "/error",
                                "/webjars/**",
                                "/login**",
                                "/oauth2/**",
                                "/login/oauth2/code/**", // <--- ALSO ADD HERE FOR AUTHORIZATION RULES
                                "/api/public",

                                "/api/login",
                                "/api/user/*",
                                "/api/hello",
                                "/api/logout",
                                "/api/roles",
                                "/api/users/*"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .defaultSuccessUrl("http://localhost:3000/dashboard", true)
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                )
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(Customizer.withDefaults())
                );
        return http.build();
    }

    // --- SECURITY FILTER CHAIN 2: For Basic/Form Authentication (Other Users) ---
    @Bean
    public SecurityFilterChain basicAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/login", "/api/user/**", "/api/hello", "/api/logout") // Apply this chain to specific API paths
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .cors(Customizer.withDefaults()) // Enable CORS

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/login").permitAll() // Allow unauthenticated access to the login endpoint itself
                        .requestMatchers("/api/user/**", "/api/hello").authenticated() // Require authentication for user info/hello
                        .anyRequest().authenticated() // All other requests matched by this filter chain require authentication
                )
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/api/login") // The URL to which the login form data is submitted
                        .usernameParameter("email") // Name of the request parameter for username (email in our case)
                        .passwordParameter("password") // Name of the request parameter for password
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(200); // Send 200 OK for successful login (for AJAX/SPA)
                            // You might send back user details or a simple success message
                            response.getWriter().write("{\"message\": \"Login successful\", \"username\": \"" + authentication.getName() + "\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401); // Send 401 Unauthorized for failed login
                            response.getWriter().write("{\"message\": \"Login failed: " + exception.getMessage() + "\"}");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200); // Indicate successful logout for SPA
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .authenticationProvider(authenticationProvider()); // Associate with your UserDetailsService
        // No .oauth2Login or .oauth2ResourceServer here

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin")); // Add common headers
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ... (WebClient and OAuth2AuthorizedClientManager beans remain the same as before)
    @Bean
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder()
                .filter(oauth2Client)
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .clientCredentials()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(UserService userService) {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            String email = oidcUser.getEmail();

            Users user = userRepository.findByEmail(email).orElse(null);

            List<GrantedAuthority> grantedAuthorities = new ArrayList<>(oidcUser.getAuthorities());
            if (user != null) {
                List<UserRole> userRoles = userRoleService.findByUserId(user.getId());
                userRoles.forEach(userRole ->
                        grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole()))
                );
            } else {
                try {
                Users u = Users.builder()
                        .name(email)
                        .password("*****")
                        .phone("")
                        .mailVerification(true)
                        .email(email)
                        .userId(email)
                        .gender(null)
                        .photo(null)
                        .regdDate(LocalDateTime.now())
                        .build();
                Users newUser = userService.createUser(u);

                Company c = Company.builder()
                        .name("Company_"+ generateRandomString(5))
                        .logo(null)
                        .createdBy(newUser.getId().toString())
                        .createdDate(LocalDateTime.now())
                        .status("A")
                        .approvedBy(newUser.getId().toString())
                        .approvedDate(LocalDateTime.now())
                        .comment("Default created and approved by system")
                        .lastUpdateDate(LocalDateTime.now())
                        .build();
                Company newCompany = companyService.createCompany(c);

                UserRole userRole = new UserRole(newUser.getId(), newCompany.getId(), "ADMIN", newCompany, newUser);
                userRoleRepository.save(userRole);

                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));


                    } catch (Exception e) {
                        System.err.println("Error auto-provisioning admin user: " + e.getMessage());
                    }

            }
            return new DefaultOidcUser(grantedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }

    private String generateRandomString(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random(); // Not cryptographically secure

        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive.");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex =  random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}