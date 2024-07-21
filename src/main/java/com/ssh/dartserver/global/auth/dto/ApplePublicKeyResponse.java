package com.ssh.dartserver.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplePublicKeyResponse {
    private List<ApplePublicKey> keys;
}
