package shin.watchdog.data;

import shin.watchdog.data.atom.Entry;

/**
 * POJO for a Geekhack thread which contains only the info we need from the RSS/ATOM feed
 */
public class GeekhackThread {
    String title;
    String author;
    String published;
    String id;

    public GeekhackThread(Entry entry){
        this.title = entry.getTitle();
        this.author = entry.getAuthor().getName();
        this.published = entry.getPublished();
        this.id = entry.getId();
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the published
     */
    public String getPublished() {
        return published;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
}