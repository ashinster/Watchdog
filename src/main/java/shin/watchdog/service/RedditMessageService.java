package shin.watchdog.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.MechmarketPost;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.utils.WatchdogUtils;

public class RedditMessageService implements MessageService {

    private boolean isDebug;

	public RedditMessageService(){
    }

    public RedditMessageService(boolean isDebug){
        this();
        this.isDebug = isDebug;
    }

    @Override
    public boolean sendMessage(List<SiteData> potentialPosts, List<String> users){
        Set<String> matchesForAllPosts = new HashSet<>();
        StringBuilder message = new StringBuilder();
        for(SiteData siteData : potentialPosts){
            MechmarketPost post = (MechmarketPost) siteData;

            matchesForAllPosts.addAll(post.getMatches());

            
            String selftext = post.data.selftext;
            if(selftext.isEmpty() && post.data.crosspost_parent_list != null && post.data.crosspost_parent_list.length >= 1){
                selftext = post.data.crosspost_parent_list[0].selftext;
            }

            String hyperlinks = "";
            if(post.isListing()){
                hyperlinks = WatchdogUtils.sendPmLink(post.getMatches(), post.data.author);
            }

            message.append(String.format(
                "\"%s\"\n/u/%s\n\n\\---- Start " + Arrays.toString(post.getMatches().toArray()) + " Post ----/\n\n%s\n\n\\---- End Post ----/\n\n%s", 
                    post.data.title, post.data.author, selftext, hyperlinks
            ));
    
            message.append("[Leave a comment](" + post.data.url + ")");
            message.append("\n\n*****\n*****\n");
        }

        if(!isDebug){
            return new SendPrivateMessage().sendPM(Arrays.toString(matchesForAllPosts.toArray()) + " Listing(s) Found", message.toString(), users);
        } else {
            System.out.println(Arrays.toString(matchesForAllPosts.toArray()) + " Listings Found\n" + message.toString());
            return false;
        }
    }
}