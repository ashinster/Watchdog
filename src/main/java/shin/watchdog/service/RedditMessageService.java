package shin.watchdog.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.MechmarketPost;
import shin.watchdog.utils.WatchdogUtils;

@Service
public class RedditMessageService  {

    private boolean isDebug;

    private SimpleDateFormat sdfLocal;

	public RedditMessageService(){
        this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    public RedditMessageService(boolean isDebug){
        this();
        this.isDebug = isDebug;
    }

    public boolean sendMessage(String subredditName, List<MechmarketPost> potentialPosts, String user){
        Set<String> matchesForAllPosts = new HashSet<>();
        StringBuilder message = new StringBuilder();
        for(MechmarketPost post : potentialPosts){

            matchesForAllPosts.addAll(post.getMatches());
            
            String selftext = post.data.selftext;
            if(selftext.isEmpty() && post.data.crosspost_parent_list != null && post.data.crosspost_parent_list.length >= 1){
                selftext = post.data.crosspost_parent_list[0].selftext;
            }

            String hyperlinks = "";
            if(post.isListing()){
                hyperlinks = WatchdogUtils.sendPmLink(post.getMatches(), post.data.author);
            }

            message.append("\"" + post.data.title + "\"").append("\n");
            message.append("by /u/" + post.data.author).append("\n");
            message.append(sdfLocal.format(new Date(post.data.createdUtc*1000))).append("\n\n&nbsp;\n\n");
            message.append(selftext).append("\n\n&nbsp;\n\n");
            message.append(hyperlinks);
    
            message.append("[Leave a comment](" + post.data.url + ")");
            message.append("\n\n***\n***\n");
        }

        if(!isDebug){
            return SendPrivateMessage.sendPM(Arrays.toString(matchesForAllPosts.toArray()) + " Listing(s) Found", message.toString(), user);
        } else {
            System.out.println(Arrays.toString(matchesForAllPosts.toArray()) + " Listings Found\n" + message.toString());
            return false;
        }
    }
}