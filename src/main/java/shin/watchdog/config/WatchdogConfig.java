package shin.watchdog.config;

import javax.xml.bind.JAXBException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import shin.watchdog.processor.GeekhackProcessor;
import shin.watchdog.processor.MechmarketProcessor;

@Configuration
public class WatchdogConfig{

    // @Bean
    // public Board interestChecks(){
    //     return new Board("132", "Interest Checks", Arrays.asList("timidsa", "H3NT4I"), 60);
    // }

    // @Bean
    // public Board groupBuys(){
    //     return new Board("70", "Group Buys", Arrays.asList("timidsa", "H3NT4I"), 60);
    // }

    // @Bean
    // public MechmarketProcessor mechmarket(){        
    //     return new MechmarketProcessor("Mechmarket");
    // }

    @Bean
    public GeekhackProcessor interestChecksProcessor() throws JAXBException{
        return new GeekhackProcessor("132", "Interest Checks");
    }

    @Bean
    public GeekhackProcessor groupBuysProcessor() throws JAXBException{
        return new GeekhackProcessor("70", "Group Buys and Preorders");
    }

    @Bean
    public MechmarketProcessor mechmarketProcessor(){
        return new MechmarketProcessor("Mechmarket");
    }
}