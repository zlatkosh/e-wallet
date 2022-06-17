package com.zlatkosh.ewallet.model.security;

import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record JwtMetadata(String username, List<String> roles, Long playSessionId, Date playSessionEndDate) {
}