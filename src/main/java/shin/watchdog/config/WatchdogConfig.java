package shin.watchdog.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.data.Board;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Subreddit;
import shin.watchdog.processor.MechmarketProcessor;
import shin.watchdog.service.NewRedditPostsService;

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