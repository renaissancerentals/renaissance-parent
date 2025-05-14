package com.renaissancerentals.assets.error;

import com.renaissancerentals.foundation.error.BusinessException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.Getter;

@Getter
public class AssetsBusinessException extends BusinessException {
    private final AssetsErrorCode errorCode;

    public AssetsBusinessException(AssetsErrorCode errorCode, Throwable cause) {
        super(ErrorMessage.builder().code(errorCode.name()).message(errorCode.message()).build(), cause);
        this.errorCode = errorCode;
    }

}
