package com.zlatkosh.ewallet.service.user;

import java.util.List;

interface UserRepository {
    Boolean userExists(String username);
}