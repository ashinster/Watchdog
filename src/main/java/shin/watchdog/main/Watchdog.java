package shin.watchdog.main;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import shin.watchdog.processor.GeekhackProcessor;

@Configuration
@EnableScheduling
public class Watchdog{
    //private static final Logger log = LoggerFactory.getLogger(Watchdog.class);

    @Autowired
    private GeekhackProcessor interestChecksProcessor;

    @Autowired
    private GeekhackProcessor groupBuysProcessor;

    @Autowired
    private GeekhackProcessor updatedThreadsProcessor;

    // @Autowired
    // private MechmarketProcessor mechmarket;

    // @Scheduled(fixedRate = 1000 * 60 * 30)
    // public void refreshToken(){
    //     RefreshTokenService.refreshToken();
    // }

    @Scheduled(cron = "0 * * * * *")
    public void getInterestChecks(){
        MDC.put("uuid", UUID.randomUUID().toString());
        interestChecksProcessor.process();
        MDC.clear();
    }

    @Scheduled(cron = "0 * * * * *")
    public void getGroupBuys(){
        MDC.put("uuid", UUID.randomUUID().toString());
        groupBuysProcessor.process();
        MDC.clear();
    }

    @Scheduled(cron = "0 * * * * *")
    public void getUpdatesForThread(){
        MDC.put("uuid", UUID.randomUUID().toString());
        updatedThreadsProcessor.process();
        MDC.clear();
    }

    // @Scheduled(cron = "0/5 * * * * *")
    // public void getMechmarketPosts(){
    //     MDC.put("uuid", UUID.randomUUID().toString());
    //     mechmarket.process();
    //     MDC.clear();
    // }
}