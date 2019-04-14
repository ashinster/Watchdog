package shin.watchdog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * BotConfig
 */
@Configuration
public class BotConfig {

    @Value("${WATCHDOG_BOT_TOKEN}")
    private String token;

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

}