package com.zlatkosh.ewallet.service.wallet;

import com.zlatkosh.ewallet.service.user.UserService;
import lombok.RequiredArgsConstructor;
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
public class WalletController {
    private final WalletService walletService;
    private final UserService userService;

    @PutMapping("/create")
    public void createWallet(@RequestHeader("username") @NotNull @NotEmpty String username) {
        if (!userService.userExists(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User '%s' does not exist! Failed for create a Wallet for it.".formatted(username));
        }
        try {
            walletService.addNewWallet(username);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request: %s".formatted(e.getMessage()));
        }
    }
}
