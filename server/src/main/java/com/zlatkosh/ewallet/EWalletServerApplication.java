package com.zlatkosh.ewallet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class EWalletServerApplication  implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EWalletServerApplication.class, args);
    }


    @Override
    public void run(String... args) {
        log.info("Started the e-wallet application...");
    }
}
