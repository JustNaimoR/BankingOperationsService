package effectiveMobile.bank.security.payload;

import effectiveMobile.bank.entities.Person;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LoginRequest - используемый класс для передачи данных с login-page для дальнейшей аутентификации
 */

@Data
@AllArgsConstructor
public class LoginRequest {

    public LoginRequest(Person person) {
        this.login = person.getLogin();
        this.password = person.getPassword();
    }

    @NotBlank
    private String login;
    @NotBlank
    private String password;
}