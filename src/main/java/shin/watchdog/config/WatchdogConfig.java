package shin.watchdog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shin.watchdog.processor.GbAndIcProcessor;
import shin.watchdog.processor.GeekhackProcessor;

@Configuration
public class WatchdogConfig{

    @Bean
    public GeekhackProcessor icProcessor() {
        // https://geekhack.org/index.php?action=.xml;type=atom;boards132;limit=10;sa=news
        return new GbAndIcProcessor(
            "Interest Checks",
            "https://geekhack.org/index.php?action=.xml;type=atom",
            "132",
            "10",
            "news"
        );
    }

    @Bean
    public GeekhackProcessor gbProcessor() {
        return new GbAndIcProcessor(
            "Group Buys",
            "https://geekhack.org/index.php?action=.xml;type=atom",
            "70",
            "5",
            "news"
        );
    }
}