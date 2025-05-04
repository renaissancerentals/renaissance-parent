package com.renaissancerentals.assets.error;

import com.renaissancerentals.error.ClientException;
import com.renaissancerentals.error.ErrorMessage;

public class AssetsClientException extends ClientException {
    public AssetsClientException(AssetsErrorCode errorCode, Throwable cause) {
        super(ErrorMessage.builder().code(errorCode.name()).message(errorCode.message()).build(), cause);
    }
}
