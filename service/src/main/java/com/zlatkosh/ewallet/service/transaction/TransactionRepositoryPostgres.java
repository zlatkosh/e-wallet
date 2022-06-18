package com.zlatkosh.ewallet.service.transaction;

import com.zlatkosh.ewallet.model.controller.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TransactionRepositoryPostgres implements TransactionRepository {
    private static final String CREATE_TRANSACTION_SQL = """
            INSERT INTO e_wallet_pg.public.transaction(tx_type, session_id, tx_time, tx_amount, old_balance, new_balance)
            VALUES (?, ?, now(), ?::numeric::money, ?::numeric::money, ?::numeric::money)
            """;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createTransactionRecord(TransactionDto transactionDto) {
        jdbcTemplate.update(CREATE_TRANSACTION_SQL,
                transactionDto.getTxType(),
                transactionDto.getSessionId(),
                transactionDto.getTxAmount(),
                transactionDto.getOldBalance(),
                transactionDto.getNewBalance());
    }
}
