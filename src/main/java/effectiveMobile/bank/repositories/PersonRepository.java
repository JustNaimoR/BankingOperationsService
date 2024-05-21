package effectiveMobile.bank.repositories;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByLogin(String login);
    Optional<Person> findByPhoneNumber(String phoneNumber);

    @Query("select p from Person p where p.birthday > :day order by p.birthday asc")
    List<Person> findWithBirthAfter(@Param("day") LocalDateTime birthday);
    @Query("select p from Person p where p.fullname like :fullname% order by p.fullname asc")
    List<Person> findWithLikeFullname(@Param("fullname") String fullname);
}