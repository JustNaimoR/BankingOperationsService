package effectiveMobile.bank.controllers;

import effectiveMobile.bank.BankingOperationsServiceApplication;
import effectiveMobile.bank.exceptions.dto.ExceptionDto;
import effectiveMobile.bank.security.payload.JwtResponse;
import effectiveMobile.bank.security.payload.LoginRequest;
import effectiveMobile.bank.security.service.AuthService;
import effectiveMobile.bank.services.BankAccountService;
import effectiveMobile.bank.services.PersonService;
import effectiveMobile.bank.util.PersonRegDtoValidator;
import effectiveMobile.bank.util.dto.PersonListItemDto;
import effectiveMobile.bank.util.dto.PersonRegDto;
import effectiveMobile.bank.util.dto.UnitTransferDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//todo - регистрация еще с некоторыми полями, которые мы не можем настроить потом
//     - admin token для возможности получить доступ в любую точку приложения
//     - проверка всех точек в openapi
//     - README файл на гите (c кодом базы данных)

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Tag(name = "Person controller", description = "Контроллер для обработки основных запросов пользователя")
@SecurityRequirement(name = "bearerAuth")
public class PersonController {
    private PersonService personService;
    private BankAccountService bankAccountService;



    @GetMapping("/find/birth-after")        // поиск человека по дате рождения (больше чем передана)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "список людей с датой рождения после переданной",
            parameters = @Parameter(name = "birthday", required = true, example = "31-12-2000"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "запрос выполнен успешно",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonListItemDto.class))
                    )
            }
    )
    public List<PersonListItemDto> findWithBirthAfter(@RequestParam String birthday) {
        BankingOperationsServiceApplication.logger.info("'/user/find/birth-after' request received to find people with birthday after {}", birthday);

        return personService.findWithBirthAfter(birthday);
    }

    @GetMapping("/find/phone-number")       // поиск человека по номеру телефона
    @ResponseStatus(HttpStatus.FOUND)
    @Operation(
            summary = "поиск человека по номеру телефона",
            parameters = @Parameter(name = "phoneNumber", required = true, example = "79111234532"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "человек найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonListItemDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "человек не был найден",
                            content = @Content()
                    )
            }
    )
    public PersonListItemDto findWithPhoneNumber(@RequestParam String phoneNumber) {
        BankingOperationsServiceApplication.logger.info("'/user/find/phone-number' request received to find a person with {} phone number", phoneNumber);

        return personService.toListItemDto(personService.findByPhoneNumber(phoneNumber));
    }

    @GetMapping("/find/like-fullname")           // поиск человека по like имени
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "поиск человека по like имени",
            parameters = @Parameter(
                    name = "fullname", required = true, example = "Leshchinskiy"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "человек найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonListItemDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "человек не был найден",
                            content = @Content()
                    )
            }
    )
    public List<PersonListItemDto> findWithLikeFullname(@RequestParam String fullname) {
        BankingOperationsServiceApplication.logger.info("'/find/like-fullname' request received to find a person with fullname like {}'", fullname);

        return personService.findWithLikeFullname(fullname);
    }

    @GetMapping("/find/email")                   // поиск человека по email
    @ResponseStatus(HttpStatus.FOUND)
    @Operation(
            summary = "поиск человека по email",
            parameters = @Parameter(
                    name = "email", required = true, example = "mail@mail.com"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "человек найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonListItemDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "человек не был найден",
                            content = @Content()
                    )
            }
    )
    public PersonListItemDto findWithEmail(@RequestParam String email) {
        BankingOperationsServiceApplication.logger.info("'/user/find/email' request received to find a person with {} email", email);

        return personService.toListItemDto(personService.findByEmail(email));
    }



    @PutMapping("/update/{id}/phone")           // обновление телефона человека (1 значение в теле запроса)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "обновление телефона человека через его id",
            parameters = @Parameter(
                    name = "phoneNumber", required = true, example = "79111234567"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "значение телефона обновлено",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "невалидное значение телефона",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "человек не был найден",
                            content = @Content()
                    )
            }
    )
    public void updatePhoneNumber(@PathVariable int id, @RequestBody String phoneNumber) {
        BankingOperationsServiceApplication.logger.info("'/user/update/{}/phone' request received to update a phone number for a person with id {} to the new value {}",
                id, id, phoneNumber);

        personService.updatePhoneNumber(id, phoneNumber);
    }

    @PutMapping("/update/{id}/email")           // обновление email у человека (в теле запроса 1 значение)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "обновление email человека через его id",
            parameters = @Parameter(
                    name = "email", required = true, example = "mail@mail.com"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "значение email обновлено",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "невалидное значение emailа",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "человек не был найден",
                            content = @Content()
                    )
            }
    )
    public void updateEmail(@PathVariable int id, @RequestBody String email) {
        BankingOperationsServiceApplication.logger.info("'/user/update/{}/email' request received to update an email for a person with id {} to the new value {}",
                id, id, email);

        personService.updateEmail(id, email);
    }

    @PutMapping("/delete/{id}/phone")           // удаление телефона человека
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "удаление телефона человека через его id",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "телефон был удален",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "человек не был найден",
                            content = @Content()
                    )
            }
    )
    public void deletePhoneNumber(@PathVariable int id) {
        BankingOperationsServiceApplication.logger.info("'/user/delete/{}/phone' request received to delete a phone number for a person with id {}",
                id, id);

        personService.deletePhoneNumber(id);
    }



    @PutMapping("/{fromId}/transfer")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "перевод денег с одного счета (from) на другой",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "деньги были переведены",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "ошибка с данными для перевода",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "человек с переданным id не был найден",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))
                    )
            }
    )
    public void transfer(@PathVariable int fromId, @RequestBody UnitTransferDto transferDto) {
        BankingOperationsServiceApplication.logger.info("'/user/{}/transfer' request received to transfer {} units to person with id {}",
                fromId, transferDto.getAmount(), transferDto.getToId());

        bankAccountService.transferMoney(fromId, transferDto);
    }
}