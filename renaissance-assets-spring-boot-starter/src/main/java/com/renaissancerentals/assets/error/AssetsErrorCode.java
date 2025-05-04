package com.renaissancerentals.assets.error;

import java.text.MessageFormat;

public enum AssetsErrorCode {
    UNAUTHORIZED_ACCESS_ERROR("Unauthorized access"), ASSET_INPUT_OUTPUT_ERROR("IO Error"), ASSET_UPLOAD_ERROR(
            "Upload Error: {0}"), ASSET_NOT_FOUND_ERROR("{0}");

    private final String message;

    AssetsErrorCode(String message) {
        this.message = message;
    }

    public String message(){
        return message;
    }

    public String format(Object... args){
        return MessageFormat.format(message,args);
    }
}
