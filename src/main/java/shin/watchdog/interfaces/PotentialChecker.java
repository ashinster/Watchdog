package shin.watchdog.interfaces;

import shin.watchdog.data.SearchItem;

public interface PotentialChecker {
    boolean check();
    void setSearch(SearchItem searchItem);
    void setPost(SiteData post);
    boolean checkPotential(String... dataToSearch);
}