package shin.watchdog.data;

import java.util.ArrayList;

public class SearchItem{
    String searchTerm;
    ArrayList<String> excludedTerms;

    public SearchItem(String searchTerm, ArrayList<String> excludedTerms){
        this.searchTerm = searchTerm;
        this.excludedTerms = excludedTerms;
    }

    public String getSearchTerm(){
        return searchTerm;
    }

    public ArrayList<String> getExcludedTerms(){
        return excludedTerms;
    }
}