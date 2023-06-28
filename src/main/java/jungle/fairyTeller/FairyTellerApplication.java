package jungle.fairyTeller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class FairyTellerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FairyTellerApplication.class, args);
	}

}
