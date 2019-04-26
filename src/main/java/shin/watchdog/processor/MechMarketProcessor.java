package shin.watchdog.processor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import shin.watchdog.service.MechMarketMessageService;
import shin.watchdog.service.MechMarketService;

/**
 * MechMarketProcessor
 */
public class MechMarketProcessor {
    final static Logger logger = LoggerFactory.getLogger(MechMarketProcessor.class);

    @Autowired
    private MechMarketService mmService;

    @Value("${isDebug:false}")
    protected boolean isDebug;

    @Autowired
    protected MechMarketMessageService mmMessageService;

    private long lastPubDate;

    public MechMarketProcessor() {
        this.lastPubDate = Instant.now().toEpochMilli()/1000;
    }

    public void process() {
        JsonObject redditJson = mmService.getMechMarketPosts();

        JsonArray children = redditJson.getAsJsonObject("data").getAsJsonArray("children");

        List<JsonObject> newPosts = new ArrayList<>();

        children.forEach(child -> {

            JsonObject elementObject = child.getAsJsonObject().getAsJsonObject("data");

            if(filter(elementObject)){
                newPosts.add(elementObject);
            }

        });

        if(!newPosts.isEmpty()) {
            this.lastPubDate = newPosts.get(0).get("created").getAsLong();
            sendAlert(newPosts);
        }
    }

    public boolean filter(JsonObject child) {
        boolean isNew = false;
        if(isDebug) {
            isNew = true;
        } else {
            if(child.get("created").getAsLong() > this.lastPubDate) {
                isNew = true;
            }
        }
        return isNew;
    }

    public void sendAlert(List<JsonObject> newPosts) {

        List<JsonObject> interestChecks = new ArrayList<>();
        List<JsonObject> groupBuys = new ArrayList<>();
        List<JsonObject> artisans = new ArrayList<>();

        for(JsonObject element : newPosts) {

            String title = element.get("title").toString();

            if (title.contains("[IC]")) {
                logger.info("New Mechmarket IC found: {}", element.get("title").toString());
                interestChecks.add(element);
            } else if (title.contains("[GB]")) {
                logger.info("New Mechmarket GB found: {}", element.get("title").toString());
                groupBuys.add(element);
            } else if (title.contains("[Artisan]")) {
                logger.info("New Mechmarket Artisan post found: {}", element.get("title").toString());
                artisans.add(element);
            }

        }

        if(!interestChecks.isEmpty()) {
            mmMessageService.sendMessage(
                interestChecks, 
                "https://discordapp.com/api/webhooks/571151483513339915/aPlFEOHiQZfuEyyZmOgY4U684q0xrJi09vfS84fj0vPwhSiDFXrApe_DF8zqbdN201kU", 
                "<@&477264441319096321>");
        }

        if(!groupBuys.isEmpty()) {
            mmMessageService.sendMessage(
                groupBuys, 
                "https://discordapp.com/api/webhooks/571153041332371466/Vq0uwmgIbt1gDni_bvtWj0ZTxAa2oSMqcGVwW6wwh648bUF2OYunUgORXUHFmQbSMrBR", 
                "<@&477264488983429130>");
        }

        if(!artisans.isEmpty()) {
            mmMessageService.sendMessage(
                artisans, 
                "https://discordapp.com/api/webhooks/571153145183469579/VdacLTuR0NbuLOH1MyqDHVfM5WdbT1rmaqFi5Tc_No0TAZOoOGsqMT7zmgokeyklgHX-", 
                "<@&571153468316844032>");
        }
    }
}