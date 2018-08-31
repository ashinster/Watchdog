package shin.watchdog.test;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import shin.watchdog.data.atom.Feed;
import shin.watchdog.service.GeekhackPostsService;

public class TestRssFeed {

    @Test
    public void getRssFeed(){
        try {
            GeekhackPostsService service = new GeekhackPostsService();
            
            Feed feed = service.makeCall("https://geekhack.org/index.php?action=.xml;sa=news;type=atom;limit=3;board=132", "Interest Checks");

            Assert.assertEquals("geekhack - Interest Checks", feed.getTitle());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }


}
