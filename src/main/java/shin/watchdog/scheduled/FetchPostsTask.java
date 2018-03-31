package shin.watchdog.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FetchPostsTask {
    private final ScheduledExecutorService scheduler = 
    		Executors.newScheduledThreadPool(1);
    
    ScheduledFuture<?> newPostHandle;
    
    public void start() {
    	FetchPostRunnable doFetchPosts = new FetchPostRunnable();
    	
    	// Check posts every x seconds
        newPostHandle =
                scheduler.scheduleAtFixedRate(doFetchPosts, 2, 10, TimeUnit.SECONDS);
    }
    
    public void stop() {
    	newPostHandle.cancel(false);
    }
}
