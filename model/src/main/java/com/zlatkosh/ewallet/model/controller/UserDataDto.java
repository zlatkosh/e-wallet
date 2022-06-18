package com.zlatkosh.ewallet.model.controller;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
public final class UserDataDto implements Serializable {
    private String username;
    private final List<PlaySessionDto> playSessions;
}
