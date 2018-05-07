package shin.watchdog.data;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Rss{
    @XmlElement
    public Channel channel;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Channel{
        @XmlElement
        public String title;
    
        @XmlElement
        public String link;
    
        @XmlElement
        public String description;
    
        @XmlElement
        public ArrayList<Item> item;
    }
}

