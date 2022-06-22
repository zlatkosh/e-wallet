package com.zlatkosh.ewallet.history;

import com.zlatkosh.ewallet.model.controller.UserDataDto;
import com.zlatkosh.ewallet.model.security.JwtMetadata;
import com.zlatkosh.ewallet.security.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import static com.zlatkosh.ewallet.security.JwtUtility.BEARER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/history")
@Slf4j
public class TransactionHistoryController {
    private final ApplicationContext context;
    private final TransactionHistoryService transactionHistoryService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved play session and transaction history",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content),
    })
    @Operation(description = "This controller is used to return the player's play session and transaction history. ")
    @GetMapping("/transactions")
    @ResponseBody
    public UserDataDto createIncreaseBalanceTx(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION).substring(BEARER_PREFIX.length());
        JwtUtility jwtUtility = context.getBean(JwtUtility.class);
        JwtMetadata jwtMetadata = jwtUtility.extractMetadataFromTokenString(accessToken);
        String username = jwtMetadata.getUsername();
        log.info("Getting play session and transaction history for user '%s'".formatted(username));
        try {
            UserDataDto playerTransactionHistory = transactionHistoryService.getPlayerTransactionHistory(username);
            log.info("Successfully retrieved play session and transaction history for user '%s'".formatted(username));
            return playerTransactionHistory;
        } catch (Exception e) {
            log.error("Internal Server Error: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: %s".formatted(e.getMessage()));
        }
    }
}
