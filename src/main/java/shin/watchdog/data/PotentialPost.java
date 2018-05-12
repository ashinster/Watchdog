package shin.watchdog.data;

import java.util.ArrayList;
import java.util.List;

import shin.watchdog.interfaces.SiteData;

public class PotentialPost implements SiteData{

    private List<String> matchedTerms;
    private SiteData post;

	public PotentialPost(SiteData post) {
        this.post = post;    
        this.matchedTerms = new ArrayList<>();
    }
    
    public void addMatchedTerm(String matchedTerm){
        this.matchedTerms.add(matchedTerm);
    }

    public List<String> getMatchedTerms(){
        return this.matchedTerms;
    }

    public void setMatchedTerms(List<String> matchedTerms){
        this.matchedTerms = matchedTerms;
    }

    public SiteData getSiteData(){
        return post;
    }    
}