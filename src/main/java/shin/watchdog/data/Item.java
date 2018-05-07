package shin.watchdog.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Item{
    @XmlElement
    public String title;

    @XmlElement
    public String link;

    @XmlElement
    public String description;

    @XmlElement
    public String comments;

    @XmlElement
    public String category;

    @XmlElement
    public String pubDate;

    @XmlElement
    public String guid;
}