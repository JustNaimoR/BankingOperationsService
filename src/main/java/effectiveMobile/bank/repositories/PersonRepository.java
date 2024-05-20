package effectiveMobile.bank.repositories;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByLogin(String login);
    Optional<Person> findByPhoneNumber(String phoneNumber);

    @Query("select new Person(p.id, p.bankAccount, p.login, p.password, p.fullname, p.email, p.birthday, p.phoneNumber) " +
            "from Person p where p.birthday > :day")
    List<Person> findWithBirthAfter(@Param("day") LocalDate birthday);
}