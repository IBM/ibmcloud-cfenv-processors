/*
 *   Copyright 2019 IBM Corporation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.example.demo.amqp;

import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner implements CommandLineRunner {

    private final RabbitOperations rabbitOperations;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitOperations rabbitOperations) {
        this.receiver = receiver;
        this.rabbitOperations = rabbitOperations;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending message to rabbit");
        rabbitOperations.convertAndSend(AMQPConfiguration.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}