package shin.watchdog.scheduled;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Site;

public class PostProcessorTask {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    ScheduledFuture<?> newPostHandle;

    Entry<String, ArrayList<String>> searchEntry;

    ArrayList<SearchItem> searchItems;

    public void start(Site site) {
        Processor doPostProcess = new Processor(site);

        newPostHandle = scheduler.scheduleAtFixedRate(doPostProcess, 1, site.getInterval(), TimeUnit.SECONDS);
    }

    public void stop() {
        System.out.println("Stopping FetchPostsTask");
        newPostHandle.cancel(false);
    }
}