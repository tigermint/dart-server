package com.ssh.dartserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class DartServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DartServerApplication.class, args);
    }

}
