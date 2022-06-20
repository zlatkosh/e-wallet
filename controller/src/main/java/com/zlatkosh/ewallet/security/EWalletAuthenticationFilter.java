package com.zlatkosh.ewallet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zlatkosh.ewallet.model.security.JwtMetadata;
import com.zlatkosh.ewallet.playsession.PlaySessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class EWalletAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final PlaySessionService playSessionService;

    @Value("${app.security.expiry.refresh-token}")
    private long refreshTokenExpiry;

    public EWalletAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtility jwtUtility, PlaySessionService playSessionService) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
        this.playSessionService = playSessionService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = (User)authResult.getPrincipal();
        Date playSessionEndDate = new Date(System.currentTimeMillis()+ refreshTokenExpiry);
        Long playSessionId = playSessionService.createNewPlaySession(user.getUsername(), playSessionEndDate);
        JwtMetadata jwtMetadata = JwtMetadata.builder()
                .username(user.getUsername())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .playSessionId(playSessionId)
                .playSessionEndDate(playSessionEndDate)
                .build();
        String accessToken = jwtUtility.generateAccessToken(jwtMetadata);
        String refreshToken = jwtUtility.generateRefreshToken(jwtMetadata);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
