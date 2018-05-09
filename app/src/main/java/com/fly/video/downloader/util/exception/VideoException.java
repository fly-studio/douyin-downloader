package com.fly.video.downloader.util.exception;

import android.annotation.SuppressLint;

public class VideoException extends RuntimeException {
    public VideoException() {
    }

    public VideoException(String message) {
        super(message);
    }

    public VideoException(String message, Throwable cause) {
        super(message, cause);
    }

    public VideoException(Throwable cause) {
        super(cause);
    }

    @SuppressLint("NewApi")
    public VideoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
