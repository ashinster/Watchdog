package shin.watchdog.test;

import org.junit.Test;

import shin.watchdog.scheduled.GHInterestChecksRunnable;

public class TestRssFeed {

    @Test
    public void getRssFeed(){
        GHInterestChecksRunnable icRunnable = new GHInterestChecksRunnable(true);
        
        icRunnable.run();
    }

}
