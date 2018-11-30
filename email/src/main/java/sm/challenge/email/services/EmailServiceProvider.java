package sm.challenge.email.services;

import java.util.List;

import org.springframework.stereotype.Service;

import sm.challenge.email.to.Email;

@Service
interface EmailServiceProvider {

    void sendEmail(Email from, List<Email> to, List<Email> cc, List<Email> bcc,
            String subject, String message);

}
