package com.zlatkosh.ewallet.service.user;

interface UserRepository {
    Boolean userExists(String username);
}