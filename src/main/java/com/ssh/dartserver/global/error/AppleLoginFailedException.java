package com.ssh.dartserver.global.error;

public class AppleLoginFailedException extends RuntimeException {
    public AppleLoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    public AppleLoginFailedException(String message) {
        super(message);
    }
}
