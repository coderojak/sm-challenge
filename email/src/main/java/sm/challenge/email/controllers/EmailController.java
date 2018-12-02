package sm.challenge.email.controllers;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import sm.challenge.email.configs.AmqpConfig;
import sm.challenge.email.to.EmailMessage;
import sm.challenge.email.to.GenericResponse;
import sm.challenge.email.to.GenericResponse.Status;
import sm.challenge.email.validators.EmailValidator;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    @NonNull
    private final RabbitTemplate rabbitTemplate;

    @NonNull
    private final EmailValidator emailMessageValidator;

    @PostMapping(value = "/messages", consumes = Versions.V1)
    public ResponseEntity<GenericResponse> messages(@RequestBody List<EmailMessage> messages) {
        emailMessageValidator.validateEmailMessages(messages);
        for (EmailMessage message : messages) {
            rabbitTemplate.convertAndSend(AmqpConfig.EMAIL_EXCHANGE, AmqpConfig.EMAIL_ROUTING_KEY, message);
        }
        return new ResponseEntity<>(new GenericResponse(Status.ENQUEUED, "Messages enqueued"), HttpStatus.ACCEPTED);
    }

}
