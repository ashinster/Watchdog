package shin.watchdog.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.data.Board;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Site;
import shin.watchdog.data.Subreddit;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.scheduled.Processor;

public class TestPotentialLogic {
    Subreddit sub;
    
    ArrayList<SearchItem> searchItems;

    ArrayList<String> holyPandasExclude;
    ArrayList<String> novatouchExclude;
    ArrayList<String> dolchPacExclude;

    SearchItem nt; 
    SearchItem dp;
    SearchItem panda;

    MechMarketChecker redditChecker = new MechMarketChecker();

    List<Site> sites;

    @Before
    public void setup(){

        searchItems = new ArrayList<>();
		
		// parameterize this later from props file maybe?
		novatouchExclude = new ArrayList<>();
		novatouchExclude.add("novatouch slider");
		novatouchExclude.add("novatouch stems");

		dolchPacExclude = new ArrayList<>();
		dolchPacExclude.add("dolch pac keys");
		dolchPacExclude.add("dolch pac key set");
		dolchPacExclude.add("dolch pac keyset");
		dolchPacExclude.add("dolch pac keycap");
		dolchPacExclude.add("dolch pac key cap");
		dolchPacExclude.add("dolch pac cap");
		dolchPacExclude.add("dolch pac set");

		holyPandasExclude = new ArrayList<>();
		holyPandasExclude.add("trash panda");
		holyPandasExclude.add("panda stem");
        holyPandasExclude.add("gaf panda");
        
        nt = new SearchItem("Novatouch", novatouchExclude);
        dp = new SearchItem("Dolch Pac", dolchPacExclude);
        panda = new SearchItem("Panda", holyPandasExclude);
        SearchItem si = new SearchItem("paypal", null);

		searchItems.add(nt);
		searchItems.add(dp);
        searchItems.add(panda);
        searchItems.add(si);
    }

    @After
    public void tearDown(){

    }

    @Test
    public void testCheckPotential1(){
        String postTitle = "[US-MD] [H] novatouch, dolch pac, invyr panda [W] paypal";
        String postDesc = "";

        Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, nt));     

        Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, dp));

        Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, panda));     
    }

    @Test
    public void testCheckPotential2(){
        String postTitle = "[US-MD] [H] stuff [W] paypal";
        String postDesc = "invyr panda";

        Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, panda));     
    }

    @Test
    public void testCheckPotential3(){
        String postTitle = "[US-MD] [H] stuff [W] paypal";
        String postDesc = "trash panda dolch";
        
        Assert.assertFalse(redditChecker.checkPotential(postTitle, postDesc, nt));     
    }

    @Test
    public void testCheckPotential4(){
        String postTitle = "[US-MD] [H] stuff [W] dolch pac";
        String postDesc = "novatouch";

        boolean match = false;

        for(SearchItem searchItem : searchItems){
            if(redditChecker.checkPotential(postTitle, postDesc, searchItem)){
                match = true;
            }
        }

        Assert.assertFalse(match);     
    }

    @Test
    public void testCheckPotential5(){
        String postTitle = "[US-MD] [H] stuff [W] pp";
        String postDesc = "novatouch";

        boolean match = false;

        for(SearchItem searchItem : searchItems){
            if(redditChecker.checkPotential(postTitle, postDesc, searchItem)){
                match = true;
            }
        }



        Assert.assertTrue(match);    
    }

    @Test
    public void testCheckPotential6(){
        sites = new ArrayList<>();

		Map<String, List<SearchItem>> searchItemsForUsers = new HashMap<>();
		searchItemsForUsers.put("timidsa", searchItems);
        
		Site mechmarket = new Subreddit("t5_2vgng", "MechMarket", searchItemsForUsers, new MechMarketChecker(true), true);
		Site geekhackIc = new Board("132", "Interest Checks", Arrays.asList("timidsa"), true);
		Site geekhackGb = new Board("70", "Group Buys", Arrays.asList("timidsa"), true);
		
		sites.add(mechmarket);
		sites.add(geekhackIc);
		sites.add(geekhackGb);

		for(Site site : sites){
			new Processor(site).run();
		}
    }

    
    @Test
    public void testCheckPotential7(){
        String postTitle = "[gb] asdkjfhaslfhjdasd asdsd";
        
        Assert.assertFalse(redditChecker.isListing(postTitle));    
    }
}