package shin.watchdog.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class WatchdogUtils {

    public static boolean wantsMoney(String text) {
        boolean isWantsMoney = false;
		if (text.contains("paypal") || text.contains("pp") || text.contains("venmo")
				|| text.contains("cash") || text.contains("money") || text.contains("google")
				|| text.contains("money") || text.contains("$")) {
			isWantsMoney = true;
		}
		return isWantsMoney;
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

        for(String match : matchedTerms){
            String pmBody = "Hey! I'll buy your " + match + " if it's still available! Let me know, thanks!";
            String params = "";
            try {
                params = String.format("to=%s&subject=%s&message=%s", URLEncoder.encode(author, "UTF-8"), URLEncoder.encode(match, "UTF-8"), URLEncoder.encode(pmBody, "UTF-8"));			
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            String url = "https://www.reddit.com/message/compose?" + params;

            link.append("[Send PM for the " + match + "](" + url + ")\n\n");
        }

        if(matchedTerms.size() > 1){
            String multipleMatches = commaSeparateList(matchedTerms); 

            String pmBody = "Hey! I'll buy your " + multipleMatches + " if they're still available! Let me know, thanks!";
            String params = String.format("to=%s&subject=%s&message=%s", author, multipleMatches, pmBody);
			try {
				params = URLEncoder.encode(params, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            String url = "https://www.reddit.com/message/compose?" + params;

            link.append("[Send PM for All Items](" + url + ")");
        }

        return link.toString();
    }
}