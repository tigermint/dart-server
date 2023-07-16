package com.ssh.dartserver.common.exception;

public class AppleLoginFailedException extends RuntimeException {
    public AppleLoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
