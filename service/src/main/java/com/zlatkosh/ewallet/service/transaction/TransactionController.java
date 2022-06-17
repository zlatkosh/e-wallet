package com.zlatkosh.ewallet.service.transaction;

import com.zlatkosh.ewallet.model.controller.BalanceIncrease;
import com.zlatkosh.ewallet.model.db.Transaction;
import com.zlatkosh.ewallet.model.security.JwtMetadata;
import com.zlatkosh.ewallet.service.security.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

import static com.zlatkosh.ewallet.service.security.JwtUtility.BEARER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tx")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
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
    @Operation(description = "This method is used to create a wallet for an existing User. " +
            "The limitation is that only a single wallet can exist for any given user.")
    @PutMapping("/balance/increase")
    @ResponseBody
    public void createIncreaseBalanceTx(HttpServletRequest request, @RequestBody @Validated BalanceIncrease balanceIncrease) {
        String accessToken = request.getHeader(AUTHORIZATION).substring(BEARER_PREFIX.length());
        JwtUtility jwtUtility = context.getBean(JwtUtility.class);
        JwtMetadata jwtMetadata = jwtUtility.extractMetadataFromTokenString(accessToken);
        log.info("Creating a transaction for '%s'".formatted(balanceIncrease));
        try {
            Transaction transaction = Transaction.builder()
                    .sessionId(jwtMetadata.getPlaySessionId())
                    .txAmount(balanceIncrease.getTransactionAmount())
                    .txType(balanceIncrease.getTxType())
                    .build();
            transactionService.createIncreaseBalanceTx(jwtMetadata.getUsername(), transaction);
            log.info("Successfully created a transaction for '%s'".formatted(balanceIncrease));
        } catch (Exception e) {
            log.error("ExceptionReturned: ", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request: %s".formatted(e.getMessage()));
        }
    }
}
