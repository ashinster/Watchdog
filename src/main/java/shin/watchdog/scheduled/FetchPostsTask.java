package shin.watchdog.scheduled;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import shin.watchdog.data.SearchItem;

public class FetchPostsTask {
    private final ScheduledExecutorService scheduler = 
    		Executors.newScheduledThreadPool(1);
    
    ScheduledFuture<?> newPostHandle;

    Entry<String, ArrayList<String>> searchEntry;

    ArrayList<SearchItem> searchItems;

    public FetchPostsTask(ArrayList<SearchItem> searchItems){
        this.searchItems = searchItems;
    }
    
    public void start() {
    	FetchPostRunnable doFetchPosts = new FetchPostRunnable(searchItems);
    	
    	// Check posts every x seconds
        newPostHandle =
                scheduler.scheduleAtFixedRate(doFetchPosts, 2, 5, TimeUnit.SECONDS);
    }
    
    public void stop() {
        System.out.println("Stopping FetchPostsTask");
    	newPostHandle.cancel(false);
    }
}
