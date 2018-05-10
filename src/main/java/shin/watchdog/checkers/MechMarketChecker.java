package shin.watchdog.checkers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shin.watchdog.data.SearchItem;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.utils.WatchdogUtils;

public class MechMarketChecker implements PotentialChecker{

	private String searchTerm;
	private List<String> excludesList;

	@Override
    public boolean checkPotential(String title, String description){
        boolean hasPotential = false;

		if (isValidTitle(title)) {
			String[] splitTitle = title.split("\\[W\\]", 2);
			if (splitTitle.length <= 1) {
				System.out.println("Error when splitting title: " + title);
			} else {
				String titleHave = splitTitle[0].toLowerCase().trim();
				String titleWant = splitTitle[1].toLowerCase().trim();

				if(WatchdogUtils.wantsMoney(titleWant)){
					description = description.toLowerCase().trim();

					// Remove all instances of whatever is in the excluded words list for each search term
					for(String excludeString : excludesList) {
						description = description.replace(excludeString.toLowerCase().trim(), "");
						titleHave = titleHave.replace(excludeString.toLowerCase().trim(), "");
					}

					// Look for the search terms in title and description
					searchTerm = searchTerm.toLowerCase().trim();
					if ((titleHave.contains(searchTerm) || description.contains(searchTerm)) && !titleWant.contains(searchTerm)) {
						hasPotential = true;
					}

				}
			}
		} else {
			System.out.println(title + " is invalid");
		}
		return hasPotential;
    }

	@Override
    public boolean isValidTitle(String title){
		Pattern havePattern = Pattern.compile("\\[\\s*[Ww]\\s*\\]");
		Pattern wantPattern = Pattern.compile("\\[\\s*[Hh]\\s*\\]");
		Matcher haveMatcher = havePattern.matcher(title);
		Matcher wantMatcher = wantPattern.matcher(title);

		boolean isValid = false;

		if(haveMatcher.find() && wantMatcher.find()){
			isValid = true;
		}

		return isValid;
	}

	@Override
	public void setSearch(SearchItem searchItem){
		this.searchTerm = searchItem.searchTerm;
		this.excludesList = searchItem.excludedTerms;
	}

}