package shin.watchdog.data;

import java.util.List;

public class GeekhackUser{

    private String username;
    private String id;
    private List<String> topics;

    // GETTERS
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the topics
     */
    public List<String> getTopics() {
        return topics;
    }

    // SETTERS

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param topics the topics to set
     */
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}