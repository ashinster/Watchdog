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
    public GeekhackProcessor gbAndIcProcessor() throws JAXBException{
        return new GbAndIcProcessor(
            "https://geekhack.org/index.php?action=.xml;sa=news;type=atom;limit=4;boards=132,70",
            "Interest Checks and Group Buys"
        );
    }

    @Bean
    public GeekhackProcessor updatedThreadsProcessor() throws JAXBException{
        return new ThreadUpdatesProcessor(
            "https://geekhack.org/index.php?action=.xml;type=atom;limit=10;board=70;sa=recent", 
            "Update for a Geekhack Group Buy Thread"
        );
    }

    // @Bean
    // public MechmarketProcessor mechmarketProcessor(){
    //     return new MechmarketProcessor("Mechmarket");
    // }
}