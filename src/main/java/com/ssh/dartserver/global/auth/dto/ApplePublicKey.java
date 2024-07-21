package com.ssh.dartserver.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Apple OAuth 2.0을 통해 발급받는 JWT 토큰은 헤더, 페이로드, 서명으로 구성된 JSON Web Token (JWT) 형식을 따릅니다. 전체적으로 Header, Payload, Signature 구조로 이루어져 있으며 각 항목은 다음과 같습니다.
 *
 * <p>이 공개 키 정보는 Apple의 JWT 토큰의 서명을 검증하는 데 사용됩니다. JWT의 헤더에서 kid 값을 읽어 동일한 kid 값을 가진 공개 키를 찾아 n과 e 값을 사용하여 서명을 검증할 수 있습니다.
 * </p>
 * <ul>
 * <li>
 *     {@code kty} Header에 해당하는 부분으로 암호화 Key의 유형을 나타냅니다.
 * </li>
 * <li>
 *     {@code kid} 키의 식별자를 나타내며, JWT의 헤더에 있는 kid와 일치하는 값을 찾아야 합니다.
 * </li>
 * <li>
 *     {@code use} 키의 용도를 나타냅니다. sig는 서명 검증에 사용된다는 의미입니다.
 * </li>
 * <li>
 *     {@code alg} 서명에 사용된 알고리즘을 나타냅니다. RS256은 RSA SHA-256 알고리즘을 의미합니다.
 * </li>
 * <li>
 *     {@code n} RSA 키의 모듈러스를 Base64Url 인코딩한 값입니다. RSA 공개 키의 중요한 구성 요소 중 하나입니다.
 * </li>
 * <li>
 *     {@code e} RSA 키의 지수를 Base64Url 인코딩한 값입니다. 일반적으로 AQAB는 65537이라는 값을 나타내며, RSA 공개 키의 또 다른 중요한 구성 요소입니다.
 * </li>
 * page.
 * </ul>
 *
 * <p>실제 값은 다음 사이트와 같은 형태입니다.
 * <a href="https://appleid.apple.com/auth/keys">
 * Apple Auth Keys Example</a>.
 *
 * @author  최현식
 * @see AppleTokenRequest
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplePublicKey {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
