package shin.watchdog.scheduled;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import shin.watchdog.data.PotentialPost;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Site;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;

public class Processor implements Runnable {

    private Site site;

    public Processor(Site site){
        this.site = site;
    }

	@Override
	public void run() {
		try{
            List<SiteData> potentialPosts = doSearch(site);

            if(!potentialPosts.isEmpty()){
                site.sendMessage(potentialPosts);
            }
            
        } catch (Throwable t) {
			t.printStackTrace();
		}
    }

	private List<SiteData> doSearch(Site site) {
        List<SiteData> potentialPosts = new ArrayList<>();

        // Get all new posts
        LinkedHashMap<String, SiteData> newItems = site.makeCall();

        if(!newItems.isEmpty()){
            PotentialChecker checker = site.getChecker();
            if(checker != null){
                for(SiteData post : newItems.values()){
                    PotentialPost matchedPost = new PotentialPost(post);
    
                    checker.setPost(post);

                    for(SearchItem searchItem : site.getSearchItems()){
                        checker.setSearch(searchItem);
                        if(checker.check()){
                            matchedPost.addMatchedTerm(searchItem.searchTerm);
                        }
                    }

                    if(!matchedPost.getMatchedTerms().isEmpty()){
                        potentialPosts.add(matchedPost);
                    }
                }
            } else {
                potentialPosts = new ArrayList<>(newItems.values());
            }
        }
        
        return potentialPosts;
	}

}