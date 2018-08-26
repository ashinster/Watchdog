package shin.watchdog.data;

import java.util.List;

public class AlertList{

    private List<Entry> newPosts;
    private List<String> usersToPing;

    /**
     * @param newPosts the newPosts to set
     */
    public void setNewPosts(List<Entry> newPosts) {
        this.newPosts = newPosts;
    }

    /**
     * @param usersToPing the usersToPing to set
     */
    public void setUsersToPing(List<String> usersToPing) {
        this.usersToPing = usersToPing;
    }

    public List<Entry> getNewPostsList(){
        return newPosts;
    }

    public List<String> getUsersToPingList(){
        return usersToPing;
    }
}