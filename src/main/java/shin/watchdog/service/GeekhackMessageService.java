package shin.watchdog.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.Alert;

@Service
public class GeekhackMessageService{

    final static Logger logger = LoggerFactory.getLogger(GeekhackMessageService.class);

    @Value("${isDebug}")
    private boolean isDebug;

	private SimpleDateFormat sdfLocal;

	public GeekhackMessageService(){
		this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
    }

    public boolean sendMessage(List<Alert> alerts, String webhookUrl){
        return sendMessage(alerts, webhookUrl, "");
    }

    public boolean sendMessage(List<Alert> alerts, String webhookUrl, String mainRecipient){
        StringBuilder message = new StringBuilder();

        if(!mainRecipient.isEmpty()){
            if(isDebug){
                mainRecipient = "@FakeRole";
            }
            message.append(mainRecipient).append("\n\n");
        }

        for(Alert alert : alerts){

            if(alert.getRecipient() != null){
                message.append(alert.getRecipient()).append("\n");
            }

            String localDate = sdfLocal.format(Instant.parse(alert.getPublished()).toEpochMilli());

            String prettyTitle = StringEscapeUtils.unescapeHtml4(alert.getTitle());

            message.append("**\"" + prettyTitle.trim() + "\" by " + alert.getAuthor() + "**").append("\n");
            message.append("*Posted on " + localDate + "*").append("\n");
            message.append(alert.getId()).append("\n\n");    
            
            /** Sample:
             * @Interest Checks
             * 
             * @Jane
             * "[GB] TGR Jane" by yuktsi 
             * Posted on 8:00PM EST
             **/
        }

        if(!isDebug){
            return SendPrivateMessage.sendPM("", message.toString(), webhookUrl);
        } else {
            // Send to debug channel instead
            webhookUrl = "https://discordapp.com/api/webhooks/477287919103639555/7LBWCz1DrYdMN0VHhv1hxpREIYhxniH0VKV0ZQ-abgZlZmxQfWsJ-Ec_KQCOJqB9Wn1L";
            return SendPrivateMessage.sendPM("", message.toString(), webhookUrl);
        }
    }
}