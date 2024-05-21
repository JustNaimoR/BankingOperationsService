package effectiveMobile.bank.repositories;

import effectiveMobile.bank.entities.BankAccount;
import effectiveMobile.bank.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    Optional<BankAccount> findById(Integer id);
}
