package com.renaissancerentals.assets.error;

import com.renaissancerentals.error.BusinessException;
import com.renaissancerentals.error.ErrorMessage;

import lombok.Getter;

@Getter
public class AssetsBusinessException extends BusinessException {
    private final AssetsErrorCode errorCode;

    public AssetsBusinessException(AssetsErrorCode errorCode, Throwable cause) {
        super(ErrorMessage.builder().code(errorCode.name()).message(errorCode.message()).build(), cause);
        this.errorCode = errorCode;
    }

}
