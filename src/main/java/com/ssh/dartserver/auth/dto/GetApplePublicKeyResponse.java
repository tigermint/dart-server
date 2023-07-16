package com.ssh.dartserver.auth.dto;

import com.ssh.dartserver.auth.dto.ApplePublicKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetApplePublicKeyResponse {
    private List<ApplePublicKey> keys;
}
