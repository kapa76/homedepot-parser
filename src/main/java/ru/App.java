package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.homedepot.common.SiteLoader;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
@Profile("test")
public class App {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(App.class, args);

        SiteLoader parser = new SiteLoader();
        parser.init();
        parser.saveToFile("items.csv");

    }

}
