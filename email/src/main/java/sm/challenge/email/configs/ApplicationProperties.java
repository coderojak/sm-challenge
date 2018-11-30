package sm.challenge.email.configs;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Component
@Validated
@ConfigurationProperties("application")
public class ApplicationProperties {

    @Data
    public static class SendGrid {

        @NotNull
        private String endpoint;

        @NotNull
        private String token;

    }

    @Data
    public static class Mailgun {

        @NotNull
        private String endpoint;

        @NotNull
        private String username;

        @NotNull
        private String password;

    }

    @NotNull
    private String emailDomain;

    @NotNull
    private SendGrid sendGrid;

    @NotNull
    private Mailgun mailgun;

}
