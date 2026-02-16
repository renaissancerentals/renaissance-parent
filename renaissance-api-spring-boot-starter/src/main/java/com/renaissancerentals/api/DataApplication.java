package com.renaissancerentals.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.renaissancerentals.data", "com.renaissancerentals.persistence"})
public class DataApplication {

    public static void main(String[] args){
        SpringApplication.run(DataApplication.class,args);
    }

}
