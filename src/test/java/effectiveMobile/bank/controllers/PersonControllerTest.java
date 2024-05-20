package effectiveMobile.bank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import effectiveMobile.bank.entities.Person;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.PersonRegDtoValidator;
import effectiveMobile.bank.util.dto.PersonRegDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {PersonController.class, PersonRegDtoValidator.class},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@DisplayName("Testing of UserController")
public class PersonControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PersonService personService;

    //todo - создание пользователя с некорректными данными (невалидные поля + неуникальность)

    @Nested
    @DisplayName("testing of incorrect registration of user")
    public class IncorrectFieldsTests {

        @Test
        @DisplayName("POST user/reg - incorrect fields")
        public void incorrectFields1() throws Exception {
            PersonRegDto dto = PersonRegDto.builder()
                    .login(null)                    // empty
                    .password("")                   // empty
                    .amount(-1)                     // negative
                    .phoneNumber("1234567890")      // len != 11
                    .email("email")                 // incorrect email
                    .build();

            Mockito.when(personService.findByLogin(Mockito.anyString())).thenThrow(new Exception());
            Mockito.when(personService.findByEmail(Mockito.anyString())).thenThrow(new Exception());
            Mockito.when(personService.findByPhoneNumber(Mockito.anyString())).thenThrow(new Exception());

            mvc.perform(
                post("/user/reg")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
            ).andExpectAll(
//                status().isBadRequest(),
//                jsonPath("")
            );
        }

        @Test
        @DisplayName("POST user/reg - violating uniqueness by fields")
        public void incorrectFields2() throws Exception {

        }
    }
}