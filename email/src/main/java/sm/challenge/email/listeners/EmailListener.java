package sm.challenge.email.listeners;

import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import sm.challenge.email.services.EmailService;
import sm.challenge.email.to.EmailMessage;

@Component
@RequiredArgsConstructor
public class EmailListener {

    @NonNull
    private EmailService service;

    public void sendEmail(EmailMessage emailMessage) {
        try {
            service.sendEmail(emailMessage);
        } catch (Throwable t) {
            throw new ImmediateAcknowledgeAmqpException(t);
        }
    }

}
