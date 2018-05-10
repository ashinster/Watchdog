package shin.watchdog.interfaces;

import shin.watchdog.data.SearchItem;

public interface PotentialChecker {
    boolean checkPotential(String title, String description);
    boolean isValidTitle(String title);
    void setSearch(SearchItem searchItem);
}