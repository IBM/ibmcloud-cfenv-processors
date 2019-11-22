package com.example.demo;

import com.example.demo.amqp.AMQPConfiguration;
import com.example.demo.mongo.StudentRepo;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doAnswer;

@SpringBootTest
class DemoApplicationTests {

    @MockBean
    RabbitTemplate rabbitOperations;

    @MockBean
    StudentRepo studentRepo;

    @MockBean
    MessageListenerContainer messageListenerContainer;

    @Test
    void contextLoads() {
        doAnswer((Answer<Void>) invocation -> null)
                .when(rabbitOperations).convertAndSend(AMQPConfiguration.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
    }

}
