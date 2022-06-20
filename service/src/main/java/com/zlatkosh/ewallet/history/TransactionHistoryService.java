package com.zlatkosh.ewallet.history;

import com.zlatkosh.ewallet.history.TransactionHistoryRepository;
import com.zlatkosh.ewallet.model.controller.PlaySessionDto;
import com.zlatkosh.ewallet.model.controller.TransactionDto;
import com.zlatkosh.ewallet.model.controller.UserDataDto;
import com.zlatkosh.ewallet.model.db.PlayerTransactionHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;

    public UserDataDto getPlayerTransactionHistory(String username) {

        Stream<PlayerTransactionHistory> playerTransactionHistoryStream = transactionHistoryRepository.getPlayerTransactionHistory(username);
        final UserDataDto userDataDto = UserDataDto.builder().playSessions(new ArrayList<>()).build();
        playerTransactionHistoryStream.forEach(mapToUserDataDtoConsumer(userDataDto));

        return userDataDto;
    }

    private Consumer<PlayerTransactionHistory> mapToUserDataDtoConsumer(UserDataDto userDataDto) {
        return playerTransactionHistory -> {
            mapToUserDataDto(userDataDto, playerTransactionHistory);
            PlaySessionDto playSessionDto = initPlaySessionDto(userDataDto, playerTransactionHistory);
            initTransactionDto(playerTransactionHistory, playSessionDto);
        };
    }

    private void initTransactionDto(PlayerTransactionHistory playerTransactionHistory, PlaySessionDto playSessionDto) {
        playSessionDto.getTransactions()
                .add(TransactionDto.builder()
                        .txId(playerTransactionHistory.getTxId())
                        .txType(playerTransactionHistory.getTxType())
                        .txTime(playerTransactionHistory.getTxTime())
                        .txAmount(playerTransactionHistory.getTxAmount())
                        .oldBalance(playerTransactionHistory.getOldBalance())
                        .newBalance(playerTransactionHistory.getNewBalance())
                        .build()
                );
    }

    private PlaySessionDto initPlaySessionDto(UserDataDto userDataDto, PlayerTransactionHistory playerTransactionHistory) {

        Long sessionId = playerTransactionHistory.getSessionId();
        Optional<PlaySessionDto> existingPlaySessionDto = userDataDto.getPlaySessions()
                .stream()
                .filter(playSessionDto -> Objects.equals(playSessionDto.getSessionId(), sessionId)).findFirst();
        PlaySessionDto playSessionDto;
        if (existingPlaySessionDto.isPresent()) {
            playSessionDto = existingPlaySessionDto.get();
        } else {
            playSessionDto = PlaySessionDto.builder()
                    .sessionId(sessionId)
                    .sessionStartTime(playerTransactionHistory.getSessionStartTime())
                    .sessionEndTime(playerTransactionHistory.getSessionExpTime())
                    .transactions(new ArrayList<>())
                    .build();
            userDataDto.getPlaySessions().add(playSessionDto);
        }
        return playSessionDto;
    }

    private void mapToUserDataDto(UserDataDto userDataDto, PlayerTransactionHistory playerTransactionHistory) {
        if (userDataDto.getUsername() == null) {
            userDataDto.setUsername(playerTransactionHistory.getUsername());
        }
    }
}
