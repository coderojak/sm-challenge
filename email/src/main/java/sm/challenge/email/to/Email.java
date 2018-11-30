package sm.challenge.email.to;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Email implements Serializable {

    private static final long serialVersionUID = 6450278358936787763L;

    private String name;
    private String email;

}
