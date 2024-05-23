package effectiveMobile.bank.security.service;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    /**
     * SECRET_KEY - подпись для jwt refresh и access токенов
     * ACCESS_EXPIRATION - время действия access jwt токена - 1 минута
     * REFRESH_EXPIRATION - время действия refresh jwt токена - 1 день
     */
    @Value("${bankingOperationsService.jwt.SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${bankingOperationsService.jwt.ACCESS_EXPIRATION_MS}")              // 1 minute
    private int ACCESS_EXPIRATION;
    @Value("${bankingOperationsService.jwt.REFRESH_EXPIRATION_MS}")             // 1 day
    private long REFRESH_EXPIRATION;

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(Map.of("dummy", "I_AM_A_TEAPOT"), userDetails, REFRESH_EXPIRATION);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        Date issuedDate = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(SECRET_KEY))
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * @param token          - jwt token
     * @param claimsResolver - используемый метод для извлечения нужных полей из jwt
     * @param <T>            - класс извлекаемого Claim
     * @return необходимое поле T
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException ignored) {
            BankingOperationsServiceApplication.logger.warn("token is expired");
        } catch (UnsupportedJwtException ignored) {
            BankingOperationsServiceApplication.logger.warn("unsupported token");
        } catch (MalformedJwtException ignored) {
            BankingOperationsServiceApplication.logger.warn("malformed token");
        } catch (Exception ignored) {
            BankingOperationsServiceApplication.logger.warn("token is invalid");
        }
        BankingOperationsServiceApplication.logger.warn("the token that came is invalid");

        return false;
    }

    public boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    // Декодирование ключа для подписи в jwt
    private Key getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}