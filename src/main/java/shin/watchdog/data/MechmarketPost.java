package shin.watchdog.data;

import java.util.ArrayList;
import java.util.List;

public class MechmarketPost extends Post {
    String postType;
    List<String> matchedTerms;
    boolean isListing;

    public MechmarketPost(Post post){
        this.data = post.data;
        postType = "";
        matchedTerms = new ArrayList<>();
    }

    public void addTerm(String matchedTerm){
        this.matchedTerms.add(matchedTerm);
    }

    public boolean isListing(){
        return this.isListing;
    }

    public List<String> getTerms(){
        return this.matchedTerms;
    }

    public void markAsListing(){
        this.postType = "LISTING";
        this.isListing = true;
    }

    public void setPostType(String type){
        this.postType = type;
    }

    public String getPostType(){
        return this.postType;
    }

    public boolean hasMatch(){
        return !this.matchedTerms.isEmpty();
    }

    public List<String> getMatches(){
        return this.matchedTerms;
    }
}