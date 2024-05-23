package effectiveMobile.bank.controllers;


import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.security.payload.JwtResponse;
import effectiveMobile.bank.security.payload.LoginRequest;
import effectiveMobile.bank.security.payload.RefreshJwtRequest;
import effectiveMobile.bank.security.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/login")          // Аутентификация пользователя в системе
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse userSignIn(@RequestBody LoginRequest loginRequest) {
        BankingOperationsServiceApplication.logger.info("'/login' request received with data: {}", loginRequest);

        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/refresh-access-token")       // Вернуть новый access токен с помощью refresh
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse token(@RequestBody RefreshJwtRequest jwtRequest) {
        BankingOperationsServiceApplication.logger.info("'/refresh-access-token' request received with data: {}", jwtRequest);

        return authService.refreshTokens(jwtRequest, false);
    }

    @PostMapping("/refresh-tokens")      // Обновить оба токена - refresh и access
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse refreshTokens(@RequestBody RefreshJwtRequest refreshJwtRequest) {
        BankingOperationsServiceApplication.logger.info("'/refresg-tokens' request received with data: {}'", refreshJwtRequest);

        return authService.refreshTokens(refreshJwtRequest, true);
    }

    @GetMapping("/dummy")           // для проверки работы jwt
    public void dummy() {}
}