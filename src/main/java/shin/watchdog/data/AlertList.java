package shin.watchdog.data;

import java.util.List;
import java.util.Set;

public class AlertList{

    private List<Entry> newPostsList;
    private Set<String> usersToPingList;

    /**
     * @param newPosts the newPosts to set
     */
    public void setNewPostsList(List<Entry> newPosts) {
        this.newPostsList = newPosts;
    }

    /**
     * @param usersToPing the usersToPing to set
     */
    public void setUsersToPingList(Set<String> usersToPing) {
        this.usersToPingList = usersToPing;
    }

    /**
     * @return the newPosts
     */
    public List<Entry> getNewPostsList() {
        return newPostsList;
    }

    /**
     * @return the usersToPing
     */
    public Set<String> getUsersToPingList() {
        return usersToPingList;
    }
}