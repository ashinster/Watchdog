package shin.watchdog.data;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shin.watchdog.utils.WatchdogUtils;

public class SearchItem{
    public String searchTerm;
    public ArrayList<String> excludedTerms;

    public SearchItem(){

    }

    public SearchItem(String searchTerm, ArrayList<String> excludedTerms){
        this.searchTerm = searchTerm;
        this.excludedTerms = excludedTerms;
    }

}