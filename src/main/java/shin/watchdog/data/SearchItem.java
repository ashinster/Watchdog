package shin.watchdog.data;

import java.util.ArrayList;
import java.util.List;

public class SearchItem{

    private String term;

    private List<String> exclude = new ArrayList<>();

    /**
     * @param term the term to set
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * @param exclude the exclude to set
     */
    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }


    /**
     * @return the exclude
     */
    public List<String> getExclude() {
        return exclude;
    }

}