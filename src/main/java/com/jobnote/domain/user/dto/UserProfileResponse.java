package com.jobnote.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jobnote.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(access = AccessLevel.PRIVATE)
public record UserProfileResponse(
        String email,
        String nickname,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDateTime createdDate
) {

        public static UserProfileResponse from(final User user) {
                return UserProfileResponse.builder()
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .createdDate(user.getCreatedDate())
                        .build();
        }
}
