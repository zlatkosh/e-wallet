package com.zlatkosh.ewallet.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zlatkosh.ewallet.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class JwtUtility {
    private static final String SECRET_KEY = "e-wallet-secret";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
    private static final int MINUTE = 60 * 1000;
    private static final String ROLES = "roles";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final String ISSUER = "e-wallet";

    @Autowired
    private ApplicationContext context;

    public UsernamePasswordAuthenticationToken verifyAccessTokenString(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim(ROLES).asList(String.class);
        List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public DecodedJWT getDecodedJWT(String token) {
        JWTVerifier verifier = JWT.require(ALGORITHM).build();
        return verifier.verify(token);
    }

    public User extractUserFromRefreshTokenString(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        String username = decodedJWT.getSubject();
        UserService userService = context.getBean(UserService.class);
        if (userService.userExists(username)) {
            List<String> roles = userService.getUserRoles(username);
            List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            return new User(username, "DUMMY_PASS", authorities);
        }
        return null;
    }
    public String generateAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+ 10 * MINUTE))
                .withIssuer(ISSUER)
                .withClaim(ROLES, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(ALGORITHM);
    }

    public String generateRefreshToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+ 60 * MINUTE))
                .withIssuer(ISSUER)
                .sign(ALGORITHM);
    }
}
