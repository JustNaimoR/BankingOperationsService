package effectiveMobile.bank.services;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.BankAccountNotFoundException;
import effectiveMobile.bank.exceptions.IllegalActionException;
import effectiveMobile.bank.exceptions.NotEnoughUnitsException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.repositories.BankAccountRepository;
import effectiveMobile.bank.security.JwtAuthenticationFilter;
import effectiveMobile.bank.security.service.AuthService;
import effectiveMobile.bank.util.dto.UnitTransferDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountService {
    private final AuthService authService;
    private final BankAccountRepository bankAccountRepository;
    private PersonService personService;


    public void createAccount(Person person, BigDecimal amount) {
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
        String curLogin = authService.getCurAuthenticatedUser().getName();

        if (!personService.findById(fromId).getLogin().equals(curLogin))
            throw new IllegalActionException("The currently authenticated user cannot send units from someone else's account");
        if (fromId == transferDto.getToId())
            throw new ValidationException("You can't transfer your own money to yourself");


        BankAccount fromAccount = findById(fromId);
        BankAccount toAccount = findById(transferDto.getToId());

        BankingOperationsServiceApplication.logger.info("Transferring units... id={} have {} and id = {} have {}",
                fromId, fromAccount.getAmount(), transferDto.getToId(), toAccount.getAmount());

        BigDecimal amount = transferDto.getAmount();
        int toId = toAccount.getId();

        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("Amount cannot be negative");
        if (fromAccount.getAmount().compareTo(amount) < 0)
            throw new NotEnoughUnitsException();

        fromAccount.setAmount(fromAccount.getAmount().subtract(amount));
        toAccount.setAmount(toAccount.getAmount().add(amount));

        BankingOperationsServiceApplication.logger.info("{} units from id={} to id={} were transferred! Now id={} have {} units and id={} have {} units",
                amount, fromId, toId, fromId, fromAccount.getAmount(), toId, toAccount.getAmount());
    }



    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    @Scheduled(cron = "0 * * * * *")
    public void updatePercent() {
        BankingOperationsServiceApplication.logger.info("Adding 5% to users...");

        List<BankAccount> list = findAll();
        AtomicInteger added = new AtomicInteger();

        list.forEach(account -> {
            BigDecimal new_value = account.getAmount().multiply(BigDecimal.valueOf(105)).divide(ONE_HUNDRED, 2, RoundingMode.HALF_DOWN);
            BigDecimal max_value = account.getInitialAmount().multiply(BigDecimal.valueOf(207)).divide(ONE_HUNDRED, 2, RoundingMode.HALF_DOWN);

            if (new_value.compareTo(max_value) < 0) {
                account.setAmount(new_value);
                added.getAndIncrement();
            } else if (!account.getAmount().equals(max_value)) {
                account.setAmount(max_value);
                added.getAndIncrement();
            }
        });

        BankingOperationsServiceApplication.logger.info("Adding is complete. {} bank accounts are updated", added.get());
    }


    @Autowired
    public void setJwtAuthenticationFilter(@Lazy PersonService personService) {
        // Отдельный сеттер для inject, тк без него произойдет circular dependency
        this.personService = personService;
    }
}