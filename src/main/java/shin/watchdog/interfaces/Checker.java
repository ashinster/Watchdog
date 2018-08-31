package shin.watchdog.interfaces;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import shin.watchdog.config.GeekhackConfig;
import shin.watchdog.data.AlertTopic;
import shin.watchdog.data.atom.Entry;

@Component
public abstract class Checker{
    final static Logger logger = LoggerFactory.getLogger(Checker.class);

    @Autowired
    protected GeekhackConfig config;
    
    protected Map<String, AlertTopic> alertTopics;
    
    protected Checker(){}

    public abstract boolean check(Entry entry);

    /**
     * @param key The author
     * Uses the author name as a string to get the 
     * recipient to alert/ping for
     */
    public String getRecipientForTopic(String key){
        return alertTopics.get(key).getRoleId();
    }

    public boolean isAlertListEmpty(){
        return alertTopics.isEmpty();
    }
}