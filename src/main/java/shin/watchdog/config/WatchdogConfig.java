package shin.watchdog.config;

import javax.xml.bind.JAXBException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shin.watchdog.checkers.GHNewTopicCheck;
import shin.watchdog.checkers.GHUpdatedTopicCheck;
import shin.watchdog.interfaces.Checker;
import shin.watchdog.processor.GbAndIcProcessor;
import shin.watchdog.processor.GeekhackProcessor;
import shin.watchdog.processor.ThreadUpdatesProcessor;

@Configuration
public class WatchdogConfig{

    @Bean
    public Checker newTopicChecker(){
        return new GHNewTopicCheck();
    }

    @Bean
    public Checker updatedTopicChecker(){
        return new GHUpdatedTopicCheck();
    }

    @Bean
    public GeekhackProcessor icProcessor() throws JAXBException{
        return new GbAndIcProcessor(
            "https://geekhack.org/index.php?action=.xml;type=atom",
            "132",
            "5",
            "news"
        );
    }

    @Bean
    public GeekhackProcessor gbProcessor() throws JAXBException{
        return new GbAndIcProcessor(
            "https://geekhack.org/index.php?action=.xml;type=atom",
            "70",
            "5",
            "news"
        );
    }

    @Bean
    public GeekhackProcessor updatedThreadsProcessor() throws JAXBException{
        return new ThreadUpdatesProcessor(
            "https://geekhack.org/index.php?action=.xml;type=atom", 
            "132,70",
            "10",
            "recent"
        );
    }

    // @Bean
    // public MechmarketProcessor mechmarketProcessor(){
    //     return new MechmarketProcessor("Mechmarket");
    // }
}