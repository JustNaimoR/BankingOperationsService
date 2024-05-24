package effectiveMobile.bank.security.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWTResponse - класс с содержанием необходимых для jwt полей
 */

@Data
@AllArgsConstructor
public class JwtResponse {
    @Schema(example = "token")
    private String accessToken;
    @Schema(example = "token")
    private String refreshToken;
}