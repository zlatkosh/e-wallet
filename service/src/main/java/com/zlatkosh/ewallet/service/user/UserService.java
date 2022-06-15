package com.zlatkosh.ewallet.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Service
public class UserService {
    private final UserRepository userRepository;

    public boolean userExists(String username) {
        return userRepository.userExists(username);
    }

    public List<String> getUserRoles(String username) {
        return userRepository.getUserRoles(username);
    }
}
