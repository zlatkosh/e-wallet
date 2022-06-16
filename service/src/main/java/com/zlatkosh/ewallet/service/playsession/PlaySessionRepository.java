package com.zlatkosh.ewallet.service.playsession;

import java.util.Date;

public interface PlaySessionRepository {
    Long createPlaySession (String username, Date endTime);
}
