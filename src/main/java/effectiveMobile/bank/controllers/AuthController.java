package effectiveMobile.bank.controllers;


import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.exceptions.dto.ExceptionDto;
import effectiveMobile.bank.security.payload.JwtResponse;
import effectiveMobile.bank.security.payload.LoginRequest;
import effectiveMobile.bank.security.payload.RefreshJwtRequest;
import effectiveMobile.bank.security.service.AuthService;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.PersonRegDtoValidator;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(
        name = "Authentication controller",
        description = "контроллер для обработки запросов, связанных с аутентификацией пользователя"
)
public class AuthController {
    private AuthService authService;
    private PersonRegDtoValidator personRegDtoValidator;
    private PersonService personService;


    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "Аутентификация пользователя",
            summary = "Вход в систему пользователем с предоставлением его данных - логин и пароль",
            parameters = @Parameter(
                    name = "loginRequest", description = "логин-пароль пользователя", required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "пользователь вошел в систему",
                            content =  @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "неверные логин или пароль",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
                    )
            }
    )
    public JwtResponse userLogin(@RequestBody LoginRequest loginRequest) {
        BankingOperationsServiceApplication.logger.info("'/login' request received with data: {}", loginRequest);

        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/refresh-access-token")       // Вернуть новый access токен с помощью refresh
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "Обновление токена доступа",
            summary = "Обновление токена доступа через токен обновления",
            parameters = @Parameter(
                    name = "jwtRequest", description = "токен обновления", required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "токен доступа обновлен",
                            content = @Content(mediaType = "application/json", schema = @Schema(
                                    example = "{\"accessToken\": \"token\", \"refreshToken\":null}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "токен обновления невалиден",
                            content = @Content()
                    )
            }
    )
    public JwtResponse token(@RequestBody RefreshJwtRequest jwtRequest) {
        BankingOperationsServiceApplication.logger.info("'/refresh-access-token' request received with data: {}", jwtRequest);

        return authService.refreshTokens(jwtRequest, false);
    }

    @PostMapping("/refresh-tokens")      // Обновить оба токена - refresh и access
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            description = "обновление токенов доступа и обновления",
            summary = "обновление токенов доступа и обновления с помощью токена обновления",
            parameters = @Parameter(
                    name = "refreshJwtRequest", description = "токен обновления", required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "токены обновлены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public JwtResponse refreshTokens(@RequestBody RefreshJwtRequest refreshJwtRequest) {
        BankingOperationsServiceApplication.logger.info("'/refresg-tokens' request received with data: {}'", refreshJwtRequest);

        return authService.refreshTokens(refreshJwtRequest, true);
    }




    @PostMapping("/reg")                        // Регистрация новых пользователей
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            description = "Регистрация пользователя и его банковского аккаунта",
            summary = "Регистрирует пользователя и возвращает токены доступа и обновления",
            parameters = @Parameter(
                    name = "regDto", description = "логин-пароль пользователя", required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "пользователь зарегистрирован",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "невалидные поля пользователя",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
                    )
            }
    )
    public JwtResponse register(@Valid @RequestBody PersonRegDto regDto,
                                BindingResult bindingResult) {
        BankingOperationsServiceApplication.logger.info("'/user/reg' request received to register a new person with values: {}", regDto.toString());

        personRegDtoValidator.validate(regDto, bindingResult);
        personService.registerPerson(regDto);

        return authService.authenticateUser(new LoginRequest(regDto));
    }



    @GetMapping("/list")                    // получение списка всех пользователей
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "список зарегистрированных пользователей",
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonListItemDto.class))
            )
    )
    public List<PersonListItemDto> getPeople() {
        return personService.getPeople();
    }

    @GetMapping("/dummy")           // для проверки работы jwt
    @Hidden
    public void dummy() {}
}