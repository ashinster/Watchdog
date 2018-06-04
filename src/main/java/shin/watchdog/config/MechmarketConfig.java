package shin.watchdog.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import shin.watchdog.data.WatchdogUser;

@Component
@ConfigurationProperties(prefix = "watchdog.mechmarket")
public class MechmarketConfig{

    private long interval;

    private final List<WatchdogUser> users = new ArrayList<>();

    /**
     * @return the users
     */
    public List<WatchdogUser> getUsers() {
        return users;
    }

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
}