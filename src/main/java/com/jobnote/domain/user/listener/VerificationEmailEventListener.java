package com.jobnote.domain.user.listener;

import com.jobnote.domain.user.dto.SignUpEvent;
import com.jobnote.domain.user.service.UserMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class VerificationEmailEventListener {

    private final UserMailService userMailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationEvent(final SignUpEvent signUpEvent) {
        userMailService.sendVerificationEmail(signUpEvent.email(), signUpEvent.token());
    }
}
