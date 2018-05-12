package shin.watchdog.data;

import java.util.List;

public class SearchItem{
    public String searchTerm;
    public List<String> excludedTerms;
    
    public SearchItem(String searchTerm, List<String> excludedTerms){
        this.searchTerm = searchTerm;
        this.excludedTerms = excludedTerms;
    }

}