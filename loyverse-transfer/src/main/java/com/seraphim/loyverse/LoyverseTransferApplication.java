package com.seraphim.loyverse;

import com.seraphim.loyverse.service.ReceiptService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoyverseTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyverseTransferApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ReceiptService receiptService) {
        return args -> {
            receiptService.transferReceipts();
        };
    }
}
