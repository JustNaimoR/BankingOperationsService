package effectiveMobile.bank.util.dto;

import effectiveMobile.bank.entities.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class PersonListItemDto {
    @Schema(example = "1")
    private int id;
    @Schema(example = "login123")
    private String login;
    @Schema(example = "Leschinskiy Roman Georgievich")
    private String fullname;
    @Schema(example = "mail@mail.com")
    private String email;
    @Schema(example = "year-month-dayT00:00:00")
    private LocalDateTime birthday;
    @Schema(example = "79045934392")
    private String phoneNumber;
    @Schema(example = "147.75")
    private BigDecimal amount;

    public static PersonListItemDto toDto(Person person, BigDecimal amount) {
        return PersonListItemDto.builder()
                .id(person.getId())
                .login(person.getLogin())
                .fullname(person.getFullname())
                .email(person.getEmail())
                .birthday(person.getBirthday())
                .phoneNumber(person.getPhoneNumber())
                .amount(amount)
                .build();
    }
}