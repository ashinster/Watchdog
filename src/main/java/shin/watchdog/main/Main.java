package shin.watchdog.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.data.Board;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Site;
import shin.watchdog.data.Subreddit;
import shin.watchdog.scheduled.PostProcessorTask;

public class Main {

	public static void main (String[] args) throws UnsupportedEncodingException {	

		ArrayList<SearchItem> searchItems = new ArrayList<>();
		
		// parameterize this later from props file maybe?
		ArrayList<String> novatouchExclude = new ArrayList<>();
		novatouchExclude.add("novatouch slider");
		novatouchExclude.add("novatouch stems");

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

		searchItems.add(new SearchItem("Novatouch", novatouchExclude));
		searchItems.add(new SearchItem("Dolch Pac", dolchPacExclude));
		searchItems.add(new SearchItem("Panda", holyPandasExclude));


		List<Site> sites = new ArrayList<>();
		Site mechmarket = new Subreddit("t5_2vgng", "MechMarket", searchItems, new MechMarketChecker(), 5);
		Site geekhackIc = new Board("132", "Interest Checks", 60);
		Site geekhackGb = new Board("70", "Group Buys", 60);
		
		sites.add(mechmarket);
		sites.add(geekhackIc);
		sites.add(geekhackGb);

		PostProcessorTask postProcessorTask = new PostProcessorTask();
		for(Site site : sites){
			System.out.println("Starting " + site.getName() + " Process");
			postProcessorTask.start(site);
		}
	}
}
