package effectiveMobile.bank.util.dto;

// DTO для регистрации нового пользователя

import effectiveMobile.bank.entities.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PersonRegDto {

    @NotEmpty(message = "login can't be empty!")
    @Schema(example = "login123")
    private String login;

    @NotEmpty(message = "password is required")
    @Schema(example = "1234")
    private String password;

    @Min(value = 0, message = "initial amount can't be negative")
    @Schema(example = "100")
    private BigDecimal amount;

    @Size(min = 11, max = 11, message = "length of the phone number is incorrect")
    @Schema(example = "01234567890")
    private String phoneNumber;

    @Email(message = "incorrect email")
    @Schema(example = "mail@mail.com")
    private String email;

    @Schema(example = "Leschinsky Roman Georgievich")
    private String fullname;

    @Schema(example = "01-02-2003")
    private LocalDateTime birthday;

    public static Person toPerson(PersonRegDto dto) {
        return Person.builder()
                .withFullname(dto.getFullname())
                .withBirthday(dto.getBirthday())
                .withLogin(dto.getLogin())
                .withPassword(dto.getPassword())
                .withEmail(dto.getEmail())
                .withPhoneNumber(dto.getPhoneNumber())
                .build();
    }

    @Override
    public String toString() {
        return "[ " +
                "login=" + login +
                ", password=" + password +
                ", amount=" + amount +
                ", phoneNumber=" + phoneNumber +
                ", email=" + email +
                ", fullname=" + fullname +
                ", birthday=" + birthday +
                "]";
    }
}