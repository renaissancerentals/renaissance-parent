package com.renaissancerentals.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.renaissancerentals.FoundationApplication;

@SpringBootApplication
public class MailApplication {
    public static void main(String[] args){
        SpringApplication.run(FoundationApplication.class,args);
    }
}
