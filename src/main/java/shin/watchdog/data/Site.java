package shin.watchdog.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;

public abstract class Site {

    public static RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(5 * 1000)
        .setConnectionRequestTimeout(5 * 1000)
        .setSocketTimeout(5 * 1000)
        .build();

    public static HttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

    protected SimpleDateFormat sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
    protected SimpleDateFormat sdfGmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    protected String id;
    protected String name;
    protected PotentialChecker potentialChecker;
    protected List<String> cache;
    protected List<SearchItem> searchItems;
    protected long interval;
    
    public Site(String id, String name, List<SearchItem> searchItems, PotentialChecker potentialChecker, long interval){
        this.id = id;
        this.name = name;
        this.searchItems = searchItems;
        this.potentialChecker = potentialChecker;
        this.cache = new ArrayList<>();
        this.interval = interval;
    }

    public PotentialChecker getChecker(){
        return this.potentialChecker;
    }

    public void updateCache(List<String> newCache){
        this.cache = newCache;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public List<String> getCache(){
        return this.cache;
    }

    public List<SearchItem> getSearchItems(){
        return this.searchItems;
    }

    abstract public LinkedHashMap<String, SiteData> makeCall();

    abstract public void sendMessage(List<SiteData> potentialPosts);

    abstract public long getInterval();

}