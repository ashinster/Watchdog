package shin.watchdog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"shin.watchdog"})
public class WatchdogApplication{

    public static void main(String[] args) throws Exception {
		SpringApplication.run(WatchdogApplication.class, args);
	}
}