package shin.watchdog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "watchdog")
public class GeekhackConfig{

    @Value("${watchdog.message.discord}")
    private String messageFormat;

    /**
     * @return the messageFormat
     */
    public String getMessageFormat() {
        return messageFormat;
    }

    /**
     * @param messageFormat the messageFormat to set
     */
    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }
}