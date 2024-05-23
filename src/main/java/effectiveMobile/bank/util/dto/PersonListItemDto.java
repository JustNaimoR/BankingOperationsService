package effectiveMobile.bank.util.dto;

import effectiveMobile.bank.entities.Person;
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
    private String login;
    private String fullname;
    private String email;
    private LocalDateTime birthday;
    private String phoneNumber;
    private BigDecimal amount;

    public static PersonListItemDto toDto(Person person, BigDecimal amount) {
        return PersonListItemDto.builder()
                .login(person.getLogin())
                .fullname(person.getFullname())
                .email(person.getEmail())
                .birthday(person.getBirthday())
                .phoneNumber(person.getPhoneNumber())
                .amount(amount)
                .build();
    }
}