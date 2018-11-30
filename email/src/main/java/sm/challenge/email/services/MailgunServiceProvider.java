package sm.challenge.email.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sm.challenge.email.configs.ApplicationProperties;
import sm.challenge.email.configs.ApplicationProperties.Mailgun;
import sm.challenge.email.exceptions.EmailException;
import sm.challenge.email.to.Email;

@Service
@Slf4j
@RequiredArgsConstructor
class MailgunServiceProvider implements EmailServiceProvider {

    @NonNull
    private final ApplicationProperties properties;

    @NonNull
    private final RestTemplate restTemplate;

    private static String toString(Email email) {
        if (StringUtils.hasText(email.getName())) {
            return String.format("%s <%s>", email.getName(), email.getEmail());
        } else {
            return email.getEmail();
        }
    }

    @Override
    public void sendEmail(Email from, List<Email> to, List<Email> cc, List<Email> bcc, String subject, String message) {
        log.info("Invoking Mailgun");

        Mailgun mailgun = properties.getMailgun();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(mailgun.getUsername(), mailgun.getPassword());

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("from", toString(from));
        data.add("to", String.join(",", to.stream().map(e -> toString(e)).collect(Collectors.toList())));
        if (!cc.isEmpty()) {
            data.add("cc", String.join(",", cc.stream().map(e -> toString(e)).collect(Collectors.toList())));
        }
        if (!bcc.isEmpty()) {
            data.add("bcc", String.join(",", bcc.stream().map(e -> toString(e)).collect(Collectors.toList())));
        }
        data.add("subject", subject);
        data.add("text", message);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(data, headers);

        ResponseEntity<String> response = restTemplate.exchange(mailgun.getEndpoint(), HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new EmailException("Mailgun with status: " + response.getStatusCode());
        }

        log.info(response.getBody());
    }

}
