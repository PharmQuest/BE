package com.pharmquest.pharmquest.global.apiPayload.exception.handler;

import com.pharmquest.pharmquest.global.apiPayload.code.BaseErrorCode;
import com.pharmquest.pharmquest.global.apiPayload.exception.GeneralException;

public class CommonExceptionHandler extends GeneralException {

    public CommonExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
