package effectiveMobile.bank.security.payload;

import lombok.Getter;

@Getter
public class RefreshJwtRequest {
    private String refreshToken;
}