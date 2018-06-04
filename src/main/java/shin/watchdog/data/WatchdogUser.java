package shin.watchdog.data;

import java.util.ArrayList;
import java.util.List;

import shin.watchdog.utils.WatchdogUtils;

public class WatchdogUser{
    private String sendto;

    private List<String> ignore = new ArrayList<>();

    private List<String> special = new ArrayList<>();

    private List<SearchItem> search = new ArrayList<>();

    private List<String> listingtypes = new ArrayList<>();

    /**
     * @return the sendto
     */
    public String getSendto() {
        return sendto;
    }

    /**
     * @return the ignore
     */
    public List<String> getIgnore() {
        return ignore;
    }
    
    /**
     * @return the special
     */
    public List<String> getSpecial() {
        return special;
    }

    /**
     * @return the search
     */
    public List<SearchItem> getSearch() {
        return search;
    }

    /**
     * @return the listingtype
     */
    public List<String> getListingtypes() {
        return listingtypes;
    }


    /**
     * @param sendto the sendto to set
     */
    public void setSendto(String sendto) {
        this.sendto = sendto;
    }
    /**
     * @param ignore the ignore to set
     */
    public void setIgnore(List<String> ignore) {
        this.ignore = WatchdogUtils.lowercaseList(ignore);
    }    
    
    /**
    * @param special the special to set
    */
   public void setSpecial(List<String> special) {
       this.special = WatchdogUtils.lowercaseList(special);
   }

    /**
     * @param search the search to set
     */
    public void setSearch(List<SearchItem> search) {
        this.search = search;
    }

    /**
     * @param listingtype the listingtype to set
     */
    public void setListingtypes(List<String> listingtypes) {
        this.listingtypes = listingtypes;
    }

}