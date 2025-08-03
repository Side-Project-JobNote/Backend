package com.jobnote.domain.user.dto;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.VerificationToken;

public record SignUpEvent(
        User user,
        VerificationToken verificationToken
) {
}
