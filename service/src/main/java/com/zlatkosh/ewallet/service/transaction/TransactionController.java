package com.zlatkosh.ewallet.service.transaction;

import com.zlatkosh.ewallet.model.controller.BalanceDecreaseDto;
import com.zlatkosh.ewallet.model.controller.BalanceIncreaseDto;
import com.zlatkosh.ewallet.model.controller.TransactionDto;
import com.zlatkosh.ewallet.model.exception.EWalletException;
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
@RequestMapping("/tx/balance")
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final ApplicationContext context;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Increase balance transaction successfully created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "406", description = "Not acceptable.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content),
    })
    @Operation(description = "This controller is used to create a transaction that will deposit funds to the player's wallet. ")
    @PutMapping("/increase")
    @ResponseBody
    public void createIncreaseBalanceTx(HttpServletRequest request, @RequestBody @Validated BalanceIncreaseDto balanceIncreaseDto) {
        String accessToken = request.getHeader(AUTHORIZATION).substring(BEARER_PREFIX.length());
        JwtUtility jwtUtility = context.getBean(JwtUtility.class);
        JwtMetadata jwtMetadata = jwtUtility.extractMetadataFromTokenString(accessToken);
        log.info("Creating a transaction for '%s'".formatted(balanceIncreaseDto));
        try {
            TransactionDto transactionDto = TransactionDto.builder()
                    .sessionId(jwtMetadata.getPlaySessionId())
                    .txAmount(balanceIncreaseDto.getTransactionAmount())
                    .txType(balanceIncreaseDto.getTxType())
                    .build();
            transactionService.createMonetaryTx(jwtMetadata.getUsername(), transactionDto);
            log.info("Successfully created a transaction for '%s'".formatted(balanceIncreaseDto));
        } catch (EWalletException e) {
            log.error("Bad request: ", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request: %s".formatted(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal Server Error: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: %s".formatted(e.getMessage()));
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decrease balance transaction successfully created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content),
            @ApiResponse(responseCode = "406", description = "Not acceptable.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content),
    })
    @Operation(description = "This controller is used to create a transaction that will remove funds from the player's wallet. " +
            "The amount left in the wallet can't go below zero.")
    @PutMapping("/decrease")
    @ResponseBody
    public void createDecreaseBalanceTx(HttpServletRequest request, @RequestBody @Validated BalanceDecreaseDto balanceDecreaseDto) {
        String accessToken = request.getHeader(AUTHORIZATION).substring(BEARER_PREFIX.length());
        JwtUtility jwtUtility = context.getBean(JwtUtility.class);
        JwtMetadata jwtMetadata = jwtUtility.extractMetadataFromTokenString(accessToken);
        log.info("Creating a transaction for '%s'".formatted(balanceDecreaseDto));
        try {
            TransactionDto transactionDto = TransactionDto.builder()
                    .sessionId(jwtMetadata.getPlaySessionId())
                    .txAmount(balanceDecreaseDto.getTransactionAmount().negate())
                    .txType(balanceDecreaseDto.getTxType())
                    .build();
            transactionService.createMonetaryTx(jwtMetadata.getUsername(), transactionDto);
            log.info("Successfully created a transaction for '%s'".formatted(balanceDecreaseDto));
        } catch (EWalletException e) {
            log.error("Bad request: ", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request: %s".formatted(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal Server Error: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: %s".formatted(e.getMessage()));
        }
    }
}
