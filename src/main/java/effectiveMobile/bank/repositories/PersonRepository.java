package effectiveMobile.bank.repositories;

import effectiveMobile.bank.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByLogin(String login);
    Optional<Person> findByPhoneNumber(String phoneNumber);
}