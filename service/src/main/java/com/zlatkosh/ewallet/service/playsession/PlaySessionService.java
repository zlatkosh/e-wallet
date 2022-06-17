package com.zlatkosh.ewallet.service.playsession;

import com.zlatkosh.ewallet.model.exception.EWalletException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class PlaySessionService {

    private final PlaySessionRepository playSessionRepository;

    public Long createNewPlaySession(String username, Date endTime) {
        try {
            return playSessionRepository.createPlaySession(username, endTime);
        } catch (Exception e) {
            String message = "Failed to create a session for user '%s', endDate '%s'.".formatted(username, endTime);
            log.error(message, e);
            throw new EWalletException(message);
        }
    }

}
