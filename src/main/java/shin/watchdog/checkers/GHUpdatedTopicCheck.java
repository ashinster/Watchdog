package shin.watchdog.checkers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.data.AlertTopic;
import shin.watchdog.data.Entry;
import shin.watchdog.interfaces.Checker;

public class GHUpdatedTopicCheck extends Checker{
	final static Logger logger = LoggerFactory.getLogger(GHUpdatedTopicCheck.class);
	
	public GHUpdatedTopicCheck(Map<String, AlertTopic> alertTopics){
		super(alertTopics);
	}

	@Override
	public boolean check(Entry entry) {
		boolean doAlert = false;

		String author = entry.getAuthor().getName().trim().toLowerCase();
		String entryId = 
			entry.getId()
				.substring("https://geekhack.org/index.php?topic=".length()).trim()
				.split(".msg")[0];

		// Check if author's name is in the list of authors we're interested in
		if(alertTopics.containsKey(author)){
			AlertTopic alertTopic = alertTopics.get(author);

			// Check if comment by the author is created in the thread we're interested in
			if(entryId.equals(alertTopic.getTopic())){
				// Alert the role
				doAlert = true;
			}
		}

		return doAlert;
	}

}