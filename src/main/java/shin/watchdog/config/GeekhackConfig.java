package shin.watchdog.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import shin.watchdog.data.TopicSubscription;
import shin.watchdog.data.GeekhackUser;

@Component
@ConfigurationProperties(prefix = "watchdog.geekhack")
public class GeekhackConfig{

    private final List<GeekhackUser> users = new ArrayList<>();
    private final Map<String, TopicSubscription> updatedTopics = new HashMap<>();

    /**
     * @return the users
     */
    public List<GeekhackUser> getUsers() {
        return users;
    }

    /**
     * @return the updatedTopics
     */
    public Map<String, TopicSubscription> getUpdatedTopics() {
        return updatedTopics;
    }
}