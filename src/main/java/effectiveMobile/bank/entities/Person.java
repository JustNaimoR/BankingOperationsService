package effectiveMobile.bank.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

/*
todo
 - валидация полей
 */

@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "person")
    private BankAccount bankAccount;

    @NotEmpty(message = "login can't be empty!")
    private String login;

    @NotEmpty(message = "password can't be empty!")
    private String password;

    private String fullname;

    @Email(message = "incorrect email")
    private String email;

    private LocalDateTime birthday;

    @Column(name = "phone_number")
    @Size(min = 11, max = 11, message = "length of the phone number is incorrect")
    private String phoneNumber;
}