package effectiveMobile.bank.controllers;


import effectiveMobile.bank.security.payload.JwtResponse;
import effectiveMobile.bank.security.payload.LoginRequest;
import effectiveMobile.bank.security.payload.RefreshJwtRequest;
import effectiveMobile.bank.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus
@RequestMapping("")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    //todo если мы логинимся принудительно, но должны вернуть новые access и refresh токены
    @PostMapping("/login")          // Аутентификация пользователя в системе
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse userSignIn(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PutMapping("/refresh-access-token")       // Вернуть новый access токен с помощью refresh
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse token(@RequestBody RefreshJwtRequest jwtRequest) {
        return authService.refreshTokens(jwtRequest, false);
    }

    @PutMapping("/refresh-tokens")      // Обновить оба токена - refresh и access
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse refreshTokens(@RequestBody RefreshJwtRequest refreshJwtRequest) {
        return authService.refreshTokens(refreshJwtRequest, true);
    }
}