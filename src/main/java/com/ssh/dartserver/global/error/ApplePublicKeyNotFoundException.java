package com.ssh.dartserver.global.error;

public class ApplePublicKeyNotFoundException extends RuntimeException {
    public ApplePublicKeyNotFoundException(String message) {
        super(message);
    }
}
