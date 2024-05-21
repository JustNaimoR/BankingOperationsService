package effectiveMobile.bank.security.service;

import effectiveMobile.bank.security.entity.UserDetailsImpl;
import effectiveMobile.bank.security.payload.LoginRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private AuthenticationManager authenticationManager;

//    //todo исключение при неверном пароле - какое и настроить обработку
//    public void authenticateUser(LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                loginRequest.getLogin(), loginRequest.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
////        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//    }

}