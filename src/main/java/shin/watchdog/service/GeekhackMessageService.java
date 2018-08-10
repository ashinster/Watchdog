package shin.watchdog.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.Entry;

@Service
public class GeekhackMessageService{

    final static Logger logger = LoggerFactory.getLogger(GeekhackMessageService.class);

    @Value("${isDebug}")
    private boolean isDebug;

	private SimpleDateFormat sdfLocal;

	public GeekhackMessageService(){
		this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
    }

    public GeekhackMessageService(boolean isDebug){
        this();
        this.isDebug = isDebug;
    }

    public boolean sendMessage(String boardName, List<Entry> potentialPosts, String user){
        ArrayList<String> formattedItems = new ArrayList<>();

        StringBuilder entries = new StringBuilder();

        for(Entry siteData : potentialPosts){
            Entry post = (Entry) siteData;
            String localDate = sdfLocal.format(Instant.parse(post.getPublished()).toEpochMilli());

            StringBuilder message = new StringBuilder();
            message.append("\"" + post.getTitle() + "\" by " + post.getAuthor().getName()).append("\n\n");
            message.append("Posted on " + localDate).append("\n\n");
            message.append(post.getId()).append("\n\n");
            message.append("> " + post.getSummary().getValue()).append("\n\n");
            message.append("***\n\n");

            // Add this entry
            formattedItems.add(message.toString());
        }                

        if(!formattedItems.isEmpty()){
            // Append the beginning title and footer for the entries
            formattedItems.add(0, "**New " + boardName + "**\n\n");
            formattedItems.add("***\n*This message was created at " + sdfLocal.format(new Date(System.currentTimeMillis())) + "*");

            for(String s : formattedItems){
                entries.append(s);
            }
        }

        if(!isDebug){
            return SendPrivateMessage.sendPM("New " + boardName + " Found on Geekhack", entries.toString(), user);
        } else {
            logger.info("New " + boardName + " Found on Geekhack\n" + entries.toString());
            return false;
        }
    }
}