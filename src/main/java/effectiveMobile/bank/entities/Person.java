package effectiveMobile.bank.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

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
    private String email;
    private Date birthday;

    @Column(name = "phone_number")
    private String phoneNumber;
}