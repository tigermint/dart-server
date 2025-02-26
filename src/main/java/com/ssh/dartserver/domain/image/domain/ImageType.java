package com.ssh.dartserver.domain.image.domain;

public enum ImageType {
    URL,  // URL 형식의 이미지
    BASE64  // Base64 형식의 파일 데이터
    ;
    public boolean isUrl() {
        return this == URL;
    }

    public boolean isBase64() {
        return this == BASE64;
    }
}
