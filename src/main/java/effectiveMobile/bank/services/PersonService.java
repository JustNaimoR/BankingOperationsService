package effectiveMobile.bank.services;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.repositories.PersonRepository;
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

    public List<Person> getPeople() {
        return personRepository.findAll();
    }

    // todo заменить исключение на своё - ненаход человека
    public Person findByEmail(String email) {
        Optional<Person> optionalPerson = personRepository.findByEmail(email);
        return optionalPerson.orElseThrow();
    }

    public Person findByLogin(String login) {
        Optional<Person> optionalPerson = personRepository.findByLogin(login);
        return optionalPerson.orElseThrow();
    }

    public Person findByPhoneNumber(String phoneNumber) {
        Optional<Person> optionalPerson = personRepository.findByPhoneNumber(phoneNumber);
        return optionalPerson.orElseThrow();
    }

    @Transactional
    public void registerPerson(PersonRegDto regDto) {
        Person person = PersonRegDto.toPerson(regDto);
        bankAccountService.createAccount(person, regDto.getAmount());

        personRepository.save(person);
    }

}