package shin.watchdog.checkers;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.data.AlertTopic;
import shin.watchdog.data.atom.Entry;
import shin.watchdog.interfaces.Checker;

public class GHNewTopicCheck extends Checker{
    final static Logger logger = LoggerFactory.getLogger(GHNewTopicCheck.class);

	@PostConstruct
	private void postConstruct(){
		this.alertTopics = config.getNewTopics();
	}

    @Override
	public boolean check(Entry thread) {
		boolean doAlert = false;

        String title = thread.getTitle().trim().toLowerCase();
        String summary = thread.getSummary().getValue().trim().toLowerCase();
        String author = thread.getAuthor().getName().trim().toLowerCase();
        
        // Check if author's name is in the list of authors we're interested in
		if(alertTopics.containsKey(author)){
			AlertTopic alertTopic = alertTopics.get(author);

			// Check if the new thread contains the topic/subject we're interested in
			if(title.contains(alertTopic.getTopic()) || summary.contains(alertTopic.getTopic())){
				doAlert = true;
			}
		}

        return doAlert;
	}
}