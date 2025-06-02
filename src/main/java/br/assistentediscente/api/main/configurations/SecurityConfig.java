package br.assistentediscente.api.main.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests.requestMatchers(
                        "/api/authenticate-student",
                        "api/login-fields/*",
                        "api/institutionInformationsTools",
                        "api/generate-response/*").permitAll()
                        .anyRequest().authenticated());
        http.csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(outh2 ->
                        outh2.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter())));



        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(
                jwt -> {
                    List<String> authorities = jwt.getClaimAsStringList("authorities");
                    if (authorities == null){
                        return Collections.emptyList();
                    }
                    JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
                    Collection<GrantedAuthority> scopeAuthorities = scopeConverter.convert(jwt);
                    scopeAuthorities.addAll(authorities.stream().map(SimpleGrantedAuthority::new).toList());
                    return  null;
                }
        );
        return converter;
    }
}
