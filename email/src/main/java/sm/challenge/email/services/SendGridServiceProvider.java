package sm.challenge.email.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sm.challenge.email.configs.ApplicationProperties;
import sm.challenge.email.configs.ApplicationProperties.SendGrid;
import sm.challenge.email.exceptions.EmailException;
import sm.challenge.email.to.Email;

@Service
@Slf4j
@RequiredArgsConstructor
class SendGridServiceProvider implements EmailServiceProvider {

    @NonNull
    private final ApplicationProperties properties;

    @NonNull
    private final RestTemplate restTemplate;

    @Data
    @JsonInclude(Include.NON_NULL)
    private static class Payload {
        private List<Personalization> personalizations;

        private Email from;

        @JsonProperty("reply_to")
        private Email replyTo;

        private List<Content> content;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    private static class Personalization {
        private List<Email> to;
        private List<Email> cc;
        private List<Email> bcc;
        private String subject;
    }

    @Data
    @JsonInclude(Include.NON_NULL)
    private static class Content {
        private String type;
        private String value;
    }

    private static Payload buildPayload(Email from, List<Email> to, List<Email> cc, List<Email> bcc, String subject,
            String message) {
        Payload payload = new Payload();

        Personalization personalization = new Personalization();
        personalization.setTo(to);
        if (!cc.isEmpty()) {
            personalization.setCc(cc);
        }
        if (!bcc.isEmpty()) {
            personalization.setBcc(bcc);
        }
        personalization.setSubject(subject);
        payload.setPersonalizations(Arrays.asList(personalization));

        payload.setFrom(from);
        payload.setReplyTo(from);

        Content content = new Content();
        content.setType("text/plain");
        content.setValue(message);
        payload.setContent(Arrays.asList(content));

        return payload;
    }

    @Override
    public void sendEmail(Email from, List<Email> to, List<Email> cc, List<Email> bcc, String subject, String message) {
        log.info("Invoking SendGrid");

        SendGrid sendGrid = properties.getSendGrid();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(sendGrid.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Payload data = buildPayload(from, to, cc, bcc, subject, message);

        HttpEntity<Payload> requestEntity = new HttpEntity<>(data, headers);

        ResponseEntity<String> response = restTemplate.exchange(sendGrid.getEndpoint(), HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new EmailException("SendGrid failed with status: " + response.getStatusCode());
        }
    }

}
