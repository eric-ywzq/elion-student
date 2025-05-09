package com.example;

import com.example.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SpringBootConfiguration
public class StudentApplicationTests{

    @Test
    void contextLoads() {
        assertTrue(true);
        assertNotNull(Student.class);
        System.out.println("Hello World");
    }
}
