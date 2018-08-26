package shin.watchdog.config;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shin.watchdog.checkers.GHNewTopicCheck;
import shin.watchdog.checkers.GHUpdatedTopicCheck;
import shin.watchdog.interfaces.Checker;
import shin.watchdog.processor.GeekhackProcessor;

@Configuration
public class WatchdogConfig{

    @Autowired
    protected GeekhackConfig config;

    @Autowired
    private Checker newTopicChecker;

    @Autowired
    private Checker updatedTopicChecker;

    @Bean
    public Checker newTopicChecker(){
        return new GHNewTopicCheck(config.getNewTopics());
    }

    @Bean
    public Checker updatedTopicChecker(){
        return new GHUpdatedTopicCheck(config.getUpdatedTopics());
    }

    @Bean
    public GeekhackProcessor interestChecksProcessor() throws JAXBException{
        return new GeekhackProcessor(
            "https://geekhack.org/index.php?action=.xml;sa=news;type=atom;limit=3;board=132",
            "Interest Checks", 
            "https://discordapp.com/api/webhooks/477261735271858176/atBPCQzWMAj_k6PVrJTMqggwaoEnQ7Hz4HlHjyp6hmfGrdIKgNEbbD9hMrmUms3Y5hVq",
            "<@&477264441319096321>",
            newTopicChecker
        );
    }

    @Bean
    public GeekhackProcessor groupBuysProcessor() throws JAXBException{
        return new GeekhackProcessor(
            "https://geekhack.org/index.php?action=.xml;sa=news;type=atom;limit=3;board=70", 
            "Group Buys and Preorders", 
            "https://discordapp.com/api/webhooks/477261735271858176/atBPCQzWMAj_k6PVrJTMqggwaoEnQ7Hz4HlHjyp6hmfGrdIKgNEbbD9hMrmUms3Y5hVq",
            "<@&477264488983429130>",
            newTopicChecker
        );
    }

    @Bean
    public GeekhackProcessor updatedThreadsProcessor() throws JAXBException{
        return new GeekhackProcessor(
            "https://geekhack.org/index.php?action=.xml;type=atom;limit=10;board=70;sa=recent", 
            "Update for a Geekhack Group Buy Thread", 
            "https://discordapp.com/api/webhooks/483098053381849093/VkwqJi4zNO65ydNGmtn42Ac4eNOZR3DLcglRDclNqiJW4A0G7hQtym1Bv5jkJgM8GWJq",
            "",
            updatedTopicChecker
        );
    }

    // @Bean
    // public MechmarketProcessor mechmarketProcessor(){
    //     return new MechmarketProcessor("Mechmarket");
    // }
}