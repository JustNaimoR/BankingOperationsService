package effectiveMobile.bank.security.service;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.security.entity.UserDetailsImpl;
import effectiveMobile.bank.security.payload.JwtResponse;
import effectiveMobile.bank.security.payload.LoginRequest;
import effectiveMobile.bank.security.payload.RefreshJwtRequest;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private UserDetailsServiceImpl userDetailsService;

    //todo исключение при неверном пароле - какое и настроить обработку
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        BankingOperationsServiceApplication.logger.info("Authenticating user...");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getLogin(), loginRequest.getPassword()));

        BankingOperationsServiceApplication.logger.info("Authentication is successful");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtService.generateAccessToken(userDetails);
        String jwtRefresh = jwtService.generateRefreshToken(userDetails);

        return new JwtResponse(jwtToken, jwtRefresh);
    }

    public JwtResponse refreshTokens(RefreshJwtRequest jwtRequest, boolean newRefresh) {
        String refreshToken = jwtRequest.getRefreshToken();

        if (jwtService.isTokenValid(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String new_accessToken = jwtService.generateAccessToken(userDetails);
            String new_refreshToken = jwtService.generateRefreshToken(userDetails);

            return new JwtResponse(new_accessToken, newRefresh? new_refreshToken: null);
        } else {
            return new JwtResponse(null, null);
        }
    }
}