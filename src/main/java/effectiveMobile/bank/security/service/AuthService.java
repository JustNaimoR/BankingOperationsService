package effectiveMobile.bank.security.service;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.exceptions.ValidationException;
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
        BankingOperationsServiceApplication.logger.info("Refreshing tokens...");

        String refreshToken = jwtRequest.getRefreshToken();

        if (jwtService.isRefreshTokenValid(refreshToken)) {
            String username = jwtService.extractRefreshUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String new_accessToken = jwtService.generateAccessToken(userDetails);
            String new_refreshToken = jwtService.generateRefreshToken(userDetails);

            if (newRefresh) {
                BankingOperationsServiceApplication.logger.info("new tokens: access - {} refresh - {}",
                        new_accessToken, new_refreshToken);
            } else {
                BankingOperationsServiceApplication.logger.info("new access token - {}",
                        new_accessToken);
            }

            return new JwtResponse(new_accessToken, newRefresh? new_refreshToken: null);
        } else {
            BankingOperationsServiceApplication.logger.warn("Refresh-token is invalid");

            throw new ValidationException("Refresh-token is invalid");
//            return new JwtResponse(null, null);
        }
    }

    public Authentication getCurAuthenticatedUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}