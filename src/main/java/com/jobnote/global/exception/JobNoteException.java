package com.jobnote.global.exception;

import com.jobnote.global.common.ResponseCode;
import lombok.Getter;

@Getter
public class JobNoteException extends RuntimeException {
    private final ResponseCode responseCode;

    public JobNoteException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}
