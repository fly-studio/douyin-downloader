package com.fly.video.downloader.exception;

import android.annotation.SuppressLint;

public class DownloadFileException extends RuntimeException {
    public DownloadFileException() {
    }

    public DownloadFileException(String message) {
        super(message);
    }

    public DownloadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadFileException(Throwable cause) {
        super(cause);
    }

    @SuppressLint("NewApi")
    public DownloadFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
