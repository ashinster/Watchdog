package shin.watchdog.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.data.Board;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Site;
import shin.watchdog.data.Subreddit;
import shin.watchdog.scheduled.PostProcessorTask;

public class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static RequestConfig config;

	public static HttpClient httpclient;

	public static void main (String[] args) throws UnsupportedEncodingException {	

		config = RequestConfig.custom()
		.setConnectTimeout(2 * 1000)
		.setConnectionRequestTimeout(2 * 1000)
		.setSocketTimeout(5 * 1000)
		.build();

		httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		
		// parameterize this later from props file maybe?
		ArrayList<String> novatouchExclude = new ArrayList<>();
		novatouchExclude.add("novatouch slider");
		novatouchExclude.add("novatouch stem");

		ArrayList<String> dolchPacExclude = new ArrayList<>();
		dolchPacExclude.add("dolch pac keys");
		dolchPacExclude.add("dolch pac key set");
		dolchPacExclude.add("dolch pac keyset");
		dolchPacExclude.add("dolch pac keycap");
		dolchPacExclude.add("dolch pac key cap");
		dolchPacExclude.add("dolch pac cap");
		dolchPacExclude.add("dolch pac set");

		ArrayList<String> holyPandasExclude = new ArrayList<>();
		holyPandasExclude.add("trash panda");
		holyPandasExclude.add("panda stem");
		holyPandasExclude.add("gaf panda");
		holyPandasExclude.add("stash panda");
		holyPandasExclude.add("hash panda");

		ArrayList<SearchItem> searchItems = new ArrayList<>();
		searchItems.add(new SearchItem("Novatouch", novatouchExclude));
		searchItems.add(new SearchItem("Dolch Pac", dolchPacExclude));
		searchItems.add(new SearchItem("Panda", holyPandasExclude));


		Map<String, List<SearchItem>> searchItemsForUsers = new HashMap<>();
		searchItemsForUsers.put("timidsa", searchItems);
		// add more here

		List<Site> sites = new ArrayList<>();
		Site mechmarket = new Subreddit("t5_2vgng", "MechMarket", searchItemsForUsers, new MechMarketChecker(Arrays.asList("EZEALLINH")), 5);
		Site geekhackIc = new Board("132", "Interest Checks", Arrays.asList("timidsa", "H3NT4I"), 60);
		Site geekhackGb = new Board("70", "Group Buys", Arrays.asList("timidsa", "H3NT4I"), 60);
		
		sites.add(mechmarket);
		sites.add(geekhackIc);
		sites.add(geekhackGb);

		PostProcessorTask postProcessorTask = new PostProcessorTask();
		for(Site site : sites){
			logger.info("Starting {} Process", site.getName());
			postProcessorTask.start(site);
		}
	}
}
