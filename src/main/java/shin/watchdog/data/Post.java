package shin.watchdog.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import shin.watchdog.interfaces.SiteData;

public class Post implements SiteData{

    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("selftext")
        @Expose
        public String selftext;

        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("author")
        @Expose
        public String author;

        @SerializedName("url")
        @Expose
        public String url;

        @SerializedName("title")
        @Expose
        public String title;

        @SerializedName("created_utc")
        @Expose
        public long createdUtc;
    }

}