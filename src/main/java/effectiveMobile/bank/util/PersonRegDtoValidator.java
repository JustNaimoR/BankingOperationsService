package effectiveMobile.bank.util;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.exceptions.PersonNotFoundException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.dto.PersonRegDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class PersonRegDtoValidator implements Validator {
    private PersonService personService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonRegDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonRegDto dto = (PersonRegDto) target;

        try {
            personService.findByLogin(dto.getLogin());
            errors.rejectValue("login", "", "This login is already taken");
        } catch (PersonNotFoundException ignored) {}

        try {
            personService.findByEmail(dto.getEmail());
            errors.rejectValue("email", "", "This email address is already in use");
        } catch (PersonNotFoundException ignored) {}

        try {
            personService.findByPhoneNumber(dto.getPhoneNumber());
            errors.rejectValue("phoneNumber", "", "This phone number is already in use");
        } catch (PersonNotFoundException ignored) {}

        if (errors.hasErrors()) {
            throw new ValidationException("incorrect values", errors);
        }

        BankingOperationsServiceApplication.logger.info("Person's values are valid!");
    }
}