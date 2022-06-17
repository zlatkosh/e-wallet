package com.zlatkosh.ewallet.service.playsession;

import com.zlatkosh.ewallet.service.security.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import static com.zlatkosh.ewallet.service.security.JwtUtility.BEARER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/session")
@Slf4j
public class PlaySessionController {
    private final PlaySessionService playSessionService;
    private final ApplicationContext context;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Play session successfully created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content),
    })
    @Operation(description = "This method is used to create a play session for an existing User. " +
            "One user can have multiple play sessions. " +
            "A play session is automatically created upon a successful login and has the same expiration as the generated refresh token." +
            "Meaning a new play session is created with the next login after the refresh token has expired.")
    @PutMapping("/create")
    public void createPlaySession(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION).substring(BEARER_PREFIX.length());
        JwtUtility jwtUtility = context.getBean(JwtUtility.class);
        String username = jwtUtility.getDecodedJWT(accessToken).getSubject();
        log.info("Creating a play session for user: '%s'".formatted(username));
        try {
            Long playSessionId = playSessionService.createNewPlaySession(username, jwtUtility.extractMetadataFromTokenString(accessToken).getPlaySessionEndDate());
            log.info("Successfully created a play session with id '%d' for user '%s'".formatted(playSessionId, username));
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request: %s".formatted(e.getMessage()));
        }
    }
}
