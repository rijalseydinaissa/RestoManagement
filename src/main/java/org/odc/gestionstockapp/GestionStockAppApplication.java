package org.odc.gestionstockapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"org.odc.gestionstockapp"})
@EnableJpaRepositories(basePackages = "org.odc.gestionstockapp.Datas.Repositories")
@EntityScan(basePackages = "org.odc.gestionstockapp.Datas.Entities")
@EnableTransactionManagement
public class GestionStockAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionStockAppApplication.class, args);
    }

}
