package com.zlatkosh.ewallet.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.zlatkosh.ewallet.service.security.JwtUtility.BEARER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/access")
@Slf4j
@RequiredArgsConstructor
public class AccessController {

    @Autowired
    private ApplicationContext context;

    @GetMapping("/refresh_token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            try {
                String refreshToken = authorizationHeader.substring(BEARER_PREFIX.length());
                JwtUtility jwtUtility = context.getBean(JwtUtility.class);
                User user = jwtUtility.extractUserFromRefreshTokenString(refreshToken);
                if (user != null) {

                    String accessToken = jwtUtility.generateAccessToken(user);
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("accessToken", accessToken);
                    tokens.put("refreshToken", refreshToken);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }

            } catch (Exception e) {
                log.error("Login error", e);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("errorMessage", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }
        } else {
            log.error("Refresh token missing");
            throw new ResponseStatusException(
                    FORBIDDEN, "Refresh token missing!");
        }
    }
}