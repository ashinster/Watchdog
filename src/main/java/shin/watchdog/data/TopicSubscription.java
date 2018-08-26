package shin.watchdog.data;

import java.util.HashMap;
import java.util.Map;

public class TopicSubscription{
    private String topicName;
    private String topicAuthor;
    private final Map<String, String> subscribers = new HashMap<>();

    /**
     * @param topicName the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * @param topicAuthor the topicAuthor to set
     */
    public void setTopicAuthor(String topicAuthor) {
        this.topicAuthor = topicAuthor;
    }

    /**
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @return the topicAuthor
     */
    public String getTopicAuthor() {
        return topicAuthor;
    }

    /**
     * @return the subscribers
     */
    public Map<String, String> getSubscribers() {
        return subscribers;
    }
}