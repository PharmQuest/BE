package com.pharmquest.pharmquest.common.apiPayload.exception.handler;

import com.pharmquest.pharmquest.common.apiPayload.code.BaseErrorCode;
import com.pharmquest.pharmquest.common.apiPayload.exception.GeneralException;

public class CommonExceptionHandler extends GeneralException {

    public CommonExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
