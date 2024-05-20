package effectiveMobile.bank.services;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.PersonNotFoundException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.repositories.PersonRepository;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class PersonService {
    private PersonRepository personRepository;
    private BankAccountService bankAccountService;

    public List<PersonListItemDto> getPeople() {
        return personRepository.findAll().stream().map(person ->
                PersonListItemDto.builder()
                        .login(person.getLogin())
                        .fullname(person.getFullname())
                        .email(person.getEmail())
                        .birthday(person.getBirthday())
                        .phoneNumber(person.getPhoneNumber())
                        .amount(person.getBankAccount().getAmount())
                        .build()
        ).toList();
    }

    @Transactional
    public List<PersonListItemDto> findWithBirthAfter(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(birthday, formatter);

        return personRepository.findWithBirthAfter(date).stream().map(person ->
                PersonListItemDto.toDto(person, bankAccountService.findById(person.getId()).getAmount()))
                .toList();
    }

    @Transactional
    public void updatePhoneNumber(int id, String phoneNumber) {
        if (phoneNumber != null && personRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new ValidationException("Phone number already exists");
        }
        if (phoneNumber != null && phoneNumber.length() != 11) {
            throw new ValidationException("Phone number should be 11 digits");
        }

        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        person.setPhoneNumber(phoneNumber);
    }

    @Transactional
    public void deletePhoneNumber(int id) {
        updatePhoneNumber(id, null);
    }

    @Transactional
    public void updateEmail(int id, String email) {
        if (personRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("email already exists");
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new ValidationException("incorrect email");
        }

        Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
        person.setEmail(email);
    }

    private <T> Person findByField(T field, Function<T, Optional<Person>> repFunc) {
        Optional<Person> optionalPerson = repFunc.apply(field);
        return optionalPerson.orElseThrow(PersonNotFoundException::new);
    }

    public Person findByEmail(String email) {
        return findByField(email, personRepository::findByEmail);
    }

    public Person findByLogin(String login) {
        return findByField(login, personRepository::findByLogin);
    }

    public Person findByPhoneNumber(String phoneNumber) {
        return findByField(phoneNumber, personRepository::findByPhoneNumber);
    }

    @Transactional
    public void registerPerson(PersonRegDto regDto) {
        Person person = PersonRegDto.toPerson(regDto);
        bankAccountService.createAccount(person, regDto.getAmount());

        personRepository.save(person);
    }

}