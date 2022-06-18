package com.zlatkosh.ewallet.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class UserRepositoryPostgres implements UserRepository {
    private static final String USER_EXISTS_SQL = """
            SELECT EXISTS(SELECT * FROM e_wallet_pg.public.user_data WHERE username = ?)
            """;
    private static final String GET_ROLES_SQL = """
    SELECT role_name FROM e_wallet_pg.public.role WHERE username = ?
            """;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Boolean userExists(String username) {
        return jdbcTemplate.queryForObject(USER_EXISTS_SQL, Boolean.class, username);
    }

    @Override
    public List<String> getUserRoles(String username) {
        return jdbcTemplate.queryForList(GET_ROLES_SQL, String.class, username);
    }
}
