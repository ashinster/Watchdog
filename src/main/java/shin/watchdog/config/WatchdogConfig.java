package shin.watchdog.config;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shin.watchdog.checkers.GHNewTopicCheck;
import shin.watchdog.interfaces.Checker;
import shin.watchdog.processor.GbAndIcProcessor;
import shin.watchdog.processor.GeekhackProcessor;

@Configuration
public class WatchdogConfig{

    @Autowired
    protected GeekhackConfig config;

    // @Autowired
    // private Checker updatedTopicChecker;

    @Bean
    public Checker newTopicChecker(){
        return new GHNewTopicCheck(config.getNewTopics());
    }

    // @Bean
    // public Checker updatedTopicChecker(){
    //     return new GHUpdatedTopicCheck(config.getUpdatedTopics());
    // }

    @Bean
    public GeekhackProcessor gbAndIcProcessor() throws JAXBException{
        return new GbAndIcProcessor(
            "https://geekhack.org/index.php?action=.xml;sa=news;type=atom;limit=3;board=132",
            "Interest Checks and Group Buys", 
            "https://discordapp.com/api/webhooks/477261547517902848/eq1z6lMMo4-xdz5WAw3xK9DXKFWBUjPwunbeCHwJbRBYNVToqUailAVEB4-08yc8FyHh",
            "<@&477264441319096321>"
        );
    }

    // @Bean
    // public GeekhackProcessor updatedThreadsProcessor() throws JAXBException{
    //     return new GeekhackProcessor(
    //         "https://geekhack.org/index.php?action=.xml;type=atom;limit=10;board=70;sa=recent", 
    //         "Update for a Geekhack Group Buy Thread", 
    //         "https://discordapp.com/api/webhooks/483098053381849093/VkwqJi4zNO65ydNGmtn42Ac4eNOZR3DLcglRDclNqiJW4A0G7hQtym1Bv5jkJgM8GWJq",
    //         ""
    //     );
    // }

    // @Bean
    // public MechmarketProcessor mechmarketProcessor(){
    //     return new MechmarketProcessor("Mechmarket");
    // }
}