package sm.challenge.email.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sm.challenge.email.listeners.EmailListener;

@Configuration
public class AmqpConfig {

    public static final String EMAIL_EXCHANGE = "email-exchange";
    public static final String EMAIL_ROUTING_KEY = "email-routing-key";
    private static final String EMAIL_QUEUE = "email-queue";

    @Bean
    public Queue queue() {
        return new Queue(EMAIL_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(EMAIL_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(EmailListener listener) {
        return new MessageListenerAdapter(listener, "sendEmail");
    }

}
