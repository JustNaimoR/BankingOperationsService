package effectiveMobile.bank.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PersonListItemDto {
    private String login;
    private String fullname;
    private String email;
    private Date birthday;
    private String phoneNumber;
    private int amount;
}