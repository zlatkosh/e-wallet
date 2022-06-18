package com.zlatkosh.ewallet.service.playsession;

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
        return playSessionRepository.createPlaySession(username, endTime);
    }

}
