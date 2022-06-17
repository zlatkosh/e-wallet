package com.zlatkosh.ewallet.service.wallet;

import com.zlatkosh.ewallet.model.db.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@RequiredArgsConstructor
@Repository
class WalletRepositoryPostgres implements WalletRepository {
    static final String INSERT_WALLET_SQL = """
            INSERT INTO e_wallet_pg.public.wallet(username, current_balance) VALUES (?,?::numeric)
            """;
    private static final String DO_TRANSACTION_SQL = """
            UPDATE e_wallet_pg.public.wallet wt SET current_balance = current_balance::money::numeric + ? WHERE username = ?
            RETURNING
                (SELECT current_balance FROM e_wallet_pg.public.wallet WHERE username = wt.username) old_balance,
                current_balance new_balance;
            """;
    final JdbcTemplate jdbcTemplate;

    @Override
    public void insertWallet(String username) {
        jdbcTemplate.update(INSERT_WALLET_SQL, username, 0);
    }

    @Override
    public Transaction doTransaction(String username, Transaction transaction) {
        return jdbcTemplate.queryForObject(DO_TRANSACTION_SQL,
                (rs, rowNum) -> mapTransaction(rs, transaction),
                transaction.getTxAmount(), username);
    }

    private static Transaction mapTransaction(ResultSet rs, Transaction transaction) throws SQLException {
        transaction.setOldBalance(rs.getBigDecimal("old_balance"));
        transaction.setNewBalance(rs.getBigDecimal("new_balance"));
        transaction.setTxTime(new Date());

        return transaction;
    }
}