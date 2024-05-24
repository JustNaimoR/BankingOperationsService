package effectiveMobile.bank.security.payload;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.util.dto.PersonRegDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LoginRequest - используемый класс для передачи данных с login-page для дальнейшей аутентификации
 */

@Data
@AllArgsConstructor
public class LoginRequest {

    public LoginRequest(PersonRegDto dto) {
        this.login = dto.getLogin();
        this.password = dto.getPassword();
    }

    @NotBlank
    @Schema(example = "login")
    private String login;

    @NotBlank
    @Schema(example = "password")
    private String password;



    @Override
    public String toString() {
        return "LoginRequest [login=" + login + ", password=" + password + "]";
    }
}