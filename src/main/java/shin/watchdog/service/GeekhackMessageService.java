package shin.watchdog.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.Item;
import shin.watchdog.interfaces.SiteData;

public class GeekhackMessageService implements MessageService{

    final static Logger logger = LoggerFactory.getLogger(GeekhackMessageService.class);
    
    private boolean isDebug;
	private SimpleDateFormat sdfLocal;
	private SimpleDateFormat sdfGmt;
	private String boardName;

	public GeekhackMessageService(String boardName){
        this.boardName = boardName;
		this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
		this.sdfGmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    }

    public GeekhackMessageService(String boardName, boolean isDebug){
        this(boardName);
        this.isDebug = isDebug;
    }

    @Override
    public boolean sendMessage(List<SiteData> potentialPosts, List<String> users){
        ArrayList<String> formattedItems = new ArrayList<>();

        StringBuilder entries = new StringBuilder();

        for(SiteData siteData : potentialPosts){
            Item post = (Item) siteData;
            StringBuilder entry = new StringBuilder();
            try {
                String localDate = sdfLocal.format(sdfGmt.parse(post.pubDate));
                entry.append(post.title).append("\n\n");
                entry.append(localDate).append("\n\n");
                entry.append(post.guid).append("\n\n");
                entry.append("*****\n\n");

                // Add this entry
                formattedItems.add(entry.toString());

            } catch (ParseException e) {
                logger.error("Could not parse Geekhack pubDate", e);
            }
        }                

        if(!formattedItems.isEmpty()){
            // Append the beginning title and footer for the entries
            formattedItems.add(0, "**New " + this.boardName + "**\n\n");
            formattedItems.add("*****\n&nbsp;\n\n");

            for(String s : formattedItems){
                entries.append(s);
            }
        }

        if(!isDebug){
            return new SendPrivateMessage().sendPM("New " + this.boardName + " Found on Geekhack", entries.toString(), users);
        } else {
            System.out.println("New " + this.boardName + " Found on Geekhack\n" + entries.toString());
            return false;
        }
    }
}