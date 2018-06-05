package shin.watchdog.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shin.watchdog.site.Board;
import shin.watchdog.processor.MechmarketProcessor;

@Configuration
public class WatchdogConfig{

    @Bean
    public Board interestChecks(){
        return new Board("132", "Interest Checks", Arrays.asList("timidsa", "H3NT4I"), 60);
    }

    @Bean
    public Board groupBuys(){
        return new Board("70", "Group Buys", Arrays.asList("timidsa", "H3NT4I"), 60);
    }

    @Bean
    public MechmarketProcessor mechmarket(){        
        return new MechmarketProcessor();
    }
}