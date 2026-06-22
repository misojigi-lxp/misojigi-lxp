package wanted.misojigi.lxpnext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LxpNextApplication {

	public static void main(String[] args) {
		SpringApplication.run(LxpNextApplication.class, args);
	}
}