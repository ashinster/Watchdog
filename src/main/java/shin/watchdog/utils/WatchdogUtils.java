package shin.watchdog.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchdogUtils {

	final static Logger logger = LoggerFactory.getLogger(WatchdogUtils.class);	

    public static List<String> lowercaseList(List<String> list){
        List<String> lowercaseList = new ArrayList<>();
        for(String s : list){
            lowercaseList.add(s.toLowerCase());
        }
        return lowercaseList;
    }

    public static List<String> uppercaseList(List<String> list){
        List<String> uppercaseList = new ArrayList<>();
        for(String s : list){
            uppercaseList.add(s.toUpperCase());
        }
        return uppercaseList;
    }

    private static String commaSeparateList(List<String> list){
        
        StringBuilder termsList = new StringBuilder();

        if(list.size() > 1){

            if(list.size() == 2){
                termsList.append(list.get(0) + " and " + list.get(1));
            } else { 
                for(int i = 0; i < list.size(); i++){
                    if(i == list.size() - 1){
                        termsList.append("and ").append(list.get(i));
                    } else {
                        termsList.append(list.get(i)).append(", ");
                    }
                }
            }	
        }

        return termsList.toString();
    }

    public static String sendPmLink(List<String> matchedTerms, String author){
        
        StringBuilder link = new StringBuilder();

        try{
            for(String match : matchedTerms){
                String pmBody = "Hey! I'll buy your " + match + " if it's still available! Let me know, thanks!";
                String params = "";
                params = String.format("to=%s&subject=%s&message=%s", URLEncoder.encode(author, "UTF-8"), URLEncoder.encode(match, "UTF-8"), URLEncoder.encode(pmBody, "UTF-8"));			
                String url = "https://www.reddit.com/message/compose?" + params;
    
                link.append("[Send PM for the " + match + "](" + url + ")\n\n");
            }
    
            if(matchedTerms.size() > 1){
                String multipleMatches = commaSeparateList(matchedTerms); 
    
                String pmBody = "Hey! I'll buy your " + multipleMatches + " if they're still available! Let me know, thanks!";
                String params = "";
                params = String.format("to=%s&subject=%s&message=%s", URLEncoder.encode(author, "UTF-8"), URLEncoder.encode(multipleMatches, "UTF-8"), URLEncoder.encode(pmBody, "UTF-8"));
                String url = "https://www.reddit.com/message/compose?" + params;
    
                link.append("[Send PM for All Items](" + url + ")\n\n");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Unsupported encoding exception when encoding values", e);
		}

        return link.toString();
    }
}