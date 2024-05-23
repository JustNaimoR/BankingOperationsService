package effectiveMobile.bank.security.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWTResponse - класс с содержанием необходимых для jwt полей
 */

@Data
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}