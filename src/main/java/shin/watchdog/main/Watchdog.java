package shin.watchdog.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import shin.watchdog.site.Board;
import shin.watchdog.processor.MechmarketProcessor;

@Configuration
@EnableScheduling
public class Watchdog{
    private static final Logger log = LoggerFactory.getLogger(Watchdog.class);

    @Autowired
    private Board interestChecks;

    @Autowired
    private Board groupBuys;

    @Autowired
    private MechmarketProcessor mechmarket;

    @Scheduled(cron = "0 * * * * *")
    public void getInterestChecks(){
        interestChecks.process();
    }

    @Scheduled(cron = "0 * * * * *")
    public void getGroupBuys(){
        groupBuys.process();
    }

    @Scheduled(fixedRate = 5000)
    public void getMechmarketPosts(){
        mechmarket.process();
    }
}