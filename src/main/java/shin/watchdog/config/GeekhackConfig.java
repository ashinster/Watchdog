package shin.watchdog.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import shin.watchdog.data.AlertTopic;

@Component
@ConfigurationProperties(prefix = "watchdog.geekhack")
public class GeekhackConfig{

    private final Map<String, AlertTopic> newTopics = new HashMap<>();
    private final Map<String, AlertTopic> updatedTopics = new HashMap<>();

    /**
     * @return the newTopics
     */
    public Map<String, AlertTopic> getNewTopics() {
        return newTopics;
    }

    /**
     * @return the updatedTopics
     */
    public Map<String, AlertTopic> getUpdatedTopics() {
        return updatedTopics;
    }
}