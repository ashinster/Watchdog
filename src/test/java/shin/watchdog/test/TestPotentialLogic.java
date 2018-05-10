package shin.watchdog.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Subreddit;
import shin.watchdog.scheduled.FetchPostRunnable;

public class TestPotentialLogic {
    FetchPostRunnable fpr;
    Subreddit sub;
    
    ArrayList<SearchItem> searchItems;

    ArrayList<String> holyPandasExclude;
    ArrayList<String> novatouchExclude;
    ArrayList<String> dolchPacExclude;

    SearchItem nt; 
    SearchItem dp;
    SearchItem panda;

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

		searchItems.add(nt);
		searchItems.add(dp);
        searchItems.add(panda);
        
        fpr = new FetchPostRunnable(searchItems, true);

        Map<String, SearchItem> searchMap = new HashMap<>();
		for(SearchItem sI : searchItems){
			searchMap.put(sI.searchTerm, sI);
		}

		sub = new Subreddit("t5_2vgng", "MechMarket", searchMap, new MechMarketChecker());
    }

    @After
    public void tearDown(){
        fpr = new FetchPostRunnable(searchItems, true);
    }

    @Test
    public void testCheckPotential1(){
        String postTitle = "[US-MD] [H] novatouch, dolch pac, invyr panda [W] paypal";
        String postDesc = "";

        
        sub.setSearchItem(nt.searchTerm);
        Assert.assertTrue(sub.checkPotential(postTitle, postDesc));     

        sub.setSearchItem(dp.searchTerm);
        Assert.assertTrue(sub.checkPotential(postTitle, postDesc));     

        sub.setSearchItem(panda.searchTerm);
        Assert.assertTrue(sub.checkPotential(postTitle, postDesc));     
    }

    @Test
    public void testCheckPotential2(){
        String postTitle = "[US-MD] [H] stuff [W] paypal";
        String postDesc = "invyr panda";

        
        sub.setSearchItem(panda.searchTerm);
        Assert.assertTrue(sub.checkPotential(postTitle, postDesc));     
    }

    @Test
    public void testCheckPotential3(){
        String postTitle = "[US-MD] [H] stuff [W] paypal";
        String postDesc = "trash panda dolch";
        
        sub.setSearchItem(nt.searchTerm);
        Assert.assertFalse(sub.checkPotential(postTitle, postDesc));     
    }

    @Test
    public void testCheckPotential4(){
        String postTitle = "[US-MD] [H] stuff [W] dolch pac";
        String postDesc = "novatouch";

        boolean match = false;

        for(SearchItem searchItem : searchItems){
            sub.setSearchItem(searchItem.searchTerm);
            if(sub.checkPotential(postTitle, postDesc)){
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
            sub.setSearchItem(searchItem.searchTerm);
            if(sub.checkPotential(postTitle, postDesc)){
                match = true;
            }
        }

        Assert.assertTrue(match);    
    }

    @Test
    public void testCheckPotential6(){
        fpr.run();
    }
}