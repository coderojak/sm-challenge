package sm.challenge.email.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import lombok.extern.slf4j.Slf4j;

import sm.challenge.email.exceptions.EmailException;
import sm.challenge.email.to.EmailMessage;

@Service
@Slf4j
public class EmailService {

    private static final HystrixCommandGroupKey EMAIL_SERVICE_GROUP = HystrixCommandGroupKey.Factory
            .asKey(EmailService.class.getName());

    private final List<EmailServiceProvider> serviceProviders;

    public EmailService(List<EmailServiceProvider> serviceProviders) {
        Assert.notEmpty(serviceProviders, "At least 1 email service provider required.");
        this.serviceProviders = serviceProviders;
    }

    public void sendEmail(EmailMessage emailMessage) {

        class SendEmail {
            HystrixCommand<Void> sendEmail(int i) {
                return new HystrixCommand<Void>(EMAIL_SERVICE_GROUP, 10000) {

                    @Override
                    protected Void run() throws Exception {
                        try {
                            serviceProviders.get(i).sendEmail(emailMessage.getFrom(), emailMessage.getTo(),
                                    emailMessage.getCc(), emailMessage.getBcc(), emailMessage.getSubject(),
                                    emailMessage.getMessage());
                        } catch (Throwable t) {
                            log.debug("Exception in critical section: {} {}", t.getClass().getName(), t.getMessage());
                            throw t;
                        }
                        return null;
                    }

                    @Override
                    protected Void getFallback() {
                        if (i + 1 < serviceProviders.size()) {
                            return sendEmail(i + 1).execute();
                        } else {
                            throw new EmailException("Critical section failovers exhausted.");
                        }
                    }

                };
            }
        }

        new SendEmail().sendEmail(0).execute();
    }

}
