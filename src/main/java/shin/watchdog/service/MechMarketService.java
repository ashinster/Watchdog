package shin.watchdog.service;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import shin.watchdog.Watchdog;

/**
 * MechMarketService
 */
@Service
public class MechMarketService {
    final static Logger logger = LoggerFactory.getLogger(MechMarketService.class);

    private JsonParser jsonParser;

    public MechMarketService() {
        this.jsonParser = new JsonParser();
    }

    public JsonObject getMechMarketPosts() {

        JsonObject redditJson = null;

        String mmNewPostsUrl = "https://www.reddit.com/r/mechmarket/new.json?limit=10";

        HttpGet httpGet = new HttpGet(mmNewPostsUrl);

        httpGet.addHeader("User-Agent", "Bisoromi Watchdog");

        try(CloseableHttpResponse response = Watchdog.httpclient.execute(httpGet)) {

            String jsonString = EntityUtils.toString(response.getEntity());

            //logger.info(jsonString);

            redditJson = jsonParser.parse(jsonString).getAsJsonObject();

        } catch (IOException ioe) {
            logger.error("Error with reddit connection", ioe);
        }

        return redditJson;
    }

}