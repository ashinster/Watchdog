package shin.watchdog.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.data.MechmarketPost;
import shin.watchdog.data.Post;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.WatchdogUser;

public class MechMarketChecker{
	final static Logger logger = LoggerFactory.getLogger(MechMarketChecker.class);

	boolean isDebug;

	// For title validating
	Pattern havePattern;
	Pattern wantPattern;
	Pattern otherPattern;

	public MechMarketChecker(){
		havePattern = Pattern.compile("\\[\\s*[Hh]\\s*\\]");
		wantPattern = Pattern.compile("\\[\\s*[Ww]\\s*\\]");
		otherPattern = Pattern.compile("\\[\\s*\\w+\\s*\\]");
	}

	public MechMarketChecker(boolean isDebug){
		this();
		this.isDebug = isDebug;
	}
	
	public List<MechmarketPost> getPotentialPosts(List<Post> newPosts, WatchdogUser user) {
		List<MechmarketPost> potentialPosts = new ArrayList<>();//mechmarketChecker.getPotentialPosts(newItems, this.searchItems);

		for(Post post : newPosts){
			MechmarketPost mmPost = new MechmarketPost(post);

			if(!user.getIgnore().contains(mmPost.data.author.toLowerCase())){

				if(user.getSpecial().contains(mmPost.data.author.toLowerCase())) {
					mmPost.addTerm(post.data.author);
				} 
				else if(isListing(post.data.title)){
					mmPost.markAsListing();
					for(SearchItem searchItem : user.getSearch()){
						if(checkPotential(post.data.title, post.data.selftext, searchItem) || isDebug){
							mmPost.addTerm(searchItem.getTerm());
						}
					}
				} 
				else {
					String postType = getListingType(mmPost.data.title);
					if(user.getListingtypes().contains(postType)){
						mmPost.addTerm(postType);
					}
				}

				
				if(mmPost.hasMatch()){
					logger.info("New {} post found: \"{}\" by {}", Arrays.toString(mmPost.getMatches().toArray()), post.data.title, post.data.author);
					potentialPosts.add(mmPost);
				}
			}
		}

		return potentialPosts;
	}

	public boolean checkPotential(String title, String description, SearchItem searchItem){
		boolean hasPotential = false;

		String[] splitTitle = title.split("\\[\\s*\\w+\\s*\\]", 3); // country or gb/ic info will be first index if applicable

		if(splitTitle.length == 3){
			//String location = splitTitle[0].toLowerCase().trim();
			String titleHave = splitTitle[1].toLowerCase().trim();
			String titleWant = splitTitle[2].toLowerCase().trim();

			if(wantsMoney(titleWant)){
				description = description.toLowerCase().trim();

				// Remove all instances of whatever is in the excluded words list for each search term
				for(String excludeString : searchItem.getExclude()) {
					description = description.replace(excludeString.toLowerCase().trim(), "");
					titleHave = titleHave.replace(excludeString.toLowerCase().trim(), "");
				}
				// Look for the search terms in title and description
				String searchTerm = searchItem.getTerm().toLowerCase().trim();
				if ((titleHave.contains(searchTerm) || description.contains(searchTerm)) && !titleWant.contains(searchTerm)) {
					hasPotential = true;
				}
			}
		} else {
			logger.error("Invalid title: {}", title);
		}
		
		return hasPotential;
	}
	
    public boolean wantsMoney(String text) {
        boolean isWantsMoney = false;
		if (text.contains("paypal") || text.contains("pp") || text.contains("venmo")
				|| text.contains("cash") || text.contains("money") || text.contains("google")
				|| text.contains("money") || text.contains("$")) {
			isWantsMoney = true;
		}
		return isWantsMoney;
    }

	public boolean isListing(String title){
		Matcher haveMatcher = this.havePattern.matcher(title);
		Matcher wantMatcher = this.wantPattern.matcher(title);

		return haveMatcher.find() && wantMatcher.find();
	}

	public String getListingType(String title){		
		String postType = "";

		Matcher otherMatcher = this.otherPattern.matcher(title);

		if(otherMatcher.find()) {
			String type = otherMatcher.group();
			postType = type.substring(1, type.length()-1);
		}

		return postType;
	}
}