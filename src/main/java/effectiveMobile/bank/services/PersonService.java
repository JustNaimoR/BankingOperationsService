package effectiveMobile.bank.services;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.PersonNotFoundException;
import effectiveMobile.bank.repositories.PersonRepository;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

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

    public Person findByEmail(String email) {
        Optional<Person> optionalPerson = personRepository.findByEmail(email);
        return optionalPerson.orElseThrow(PersonNotFoundException::new);
    }

    public Person findByLogin(String login) {
        Optional<Person> optionalPerson = personRepository.findByLogin(login);
        return optionalPerson.orElseThrow(PersonNotFoundException::new);
    }

    public Person findByPhoneNumber(String phoneNumber) {
        Optional<Person> optionalPerson = personRepository.findByPhoneNumber(phoneNumber);
        return optionalPerson.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void registerPerson(PersonRegDto regDto) {
        Person person = PersonRegDto.toPerson(regDto);
        bankAccountService.createAccount(person, regDto.getAmount());

        personRepository.save(person);
    }

}