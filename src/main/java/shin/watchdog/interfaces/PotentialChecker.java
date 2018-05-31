package shin.watchdog.interfaces;

import java.util.List;

import shin.watchdog.data.SearchItem;

public interface PotentialChecker {
    boolean checkPotential(String title, String description, SearchItem searchItems);
    List<SiteData> getPotentialPosts(List<SiteData> newPosts, List<SearchItem> searchItems);
}