package shin.watchdog.data;

import shin.watchdog.data.atom.Entry;

public class Alert{
    String title;
    String author;
    String published;
    String id;

    // User or role to ping
    String recipient;

    public Alert(Entry entry){
        this.title = entry.getTitle();
        this.author = entry.getAuthor().getName();
        this.published = entry.getPublished();
        this.id = entry.getId();
    }

    /**
     * @param recipient the recipient to set
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
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

    /**
     * @return the user
     */
    public String getRecipient() {
        return recipient;
    }
}