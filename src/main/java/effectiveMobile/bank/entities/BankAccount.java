package effectiveMobile.bank.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Entity
@Table(name = "bank_account")
public class BankAccount {

    @Id
    private int id;

    @OneToOne
    @JoinColumn(name = "person_id")
    @MapsId
    private Person person;

    private int amount;
}