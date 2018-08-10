package shin.watchdog.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("data")
    public Data data;

    public class Data {

        @SerializedName("link_flair_text")
        @Expose
        public String link_flair_text;

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

        @SerializedName("crosspost_parent_list")
        @Expose
        public Crossposts[] crosspost_parent_list;

        public class Crossposts{
            @SerializedName("selftext")
            @Expose
            public String selftext;
        }
    }
}