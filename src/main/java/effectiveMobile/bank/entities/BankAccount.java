package effectiveMobile.bank.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @MapsId
    private Person person;

    @Min(value = 0, message = "initial amount can't be negative")

    private BigDecimal amount;

    @Column(name = "initial_amount")
    private BigDecimal initialAmount;
}