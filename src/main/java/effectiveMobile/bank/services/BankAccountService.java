package effectiveMobile.bank.services;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.BankAccountNotFoundException;
import effectiveMobile.bank.exceptions.NotEnoughUnitsException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.repositories.BankAccountRepository;
import effectiveMobile.bank.util.dto.UnitTransferDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@Transactional
public class BankAccountService {
    private BankAccountRepository bankAccountRepository;

    public void createAccount(Person person, double amount) {
        BankingOperationsServiceApplication.logger.info("the bank_account for a person is creating...");

        BankAccount bankAccount = BankAccount.builder()
                .withPerson(person)
                .withAmount(amount)
                .withInitialAmount(amount)
                .build();

        bankAccountRepository.save(bankAccount);

        BankingOperationsServiceApplication.logger.info("the bank_account is created!");
    }

    public BankAccount findById(int id) {
        Optional<BankAccount> optional = bankAccountRepository.findById(id);
        return optional.orElseThrow(() -> new BankAccountNotFoundException(id));
    }

    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    public void transferMoney(int fromId, UnitTransferDto transferDto) {
        //todo перевод аутентифицированным пользователем - проверка на совпадение id?

        BankAccount fromAccount = findById(fromId);
        BankAccount toAccount = findById(transferDto.getToId());

        BankingOperationsServiceApplication.logger.info("Transferring units... id={} have {} and id = {} have {}",
                fromId, fromAccount.getAmount(), transferDto.getToId(), toAccount.getAmount());

        double amount = transferDto.getAmount();
        int toId = toAccount.getId();

        if (amount < 0)
            throw new ValidationException("Amount cannot be negative");
        if (fromAccount.getAmount() < amount)
            throw new NotEnoughUnitsException();

        fromAccount.setAmount(fromAccount.getAmount() - amount);
        toAccount.setAmount(toAccount.getAmount() + amount);

        BankingOperationsServiceApplication.logger.info("{} units from id={} to id={} were transferred! Now id={} have {} units and id={} have {} units",
                amount, fromId, toId, fromId, fromAccount.getAmount(), toId, toAccount.getAmount());
    }


    @Scheduled(cron = "0 * * * * *")
    public void updatePercent() {
        BankingOperationsServiceApplication.logger.info("Adding 5% to users...");

        List<BankAccount> list = findAll();
        AtomicInteger added = new AtomicInteger();

        list.forEach(account -> {
            double value = (double) Math.round(account.getAmount() * 1.05 * 100) / 100;

            if (account.getInitialAmount() * 2.07 > value) {
                account.setAmount(value);
                added.getAndIncrement();
            }
        });

        BankingOperationsServiceApplication.logger.info("Adding is complete. {} bank accounts are updated", added.get());
    }
}