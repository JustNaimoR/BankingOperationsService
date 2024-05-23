package effectiveMobile.bank.controllers;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.security.payload.JwtResponse;
import effectiveMobile.bank.security.payload.LoginRequest;
import effectiveMobile.bank.security.service.AuthService;
import effectiveMobile.bank.services.BankAccountService;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.PersonRegDtoValidator;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import effectiveMobile.bank.util.dto.UnitTransferDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class PersonController {
    private PersonService personService;
    private PersonRegDtoValidator personRegDtoValidator;
    private BankAccountService bankAccountService;
    private AuthService authService;



    @PostMapping("/reg")                        // Регистрация новых пользователей
    @ResponseStatus(HttpStatus.CREATED)
    public JwtResponse register(@Valid @RequestBody PersonRegDto regDto,
                                BindingResult bindingResult) {
        BankingOperationsServiceApplication.logger.info("'/user/reg' request received to register a new person with values: {}", regDto.toString());

        personRegDtoValidator.validate(regDto, bindingResult);
        personService.registerPerson(regDto);

        return authService.authenticateUser(new LoginRequest(regDto));
    }

    @GetMapping("/list")                    // получение списка всех пользователей
    @ResponseStatus(HttpStatus.OK)
    public List<PersonListItemDto> getPeople() {
        return personService.getPeople();
    }



    @GetMapping("/find/birth-after")        // поиск человека по дате рождения (больше чем передана)
    @ResponseStatus(HttpStatus.OK)
    public List<PersonListItemDto> findWithBirthAfter(@RequestParam String birthday) {
        BankingOperationsServiceApplication.logger.info("'/user/find/birth-after' request received to find people with birthday after {}", birthday);

        return personService.findWithBirthAfter(birthday);
    }

    @GetMapping("/find/phone-number")       // поиск человека по номеру телефона
    @ResponseStatus(HttpStatus.FOUND)
    public PersonListItemDto findWithPhoneNumber(@RequestParam String phoneNumber) {
        BankingOperationsServiceApplication.logger.info("'/user/find/phone-number' request received to find a person with {} phone number", phoneNumber);

        return personService.toListItemDto(personService.findByPhoneNumber(phoneNumber));
    }

    @GetMapping("/find/like-fullname")           // поиск человека по like имени
    @ResponseStatus(HttpStatus.OK)
    public List<PersonListItemDto> findWithLikeFullname(@RequestParam String fullname) {
        BankingOperationsServiceApplication.logger.info("'/find/like-fullname' request received to find a person with fullname like {}'", fullname);

        return personService.findWithLikeFullname(fullname);
    }

    @GetMapping("/find/email")                   // поиск человека по email
    @ResponseStatus(HttpStatus.FOUND)
    public PersonListItemDto findWithEmail(@RequestParam String email) {
        BankingOperationsServiceApplication.logger.info("'/user/find/email' request received to find a person with {} email", email);

        return personService.toListItemDto(personService.findByEmail(email));
    }



    @PutMapping("/update/{id}/phone")           // обновление телефона человека (1 значение в теле запроса)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePhoneNumber(@PathVariable int id, @RequestBody String phoneNumber) {
        BankingOperationsServiceApplication.logger.info("'/user/update/{}/phone' request received to update a phone number for a person with id {} to the new value {}",
                id, id, phoneNumber);

        personService.updatePhoneNumber(id, phoneNumber);
    }

    @PutMapping("/update/{id}/email")           // обновление email у человека (в теле запроса 1 значение)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateEmail(@PathVariable int id, @RequestBody String email) {
        BankingOperationsServiceApplication.logger.info("'/user/update/{}/email' request received to update an email for a person with id {} to the new value {}",
                id, id, email);

        personService.updateEmail(id, email);
    }

    @PutMapping("/delete/{id}/phone")           // удаление телефона человека
    @ResponseStatus(HttpStatus.OK)
    public void deletePhoneNumber(@PathVariable int id) {
        BankingOperationsServiceApplication.logger.info("'/user/delete/{}/phone' request received to delete a phone number for a person with id {}",
                id, id);

        personService.deletePhoneNumber(id);
    }



    @PutMapping("/{from}/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@PathVariable int from, @RequestBody UnitTransferDto transferDto) {
        BankingOperationsServiceApplication.logger.info("'/user/{}/transfer' request received to transfer {} units to person with id {}",
                from, transferDto.getAmount(), transferDto.getToId());

        bankAccountService.transferMoney(from, transferDto);
    }
}