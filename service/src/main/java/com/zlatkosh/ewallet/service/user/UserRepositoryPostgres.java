package com.zlatkosh.ewallet.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class UserRepositoryPostgres implements UserRepository {
    static final String USER_EXISTS_SQL = """
            SELECT EXISTS(SELECT * FROM user_data WHERE username = ?)
            """;
    final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean userExists(String username) {
        return jdbcTemplate.queryForObject(USER_EXISTS_SQL, getFirstBooleanRowMapper(), username);
    }

    private RowMapper<Boolean> getFirstBooleanRowMapper() {
        return (rs, rowNum) -> rs.getBoolean(1);
    }
}
