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
package com.example.demo.mongo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfiguration {
    @Bean
    CommandLineRunner commandLineRunner(StudentRepo studentRepo) {
        return args -> {
            studentRepo.deleteAll();
            Student student = new Student();
            student.setFirstName("George");
            student.setLastName("Foster");
            student.setId(1);
            System.out.println("before saving student = " + student);
            student = studentRepo.save(student);
            System.out.println("after saving student = " + student);
            studentRepo.findAll().forEach(student1 -> {
                System.out.println("student1 = " + student1);
            });
        };
    }
}
