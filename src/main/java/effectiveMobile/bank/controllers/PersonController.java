package effectiveMobile.bank.controllers;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.PersonRegDtoValidator;
import effectiveMobile.bank.util.dto.PersonRegDto;
import jakarta.validation.Valid;
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

    @PostMapping("/reg")        // Регистрация новых пользователей
    public void register(@Valid @RequestBody PersonRegDto regDto,
                            BindingResult bindingResult) {
        personRegDtoValidator.validate(regDto, bindingResult);
        personService.registerPerson(regDto);
    }

    @GetMapping("/list")
    public List<Person> getPeople() {
        return personService.getPeople();
    }
}