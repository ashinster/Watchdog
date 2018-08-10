package shin.watchdog.test;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import shin.watchdog.data.Feed;
import shin.watchdog.service.GeekhackPostsService;

public class TestRssFeed {

    @Test
    public void getRssFeed(){
        try {
            GeekhackPostsService service = new GeekhackPostsService();
            
            Feed feed = service.makeCall("132", "Interest Checks");

            Assert.assertEquals("geekhack - Interest Checks", feed.getTitle());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }


}
