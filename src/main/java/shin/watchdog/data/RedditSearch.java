package shin.watchdog.data;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RedditSearch {

    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("children")
        @Expose
        public ArrayList<Post> children;
    }

}