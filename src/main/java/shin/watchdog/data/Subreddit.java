package shin.watchdog.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import shin.watchdog.interfaces.PotentialChecker;

/**
 * Used to hold information about the recent MechMarket posts. Holds a cache of previous posts to compare with newly retrieved posts.
 */
public class Subreddit {

    /**
     * The subreddit id
     */
    private String id;

    /**
     * The name of the board itself.
     */
    public String name;

    /**
     * A set which acts as a cache to store the previous IDs, which is used to compare against newly retrieved posts. 
     * <br/><br/>
     * Note: This will always be set to be a copy of newly retrieved list of IDs. This is so we always have an updated list instead
     * of adding onto an old list. Because of this, the size of the cache should be equal to the number of fetched posts
     */
    public Set<String> postsCache;
    
    /**
     * The items to look for in this subreddit
     */
    public Map<String, SearchItem> searchItems;

    /**
     * The object which determines how to check potential interested posts. Each subreddit may be different
     */
    private PotentialChecker potentialChecker;

    /**
     * Constructor which takes in the subreddit info and creates an empty topics/posts cache
     * @param name The name of the subreddit
     */
    public Subreddit(String id, String boardName, Map<String, SearchItem> searchItems, PotentialChecker potentialChecker){
        this.id = id;
        this.name = boardName;
        this.postsCache = new HashSet<>();
        this.searchItems = searchItems;
        this.potentialChecker = potentialChecker;
    }

    public boolean checkPotential(String title, String description){
        return potentialChecker.checkPotential(title, description);
    }

    public void setSearchItem(String key){
        potentialChecker.setSearch(searchItems.get(key));
    }
}