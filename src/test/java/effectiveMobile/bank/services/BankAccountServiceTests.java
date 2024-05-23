package effectiveMobile.bank.services;

import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.BankAccountNotFoundException;
import effectiveMobile.bank.exceptions.IllegalActionException;
import effectiveMobile.bank.exceptions.NotEnoughUnitsException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.repositories.BankAccountRepository;
import effectiveMobile.bank.security.entity.UserDetailsImpl;
import effectiveMobile.bank.security.service.AuthService;
import effectiveMobile.bank.security.service.UserDetailsServiceImpl;
import effectiveMobile.bank.util.dto.UnitTransferDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test0 - валидная передача средств
 * test1 - один или два пользователя отсутствуют в системе
 * test2 - перевести негативное количество единиц
 * test3 - недостаточно средств для перевода
 * test4 - снятие и перевод средств на один и тот же аккаунт
 * test5 - попытка перевести средства чужого пользователя
 */

@DisplayName("Units transfer tests")
@SpringBootTest
public class BankAccountServiceTests {
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private AuthService authService;

    @MockBean
    private BankAccountRepository accountRepository;
    @MockBean
    private PersonService personService;

    private static final String MAIN_LOGIN = "login";

    @BeforeEach
    public void setUpSecurityContext() {
        UserDetails userDetails = new UserDetailsImpl(Person.builder().withLogin(MAIN_LOGIN).build());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }


    @Test
    @DisplayName("Valid transfer")
    public void test0() {
        int fromId = 1;
        int toId = 2;
        int amount = 100;
        int initAmount = 1000;

        BankAccount fromAccount = BankAccount.builder()
                .withAmount(BigDecimal.valueOf(initAmount))
                .build();
        BankAccount toAccount = BankAccount.builder()
                .withAmount(BigDecimal.valueOf(initAmount))
                .build();

        UnitTransferDto dto = UnitTransferDto.builder()
                .toId(toId)
                .amount(BigDecimal.valueOf(amount))
                .build();

        Mockito.when(personService.findById(Mockito.anyInt())).thenReturn(Person.builder().withLogin(MAIN_LOGIN).build());
        Mockito.when(accountRepository.findById(fromId)).thenReturn(Optional.of(fromAccount));
        Mockito.when(accountRepository.findById(toId)).thenReturn(Optional.of(toAccount));

        bankAccountService.transferMoney(fromId, dto);

        int expectedFromAmount = initAmount - amount;
        int expectedToAmount = initAmount + amount;

        assertAll(
                () -> assertEquals(fromAccount.getAmount(), BigDecimal.valueOf(expectedFromAmount)),
                () -> assertEquals(toAccount.getAmount(), BigDecimal.valueOf(expectedToAmount))
        );
    }

    @Test
    @DisplayName("if one or both accounts don't exist")
    public void test1() {
        int fromId = 1;
        int toId = 2;
        int amount = 100;

        UnitTransferDto dto = UnitTransferDto.builder()
                .toId(toId)
                .amount(BigDecimal.valueOf(amount))
                .build();

        Mockito.when(personService.findById(Mockito.anyInt())).thenReturn(Person.builder().withLogin(MAIN_LOGIN).build());
        Mockito.when(accountRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.transferMoney(fromId, dto));
    }

    @Test
    @DisplayName("The transfer amount is negative")
    public void test2() {
        int amount = -1;

        UnitTransferDto dto = UnitTransferDto.builder()
                .amount(BigDecimal.valueOf(amount))
                .build();

        Mockito.when(personService.findById(Mockito.anyInt())).thenReturn(Person.builder().withLogin(MAIN_LOGIN).build());
        Mockito.when(accountRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new BankAccount()));

        assertThrows(ValidationException.class, () -> bankAccountService.transferMoney(amount, dto));
    }

    @Test
    @DisplayName("Units aren't enough to transfer")
    public void test3() {
        int transferAmount = 100;
        int initialAmount = 50;
        int fromId = 1;

        UnitTransferDto dto = UnitTransferDto.builder()
                .amount(BigDecimal.valueOf(transferAmount))
                .build();

        Mockito.when(personService.findById(Mockito.anyInt())).thenReturn(Person.builder().withLogin(MAIN_LOGIN).build());
        Mockito.when(accountRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(BankAccount.builder().withAmount(BigDecimal.valueOf(initialAmount)).build()));

        assertThrows(NotEnoughUnitsException.class, () -> bankAccountService.transferMoney(fromId, dto));
    }

    @Test
    @DisplayName("Transfer your own units to yourself")
    public void test4() {
        int toId = 1;
        int fromId = 1;

        UnitTransferDto dto = UnitTransferDto.builder()
                .toId(toId)
                .build();

        Mockito.when(personService.findById(Mockito.anyInt())).thenReturn(Person.builder().withLogin(MAIN_LOGIN).build());

        assertThrows(ValidationException.class, () -> bankAccountService.transferMoney(fromId, dto));
    }



    @Test
    @DisplayName("trying to transfer not ours units")
    public void test5() {
        Mockito.when(personService.findById(Mockito.anyInt()))
                        .thenReturn(Person.builder().withLogin("another_login").build());

        assertThrows(IllegalActionException.class, () -> bankAccountService.transferMoney(0, null));
    }
}