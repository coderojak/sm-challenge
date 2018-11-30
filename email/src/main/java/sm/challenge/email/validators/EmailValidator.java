package sm.challenge.email.validators;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sm.challenge.email.configs.ApplicationProperties;
import sm.challenge.email.to.Email;
import sm.challenge.email.to.EmailMessage;

@Component
@RequiredArgsConstructor
public class EmailValidator {

    @NonNull
    private final ApplicationProperties properties;

    private static void assertEmail(Email email, String fieldName) {
        Assert.notNull(email.getEmail(), String.format("'%s.email' cannot be null", fieldName));
        Assert.isTrue(email.getEmail().matches(".+\\@.+\\..+"),
                String.format("'%s.email' format is invalid", fieldName));
    }

    public void validateEmailMessages(List<EmailMessage> emailMessages) {
        Assert.notEmpty(emailMessages, "Root list cannot be empty");
        for (EmailMessage emailMessage : emailMessages) {

            Assert.notNull(emailMessage, "Root item cannot be null");

            assertEmail(emailMessage.getFrom(), "from");
            Assert.isTrue(emailMessage.getFrom().getEmail().endsWith(properties.getEmailDomain()),
                    "'from.email' must be of domain: " + properties.getEmailDomain());
            Assert.notNull(emailMessage.getFrom(), "'from' cannot be null.");

            Assert.notEmpty(emailMessage.getTo(), "'to' cannot be empty.");
            for (Email to : emailMessage.getTo()) {
                assertEmail(to, "to");
            }

            if (emailMessage.getCc() == null) {
                emailMessage.setCc(Optional.ofNullable(emailMessage.getCc()).orElse(Collections.emptyList()));
            } else {
                for (Email cc : emailMessage.getCc()) {
                    assertEmail(cc, "cc");
                }
            }

            if (emailMessage.getBcc() == null) {
                emailMessage.setBcc(Optional.ofNullable(emailMessage.getBcc()).orElse(Collections.emptyList()));
            } else {
                for (Email bcc : emailMessage.getBcc()) {
                    assertEmail(bcc, "bcc");
                }
            }

            emailMessage.setSubject(Optional.ofNullable(emailMessage.getSubject()).orElse(""));
            emailMessage.setMessage(Optional.ofNullable(emailMessage.getMessage()).orElse(""));
        }
    }

}
