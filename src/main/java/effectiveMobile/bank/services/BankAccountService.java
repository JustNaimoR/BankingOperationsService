package effectiveMobile.bank.services;

import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.BankAccountNotFoundException;
import effectiveMobile.bank.repositories.BankAccountRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BankAccountService {
    private BankAccountRepository bankAccountRepository;

    public void createAccount(Person person, double amount) {
        BankAccount bankAccount = BankAccount.builder()
                .withPerson(person)
                .withAmount(amount)
                .withInitialAmount(amount)
                .build();

        bankAccountRepository.save(bankAccount);
    }

    public BankAccount findById(int id) {
        Optional<BankAccount> optional = bankAccountRepository.findById(id);
        return optional.orElseThrow(BankAccountNotFoundException::new);
    }

    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    @Transactional
    public void transferMoney(int fromId, int toId, double amount) {
        //todo перевод аутентифицированным пользователем - проверка на совпадение id?
        BankAccount fromAccount = findById(fromId);
        BankAccount toAccount = findById(toId);

        fromAccount.setAmount(fromAccount.getAmount() - amount);
        toAccount.setAmount(toAccount.getAmount() + amount);
    }


    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updatePercent() {
        List<BankAccount> list = findAll();

        list.forEach(account -> {
            double value = (double) Math.round(account.getAmount() * 1.05 * 100) / 100;

            if (account.getInitialAmount() * 2.07 > value) {
                account.setAmount(value);
            }
        });
    }
}