package effectiveMobile.bank.controllers;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.security.payload.LoginRequest;
import effectiveMobile.bank.security.service.AuthService;
import effectiveMobile.bank.services.BankAccountService;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.PersonRegDtoValidator;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
todo
 - Выделить отдельно конечные ендпоинты для контроллера - swagger -> ?
 */

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class PersonController {
    private PersonService personService;
    private PersonRegDtoValidator personRegDtoValidator;
    private BankAccountService bankAccountService;
    private AuthService authService;

    @GetMapping("/find/birth-after")        // поиск человека по дате рождения (больше чем передана)
    @ResponseStatus(HttpStatus.OK)
    public List<PersonListItemDto> findWithBirthAfter(@RequestParam String birthday) {
        return personService.findWithBirthAfter(birthday);
    }

    @GetMapping("/find/phone-number")       // поиск человека по номеру телефона
    @ResponseStatus(HttpStatus.FOUND)
    public PersonListItemDto findWithPhoneNumber(@RequestParam String phoneNumber) {
        return personService.toListItemDto(personService.findByPhoneNumber(phoneNumber));
    }

    @GetMapping("/find/like-fullname")           // поиск человека по like имени
    @ResponseStatus(HttpStatus.OK)
    public List<PersonListItemDto> findWithLikeFullname(@RequestParam String fullname) {
        return personService.findWithLikeFullname(fullname);
    }

    @GetMapping("/find/email")                   // поиск человека по email
    @ResponseStatus(HttpStatus.FOUND)
    public PersonListItemDto findWithEmail(@RequestParam String email) {
        return personService.toListItemDto(personService.findByEmail(email));
    }

    @PostMapping("/reg")        // Регистрация новых пользователей
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody PersonRegDto regDto,
                            BindingResult bindingResult) {
        personRegDtoValidator.validate(regDto, bindingResult);
        personService.registerPerson(regDto);
    }

    // todo норм ли контроллер или стоит сделать получше
    @PutMapping("/update/{id}/phone")       // обновление телефона человека
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePhoneNumber(@PathVariable int id,
                                  @RequestParam String phoneNumber) {
        personService.updatePhoneNumber(id, phoneNumber);
    }

    @PutMapping("/delete/{id}/phone")       // удаление телефона человека
    @ResponseStatus(HttpStatus.OK)
    public void deletePhoneNumber(@PathVariable int id) {
        personService.deletePhoneNumber(id);
    }

    @PutMapping("/update/{id}/email")       // обновление email у человека
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateEmail(@PathVariable int id,
                            @RequestParam String email) {
        personService.updateEmail(id, email);
    }

    @PutMapping("/{from}/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@PathVariable int from, @RequestParam int to, @RequestParam double amount) {
        bankAccountService.transferMoney(from, to, amount);
    }

//    @PostMapping("/login")
//    @ResponseStatus(HttpStatus.OK)              // вход в систему под логином
//    public void personSignIn(@RequestBody LoginRequest loginRequest) {
//        authService.authenticateUser(loginRequest);
//    }

    @GetMapping("/list")                    // получение списка всех пользователей
    @ResponseStatus(HttpStatus.OK)
    public List<PersonListItemDto> getPeople() {
        return personService.getPeople();
    }
}