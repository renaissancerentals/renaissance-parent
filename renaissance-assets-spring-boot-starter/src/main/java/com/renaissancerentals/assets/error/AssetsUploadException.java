package com.renaissancerentals.assets.error;

import com.renaissancerentals.error.BusinessException;
import com.renaissancerentals.error.ErrorMessage;

import lombok.Getter;

@Getter
public class AssetsUploadException extends BusinessException {

    public AssetsUploadException(String message) {
        super(ErrorMessage.builder().code(AssetsErrorCode.ASSET_UPLOAD_ERROR.name())
                .message(AssetsErrorCode.ASSET_UPLOAD_ERROR.format(message)).build());

    }
}
