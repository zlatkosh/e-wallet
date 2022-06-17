package com.zlatkosh.ewallet.service.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class WalletRepositoryPostgres implements WalletRepository {
    static final String INSERT_WALLET_SQL = """
            INSERT INTO e_wallet_pg.public.wallet(username, current_balance) VALUES (?,?::numeric)
            """;
    final JdbcTemplate jdbcTemplate;

    @Override
    public void insertWallet(String username) {
        jdbcTemplate.update(INSERT_WALLET_SQL, username, 0);
    }
}