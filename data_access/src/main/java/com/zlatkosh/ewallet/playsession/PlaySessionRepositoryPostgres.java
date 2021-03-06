package com.zlatkosh.ewallet.playsession;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@RequiredArgsConstructor
@Repository
class PlaySessionRepositoryPostgres implements PlaySessionRepository {
    private static final String CREATE_PLAY_SESSION_SQL = """
            INSERT INTO e_wallet_pg.public.play_session(username, start_time, end_time) VALUES (?, now(), ?)  RETURNING session_id
            """;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long createPlaySession(String username, Date endTime) {
        return jdbcTemplate.queryForObject(CREATE_PLAY_SESSION_SQL, Long.class, username, endTime);
    }
}
