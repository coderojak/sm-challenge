package sm.challenge.email.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EmailControllerTests {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnAccepted() throws Exception {

        /* Minimal payload */
        mockMvc.perform(post("/email/messages").contentType("application/vnd.email.v1+json").content(
                "[{\"from\":{\"email\":\"sender@test.com\"},\"to\":[{\"email\":\"receiver@test.com\"}],\"subject\": \"Test Subject\",\"message\": \"Test message.\"}]"))
                .andDo(print()).andExpect(status().isAccepted())
                .andExpect(content().json("{\"status\":\"ENQUEUED\",\"message\":\"Messages enqueued\"}"));

        /* Full payload */
        mockMvc.perform(post("/email/messages").contentType("application/vnd.email.v1+json")
                .content("[{\"from\":{\"name\":\"Sender\",\"email\":\"sender@test.com\"},"
                        + "\"to\":[{\"name\":\"Receiver 1\",\"email\":\"receiver1@test.com\"},{\"name\":\"Receiver 2\",\"email\":\"receiver2@test.com\"}],"
                        + "\"cc\":[{\"name\":\"Receiver 3\",\"email\":\"receiver3@test.com\"},{\"name\":\"Receiver 4\",\"email\":\"receiver4@test.com\"}],"
                        + "\"bcc\":[{\"name\":\"Receiver 5\",\"email\":\"receiver5@test.com\"},{\"name\":\"Receiver 6\",\"email\":\"receiver6@test.com\"}],"
                        + "\"subject\": \"Test Subject\",\"message\": \"Test message.\"}]"))
                .andDo(print()).andExpect(status().isAccepted())
                .andExpect(content().json("{\"status\":\"ENQUEUED\",\"message\":\"Messages enqueued\"}"));

    }

    @Test
    public void shouldReturnInvalidMediaType() throws Exception {

        /* Unversioned JSON */
        mockMvc.perform(post("/email/messages").contentType("application/json").content(
                "[{\"from\":{\"email\":\"sender@test.com\"},\"to\":[{\"email\":\"receiver@test.com\"}],\"subject\": \"Test Subject\",\"message\": \"Test message.\"}]"))
                .andDo(print()).andExpect(status().isUnsupportedMediaType());

    }

    @Test
    public void shouldReturnBadRequest() throws Exception {

        /* Root list empty */
        mockMvc.perform(post("/email/messages").contentType("application/vnd.email.v1+json").content("[]"))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("status").value("ERROR"));

        /* Omitted email field */
        mockMvc.perform(post("/email/messages").contentType("application/vnd.email.v1+json").content(
                "[{\"from\":{\"name\":\"Sender\"},\"to\":[{\"name\":\"Receiver\"}],\"subject\": \"Test Subject\",\"message\": \"Test message.\"}]"))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("status").value("ERROR"));

        /* Obviously invalid email format */
        mockMvc.perform(post("/email/messages").contentType("application/vnd.email.v1+json").content(
                "[{\"from\":{\"email\":\"sendertest.com\"},\"to\":[{\"email\":\"receivertest.com\"}],\"subject\": \"Test Subject\",\"message\": \"Test message.\"}]"))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("status").value("ERROR"));

        /* Unregistered sender domain */
        mockMvc.perform(post("/email/messages").contentType("application/vnd.email.v1+json").content(
                "[{\"from\":{\"email\":\"sender@unregistered.com\"},\"to\":[{\"email\":\"receiver@test.com\"}],\"subject\": \"Test Subject\",\"message\": \"Test message.\"}]"))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("status").value("ERROR"));

        /* Blank subject and message */
        mockMvc.perform(post("/email/messages").contentType("application/vnd.email.v1+json").content(
                "[{\"from\":{\"email\":\"sender@test.com\"},\"to\":[{\"email\":\"receiver@test.com\"}],\"subject\": \"\",\"message\": \"   \"}]"))
                .andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("status").value("ERROR"));
    }

}
