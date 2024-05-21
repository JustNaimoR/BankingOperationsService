package effectiveMobile.bank.security.service;

import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.exceptions.PersonNotFoundException;
import effectiveMobile.bank.repositories.PersonRepository;
import effectiveMobile.bank.security.entity.UserDetailsImpl;
import effectiveMobile.bank.services.PersonService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> opt = personRepository.findByLogin(username);
        return new UserDetailsImpl(opt.orElseThrow(PersonNotFoundException::new));
    }
}