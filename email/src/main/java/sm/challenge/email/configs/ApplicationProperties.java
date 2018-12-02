package sm.challenge.email.configs;

import javax.validation.constraints.NotBlank;
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

        @NotBlank
        private String endpoint;

        @NotBlank
        private String token;

    }

    @Data
    public static class Mailgun {

        @NotBlank
        private String endpoint;

        @NotBlank
        private String username;

        @NotBlank
        private String password;

    }

    @NotBlank
    private String emailDomain;

    @NotNull
    private SendGrid sendGrid;

    @NotNull
    private Mailgun mailgun;

}
