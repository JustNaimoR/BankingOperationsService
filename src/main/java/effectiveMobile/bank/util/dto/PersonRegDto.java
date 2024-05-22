package effectiveMobile.bank.util.dto;

// DTO для регистрации нового пользователя

import effectiveMobile.bank.entities.Person;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PersonRegDto {

    @NotEmpty(message = "login can't be empty!")
    private String login;

    @NotEmpty(message = "password is required")
    private String password;

    @Min(value = 0, message = "initial amount can't be negative")
    private double amount;

    @Size(min = 11, max = 11, message = "length of the phone number is incorrect")
    private String phoneNumber;

    @Email(message = "incorrect email")
    private String email;

    public static Person toPerson(PersonRegDto dto) {
        return Person.builder()
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
                "]";
    }
}