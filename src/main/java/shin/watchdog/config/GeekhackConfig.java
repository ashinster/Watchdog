package shin.watchdog.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "watchdog.geekhack")
public class GeekhackConfig{

    private long interval;

    private final List<String> users = new ArrayList<>();

    /**
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * @return the users
     */
    public List<String> getUsers() {
        return users;
    }
}