package com.zlatkosh.ewallet.wallet;

import com.zlatkosh.ewallet.model.controller.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@RequiredArgsConstructor
@Repository
public class WalletRepositoryPostgres implements WalletRepository {
    static final String INSERT_WALLET_SQL = """
            INSERT INTO e_wallet_pg.public.wallet(username, current_balance) VALUES (?,?::numeric)
            """;
    private static final String DO_TRANSACTION_SQL = """
            UPDATE e_wallet_pg.public.wallet wt SET current_balance = current_balance::numeric + ? WHERE username = ?
            RETURNING
                (SELECT current_balance::numeric FROM e_wallet_pg.public.wallet WHERE username = wt.username) old_balance,
                current_balance::numeric new_balance;
            """;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insertWallet(String username) {
        jdbcTemplate.update(INSERT_WALLET_SQL, username, 0);
    }

    @Override
    public TransactionDto doTransaction(String username, TransactionDto transactionDto) {
        return jdbcTemplate.queryForObject(DO_TRANSACTION_SQL,
                (rs, rowNum) -> mapTransaction(rs, transactionDto),
                transactionDto.getTxAmount(), username);
    }

    private static TransactionDto mapTransaction(ResultSet rs, TransactionDto transactionDto) throws SQLException {
        transactionDto.setOldBalance(rs.getBigDecimal("old_balance"));
        transactionDto.setNewBalance(rs.getBigDecimal("new_balance"));
        transactionDto.setTxTime(new Date());

        return transactionDto;
    }
}