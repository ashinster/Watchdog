package shin.watchdog.checkers;

import shin.watchdog.data.SearchItem;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;

public class GeekhackChecker implements PotentialChecker{

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public void setSearch(SearchItem searchItem) {
		
	}

	@Override
	public void setPost(SiteData post) {
		
	}

	@Override
	public boolean checkPotential(String... dataToSearch) {
		return false;
	}

}