package com.zlatkosh.ewallet.playsession;

import java.util.Date;

interface PlaySessionRepository {
    Long createPlaySession (String username, Date endTime);
}
