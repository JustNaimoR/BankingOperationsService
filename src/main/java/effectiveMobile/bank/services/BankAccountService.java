package effectiveMobile.bank.services;

import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.repositories.BankAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BankAccountService {
    private BankAccountRepository bankAccountRepository;

    public void createAccount(Person person, int amount) {
        BankAccount bankAccount = BankAccount.builder()
                .withPerson(person)
                .withAmount(amount)
                .build();

        bankAccountRepository.save(bankAccount);
    }
}