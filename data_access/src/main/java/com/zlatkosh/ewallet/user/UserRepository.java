package com.zlatkosh.ewallet.user;

import java.util.List;

interface UserRepository {
    Boolean userExists(String username);

    List<String> getUserRoles(String username);
}