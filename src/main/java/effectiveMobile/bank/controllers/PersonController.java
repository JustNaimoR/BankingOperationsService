package effectiveMobile.bank.controllers;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.PersonRegDtoValidator;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
todo
 - Выделить отдельно конечные ендпоинты для контроллера
 - возвращать корректные коды ответов сервера
 */

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class PersonController {
    private PersonService personService;
    private PersonRegDtoValidator personRegDtoValidator;

    @GetMapping("/find")        // поиск человека по дате рождения (больше чем передана)
    public List<PersonListItemDto> findWithBirthAfter(@RequestParam String birthday) {
        return personService.findWithBirthAfter(birthday);
    }

    @PostMapping("/reg")        // Регистрация новых пользователей
    public void register(@Valid @RequestBody PersonRegDto regDto,
                            BindingResult bindingResult) {
        personRegDtoValidator.validate(regDto, bindingResult);
        personService.registerPerson(regDto);
    }

    // todo норм ли контроллер или стоит сделать получше
    @PutMapping("/update/{id}/phone")       // обновление телефона человека
    public void updatePhoneNumber(@PathVariable int id,
                                  @RequestParam String phoneNumber) {
        personService.updatePhoneNumber(id, phoneNumber);
    }

    @PutMapping("/delete/{id}/phone")       // удаление телефона человека
    public void deletePhoneNumber(@PathVariable int id) {
        personService.deletePhoneNumber(id);
    }

    @PutMapping("/update/{id}/email")       // обновление email у человека
    public void updateEmail(@PathVariable int id,
                            @RequestParam String email) {
        personService.updateEmail(id, email);
    }

    @GetMapping("/list")
    public List<PersonListItemDto> getPeople() {
        return personService.getPeople();
    }
}