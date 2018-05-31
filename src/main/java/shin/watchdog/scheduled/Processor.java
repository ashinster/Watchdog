package shin.watchdog.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.data.Site;

public class Processor implements Runnable {

    final static Logger logger = LoggerFactory.getLogger(Processor.class);

    private Site site;

    public Processor(Site site){
        this.site = site;
    }

	@Override
	public void run() {
		try{
            site.process();       
            return;     
        } catch (Throwable t) {
            logger.error("Unknown error while executing thread: " + site.getName(), t);
		}
    }
}