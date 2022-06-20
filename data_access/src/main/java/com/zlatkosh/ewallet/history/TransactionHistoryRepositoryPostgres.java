package com.zlatkosh.ewallet.history;

import com.zlatkosh.ewallet.model.db.PlayerTransactionHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
class TransactionHistoryRepositoryPostgres implements TransactionHistoryRepository {
    private static final String PLAYER_TX_HISTORY_SQL = """
            SELECT ps.username,
                   ps.session_id,
                   ps.start_time session_start_time,
                   ps.end_time   session_exp_time,
                   t.tx_id,
                   t.tx_type,
                   t.tx_time,
                   t.tx_amount::numeric,
                   t.old_balance::numeric,
                   t.new_balance::numeric
            FROM e_wallet_pg.public.play_session ps
                     INNER JOIN e_wallet_pg.public.transaction t on ps.session_id = t.session_id
            WHERE ps.username = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Stream<PlayerTransactionHistory> getPlayerTransactionHistory(String username) {
        return jdbcTemplate.queryForStream(PLAYER_TX_HISTORY_SQL, this::mapToPlayerTransactionHistory, username);
    }

    private PlayerTransactionHistory mapToPlayerTransactionHistory(ResultSet rs, int rowNum) throws SQLException {
        return PlayerTransactionHistory.builder()
                .username(rs.getString("username"))
                .sessionId(rs.getLong("session_id"))
                .sessionStartTime(rs.getTimestamp("session_start_time"))
                .sessionExpTime(rs.getTimestamp("session_exp_time"))
                .txId(rs.getLong("tx_id"))
                .txType(rs.getString("tx_type"))
                .txTime(rs.getTimestamp("tx_time"))
                .txAmount(rs.getBigDecimal("tx_amount"))
                .oldBalance(rs.getBigDecimal("old_balance"))
                .newBalance(rs.getBigDecimal("new_balance"))
                .build();
    }
}
