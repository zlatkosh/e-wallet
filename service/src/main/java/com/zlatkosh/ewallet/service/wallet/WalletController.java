package com.zlatkosh.ewallet.service.wallet;

import com.zlatkosh.ewallet.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
@Slf4j
public class WalletController {
    private final WalletService walletService;
    private final UserService userService;

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
    @Operation(description = "This method is used to create a wallet for an existing User. " +
            "The limitation is that only a single wallet can exist for any given user.")
    @PutMapping("/create")
    public void createWallet(@RequestHeader("username") @NotNull @NotEmpty String username) {
        log.info("Creating a wallet for user: '%s'".formatted(username));
        if (!userService.userExists(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User '%s' does not exist! Failed for create a wallet for it.".formatted(username));
        }
        try {
            walletService.addNewWallet(username);
            log.info("Successfully created a wallet for user '%s'".formatted(username));
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request: %s".formatted(e.getMessage()));
        }
    }
}
