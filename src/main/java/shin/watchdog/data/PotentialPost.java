package shin.watchdog.data;

import java.util.ArrayList;
import java.util.List;

public class PotentialPost{

    private List<String> matchedTerms;
    private Post post;

	public PotentialPost(Post post) {
        this.post = post;    
        this.matchedTerms = new ArrayList<>();
    }
    
    public void addMatchedTerm(String matchedTerm){
        this.matchedTerms.add(matchedTerm);
    }

    public List<String> getMatchedTerms(){
        return this.matchedTerms;
    }

    public String getAuthor(){
        return this.post.data.author;
    }

    public String getPostId(){
        return this.post.data.id;
    }

    public String getDescription(){
        return this.post.data.selftext;
    }

    public String getTitle(){
        return this.post.data.title;
    }

    public String getUrl(){
        return this.post.data.url;
    }

    public void setMatchedTerms(List<String> matchedTerms){
        this.matchedTerms = matchedTerms;
    }
}