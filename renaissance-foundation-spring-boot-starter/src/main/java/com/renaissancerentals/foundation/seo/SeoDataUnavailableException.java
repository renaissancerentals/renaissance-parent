package com.renaissancerentals.foundation.seo;

/** The listing data could not be read, so nothing can be said about the page (not the same as "not found"). */
public class SeoDataUnavailableException extends RuntimeException {

    public SeoDataUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
