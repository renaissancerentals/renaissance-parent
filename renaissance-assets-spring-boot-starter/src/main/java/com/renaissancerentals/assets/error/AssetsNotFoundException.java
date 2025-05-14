package com.renaissancerentals.assets.error;

import com.renaissancerentals.foundation.error.BusinessException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.Getter;

@Getter
public class AssetsNotFoundException extends BusinessException {

    public AssetsNotFoundException(String message) {
        super(ErrorMessage.builder().code(AssetsErrorCode.ASSET_NOT_FOUND_ERROR.name())
                .message(AssetsErrorCode.ASSET_NOT_FOUND_ERROR.format(message)).build());

    }
}
