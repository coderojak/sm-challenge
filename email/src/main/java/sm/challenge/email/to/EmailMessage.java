package sm.challenge.email.to;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = -4823966462670120045L;

    private Email from;
    private List<Email> to;
    private List<Email> cc;
    private List<Email> bcc;
    private String subject;
    private String message;

}
