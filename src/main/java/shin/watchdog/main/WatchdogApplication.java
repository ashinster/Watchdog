package shin.watchdog.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import shin.watchdog.service.RefreshTokenService;

@SpringBootApplication
@ComponentScan(basePackages = {"shin.watchdog"})
public class WatchdogApplication{

    public static void main(String[] args) throws Exception {
		//RefreshTokenService.refreshToken();
		SpringApplication.run(WatchdogApplication.class, args);
	}
}