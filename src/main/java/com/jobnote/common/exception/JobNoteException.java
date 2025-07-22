package com.jobnote.common.exception;

import com.jobnote.common.api.ResponseCode;
import lombok.Getter;

@Getter
public class JobNoteException extends RuntimeException {
    private final ResponseCode responseCode;

    public JobNoteException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}
