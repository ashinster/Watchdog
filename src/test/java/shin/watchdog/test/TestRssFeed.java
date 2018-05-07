package shin.watchdog.test;

import javax.xml.bind.JAXBException;

import org.junit.*;
import org.junit.Assert.*;

import shin.watchdog.scheduled.GHInterestChecksRunnable;

import org.junit.Test;

public class TestRssFeed {

    @Test
    public void getRssFeed(){
        GHInterestChecksRunnable icRunnable = new GHInterestChecksRunnable(true);
        
        icRunnable.run();
    }

}
