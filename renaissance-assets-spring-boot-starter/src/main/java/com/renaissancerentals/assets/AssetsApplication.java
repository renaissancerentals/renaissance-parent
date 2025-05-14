package com.renaissancerentals.assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.renaissancerentals.foundation.FoundationApplication;

@SpringBootApplication
public class AssetsApplication {
    public static void main(String[] args){
        SpringApplication.run(FoundationApplication.class,args);
    }
}
