package com.sensedia.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sensedia")
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
