package com.ssh.dartserver.global.error;

public class KakaoLoginFailedException extends RuntimeException{
    public KakaoLoginFailedException(String message) {
        super(message);
    }
}
