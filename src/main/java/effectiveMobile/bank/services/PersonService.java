package effectiveMobile.bank.services;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.PersonNotFoundException;
import effectiveMobile.bank.exceptions.ValidationException;
import effectiveMobile.bank.repositories.PersonRepository;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Transactional
public class PersonService {
    private PersonRepository personRepository;
    private BankAccountService bankAccountService;
    private PasswordEncoder passwordEncoder;

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

    public List<PersonListItemDto> findWithBirthAfter(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime dateTime = LocalDate.parse(birthday, formatter).atStartOfDay();

        List<PersonListItemDto> list = personRepository.findWithBirthAfter(dateTime).stream()
                .map(person -> PersonListItemDto.toDto(person, bankAccountService.findById(person.getId()).getAmount()))
                .toList();

        BankingOperationsServiceApplication.logger.info("Found {} people with birthday after {}", list.size(), birthday);

        return list;
    }

    public void updatePhoneNumber(int id, String phoneNumber) {
        //todo сделать валидацию по другому? как убрать все эти if
        // если не нашли по id то выкидывать ошибку но с указанием какой id?
        if (phoneNumber != null && personRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new ValidationException("Phone number already exists");
        if (phoneNumber != null && phoneNumber.length() != 11)
            throw new ValidationException("Phone number should be 11 digits");

        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        person.setPhoneNumber(phoneNumber);

        BankingOperationsServiceApplication.logger.info("Phone number updated to {} for person with id= {}", phoneNumber, id);
    }

    public void deletePhoneNumber(int id) {
        updatePhoneNumber(id, null);
    }

    public void updateEmail(int id, String email) {
        if (personRepository.findByEmail(email).isPresent())
            throw new ValidationException("email already exists");
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))   // проверка что email валиден
            throw new ValidationException("incorrect email");

        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        person.setEmail(email);

        BankingOperationsServiceApplication.logger.info("Email updated to {} for person with id= {}", email, id);
    }

    public Person findByEmail(String email) {
        try {
            return findByField(email, personRepository::findByEmail);
        } catch (PersonNotFoundException ex) {
            throw new PersonNotFoundException("Person with email '" + email + "' not found");
        }
    }

    public Person findByLogin(String login) {
        try {
            return findByField(login, personRepository::findByLogin);
        } catch (PersonNotFoundException ex) {
            throw new PersonNotFoundException("Person with login '" + login + "' not found");
        }
    }

    public Person findByPhoneNumber(String phoneNumber) {
        try {
            return findByField(phoneNumber, personRepository::findByPhoneNumber);
        } catch (PersonNotFoundException ex) {
            throw new PersonNotFoundException("Person with phone number '" + phoneNumber + "' not found");
        }
    }

    public PersonListItemDto toListItemDto(Person person) {
        return PersonListItemDto.toDto(person, bankAccountService.findById(person.getId()).getAmount());
    }

    public List<PersonListItemDto> findWithLikeFullname(String fullname) {
        return personRepository.findWithLikeFullname(fullname).stream().map(this::toListItemDto).toList();
    }

    public void registerPerson(PersonRegDto regDto) {
        BankingOperationsServiceApplication.logger.info("the person is registering...");

        Person person = PersonRegDto.toPerson(regDto);
        bankAccountService.createAccount(person, regDto.getAmount());
        person.setPassword(passwordEncoder.encode(person.getPassword()));

        personRepository.save(person);

        BankingOperationsServiceApplication.logger.info("the person with {} units is saved! {}", regDto.getAmount(), person);
    }


    private <T> Person findByField(T field, Function<T, Optional<Person>> reposFunc) {
        Optional<Person> optionalPerson = reposFunc.apply(field);
        return optionalPerson.orElseThrow(PersonNotFoundException::new);
    }
}