package effectiveMobile.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class BankingOperationsServiceApplication {
	//todo логирование пришедших запросов - выводить как запрос пришел
	public static final Logger logger = LoggerFactory.getLogger(BankingOperationsServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BankingOperationsServiceApplication.class, args);
	}

}