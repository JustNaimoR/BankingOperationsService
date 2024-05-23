package effectiveMobile.bank.security.payload;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.util.dto.PersonRegDto;
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
    private String login;
    @NotBlank
    private String password;
}