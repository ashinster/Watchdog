package shin.watchdog.checkers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shin.watchdog.data.Post;
import shin.watchdog.data.SearchItem;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.utils.WatchdogUtils;

public class MechMarketChecker implements PotentialChecker{

	private String searchTerm;
	private List<String> excludesList;

	private Post post;

	boolean isDebug;

	public MechMarketChecker(){
		this("", null);
	}

	public MechMarketChecker(String searchTerm, List<String> excludesList){
		this.searchTerm = searchTerm;
		this.excludesList = excludesList;
	}

	public MechMarketChecker(String searchTerm, List<String> excludesList, boolean isDebug){
		this(searchTerm, excludesList);
		this.isDebug = isDebug;
	}

	public MechMarketChecker(boolean isDebug){
		this();
		this.isDebug = isDebug;
	}

	public MechMarketChecker(SearchItem searchItem){
		setSearch(searchItem);
	}

	@Override
	public void setSearch(SearchItem searchItem){
		this.searchTerm = searchItem.searchTerm;
		this.excludesList = searchItem.excludedTerms;
	}

	@Override
	public void setPost(SiteData post) {
		this.post = (Post) post;
	}

	@Override
	public boolean checkPotential(String... dataToSearch) {
		return checkPotential(dataToSearch[0], dataToSearch[1]);
	}

	@Override
	public boolean check() {
		return checkPotential(this.post.data.title, this.post.data.selftext);
	}

    public boolean checkPotential(String title, String description){
        boolean hasPotential = false;

		if (isValidTitle(title)) {
			String[] splitTitle = title.split("\\[\\s*[Ww]\\s*\\]", 2);
			if (splitTitle.length <= 1) {
				System.out.println("Error when splitting title: " + title);
			} else {
				String titleHave = splitTitle[0].toLowerCase().trim();
				String titleWant = splitTitle[1].toLowerCase().trim();

				if(WatchdogUtils.wantsMoney(titleWant) || isDebug){
					description = description.toLowerCase().trim();

					// Remove all instances of whatever is in the excluded words list for each search term
					if(this.excludesList != null){
						for(String excludeString : this.excludesList) {
							description = description.replace(excludeString.toLowerCase().trim(), "");
							titleHave = titleHave.replace(excludeString.toLowerCase().trim(), "");
						}
					}

					// Look for the search terms in title and description
					searchTerm = this.searchTerm.toLowerCase().trim();
					if ((titleHave.contains(searchTerm) || description.contains(searchTerm)) && !titleWant.contains(searchTerm)) {
						System.out.println("Post \"" + title + "\" has potential for " + searchTerm);
						hasPotential = true;
					}
				}
			}
		} else {
			System.out.println(title + " is invalid");
		}
		return hasPotential;
    }

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

}