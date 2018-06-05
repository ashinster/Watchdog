package shin.watchdog.site;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Site {

    final static Logger logger = LoggerFactory.getLogger(Site.class);

    protected SimpleDateFormat sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
    protected SimpleDateFormat sdfGmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    protected String id;
    protected String name;
    protected long interval;
    
    public Site(String id, String name, long interval){
        this.id = id;
        this.name = name;
        this.interval = interval;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public long getInterval(){
        return interval;
    }

    abstract public void process();

}