package com.pharmquest.pharmquest.global.apiPayload.exception;

import com.pharmquest.pharmquest.global.apiPayload.code.BaseErrorCode;
import com.pharmquest.pharmquest.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason(){
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }

}
