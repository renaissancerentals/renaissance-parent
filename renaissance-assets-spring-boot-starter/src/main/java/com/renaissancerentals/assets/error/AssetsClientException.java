package com.renaissancerentals.assets.error;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

public class AssetsClientException extends ClientException {
    public AssetsClientException(AssetsErrorCode errorCode, Throwable cause) {
        super(
                ErrorMessage.builder()
                        .code(errorCode.name())
                        .message(errorCode.message())
                        .build(),
                cause);
    }
}
