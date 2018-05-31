package shin.watchdog.checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.data.MechmarketPost;
import shin.watchdog.data.Post;
import shin.watchdog.data.SearchItem;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.utils.WatchdogUtils;

public class MechMarketChecker implements PotentialChecker{

	final static Logger logger = LoggerFactory.getLogger(MechMarketChecker.class);

	boolean isDebug;

	List<String> blacklist;

	// For title validating
	Pattern havePattern;
	Pattern wantPattern;
	Pattern otherPattern;

	public MechMarketChecker(){
		havePattern = Pattern.compile("\\[\\s*[Hh]\\s*\\]");
		wantPattern = Pattern.compile("\\[\\s*[Ww]\\s*\\]");
		otherPattern = Pattern.compile("\\[\\s*\\w+\\s*\\]");
		this.blacklist = new ArrayList<>();
	}

	public MechMarketChecker(boolean isDebug){
		this();
		this.isDebug = isDebug;
	}

	public MechMarketChecker(List<String> blacklist, boolean isDebug){
		this(blacklist);
		this.isDebug = isDebug;
	}

	public MechMarketChecker(List<String> blacklist){
		this();
		this.blacklist.addAll(blacklist);
	}

	@Override
	public boolean checkPotential(String title, String description, SearchItem searchItem){
		boolean hasPotential = false;

		String[] splitTitle = title.split("\\[\\s*\\w+\\s*\\]", 3); // country or gb/ic info will be first index if applicable

		if(splitTitle.length == 3){
			//String location = splitTitle[0].toLowerCase().trim();
			String titleHave = splitTitle[1].toLowerCase().trim();
			String titleWant = splitTitle[2].toLowerCase().trim();

			if(WatchdogUtils.wantsMoney(titleWant)){
				description = description.toLowerCase().trim();

				// Remove all instances of whatever is in the excluded words list for each search term
				if(searchItem.excludedTerms != null && !searchItem.excludedTerms.isEmpty()){
					for(String excludeString : searchItem.excludedTerms) {
						description = description.replace(excludeString.toLowerCase().trim(), "");
						titleHave = titleHave.replace(excludeString.toLowerCase().trim(), "");
					}
				}

				// Look for the search terms in title and description
				String searchTerm = searchItem.searchTerm.toLowerCase().trim();
				if ((titleHave.contains(searchTerm) || description.contains(searchTerm)) && !titleWant.contains(searchTerm)) {
					hasPotential = true;
				}
			}
		} else {
			logger.error("Error when trying to parse title {}", title);
		}
		
		return hasPotential;
	}
	
	public boolean isListing(String title){
		Matcher haveMatcher = havePattern.matcher(title);
		Matcher wantMatcher = wantPattern.matcher(title);

		return haveMatcher.find() && wantMatcher.find();
	}

	public String getListingType(String title){		
		String postType = "";

		Matcher otherMatcher = otherPattern.matcher(title);

		if(otherMatcher.find()) {
			String type = otherMatcher.group();
			type = type.substring(1, type.length()-1);
			switch(type){
				case "GB":
				case "IC":
				case "Vendor":
					postType = type;
					break;
			}		
		}

		return postType;
	}

	@Override
	public List<SiteData> getPotentialPosts(List<SiteData> newPosts, List<SearchItem> searchItems) {
		List<SiteData> potentialPosts = new ArrayList<>();//mechmarketChecker.getPotentialPosts(newItems, this.searchItems);

		for(SiteData siteData : newPosts){
			MechmarketPost post = new MechmarketPost((Post) siteData);

			if(!isBlacklisted(post.data.author)){
				if(isListing(post.data.title)){
					post.markAsListing();
					for(SearchItem searchItem : searchItems){
						if(checkPotential(post.data.title, post.data.selftext, searchItem) || isDebug){
							post.addTerm(searchItem.searchTerm);
						}
					}
					if(post.hasMatch()){
						potentialPosts.add(post);
					}
				} else {
					String postType = getListingType(post.data.title);
					if(!postType.isEmpty()){
						post.addTerm(postType);
						potentialPosts.add(post);
					}
				}
			}
		}

		return potentialPosts;
	}

	private boolean isBlacklisted(String author){
		for (String s : blacklist){
			if(s.toUpperCase().equals(author.toUpperCase())){
				return true;
			}
		}
		return false;
	}
}