package shin.watchdog.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to hold information about the board we're interested in. Holds a cache of previous posts to compare with newly retrieved posts.
 */
public class Board {

    /**
     * The id of the board
     */
    public String boardId;

    /**
     * The full name of the board itself.
     */
    public String boardName;

    /**
     * A set which acts as a cache to store the previous Item topic IDs, which is used to compare against newly retrieved posts from the Task. 
     * <br/><br/>
     * Note: This will always be set the newly retrieved list of item topic IDs from the rss. This is so we always have an updated list instead
     * of adding onto an old list. Because of this, the size of the cache should be equal to the number of posts in the fetched rss.
     */
    public Set<String> topicsCache;
    
    /**
     * Constructor which takes in the board info and creates an empty topics/posts cache
     * @param id The numeric ID of the board as a String
     * @param boardName The full name of the board
     */
    public Board(String id, String boardName){
        this.boardId = id;
        this.boardName = boardName;
        this.topicsCache = new HashSet<>();
    }
}