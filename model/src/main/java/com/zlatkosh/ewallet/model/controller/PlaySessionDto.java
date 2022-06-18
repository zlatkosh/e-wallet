package com.zlatkosh.ewallet.model.controller;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Builder
@Data
public final class PlaySessionDto {
    private final long sessionId;
    private final Date sessionStartTime;
    private final Date sessionEndTime;
    private final List<TransactionDto> transactions;
}
