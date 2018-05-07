package shin.watchdog.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GHInterestChecksTask{
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    ScheduledFuture<?> newICHandler;

    public void start() {
    	GHInterestChecksRunnable getNewInterestChecks = new GHInterestChecksRunnable();
    	
    	// Check posts every x seconds
        newICHandler = scheduler.scheduleAtFixedRate(getNewInterestChecks, 2, 60, TimeUnit.SECONDS);
    }

}