package com.zlatkosh.ewallet.service.wallet;

import com.zlatkosh.ewallet.model.exception.EWalletException;
import com.zlatkosh.ewallet.service.security.JwtUtility;
import com.zlatkosh.ewallet.service.user.UserService;
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
@RequestMapping("/wallet")
@Slf4j
public class WalletController {
    private final WalletService walletService;
    private final UserService userService;
    private final ApplicationContext context;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet successfully created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "406", description = "Not acceptable.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content),
    })
    @Operation(description = "This controller is used to create a wallet for an existing User. " +
            "The limitation is that only a single wallet can exist for any given user.")
    @PutMapping("/create")
    public void createWallet(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION).substring(BEARER_PREFIX.length());
        JwtUtility jwtUtility = context.getBean(JwtUtility.class);
        String username = jwtUtility.getDecodedJWT(accessToken).getSubject();
        log.info("Creating a wallet for user: '%s'".formatted(username));
        if (!userService.userExists(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User '%s' does not exist! Failed for create a wallet for it.".formatted(username));
        }
        try {
            walletService.createNewWallet(username);
            log.info("Successfully created a wallet for user '%s'".formatted(username));
        } catch (EWalletException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request: %s".formatted(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal Server Error: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: %s".formatted(e.getMessage()));
        }
    }
}
