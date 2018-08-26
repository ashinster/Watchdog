package shin.watchdog.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.data.SearchItem;
import shin.watchdog.site.Site;

public class TestPotentialLogic {
    
    // ArrayList<SearchItem> searchItems;

    // ArrayList<String> holyPandasExclude;
    // ArrayList<String> novatouchExclude;
    // ArrayList<String> dolchPacExclude;

    // SearchItem nt; 
    // SearchItem dp;
    // SearchItem panda;

    // MechMarketChecker redditChecker = new MechMarketChecker();

    // List<Site> sites;

    // @Before
    // public void setup(){

    //     searchItems = new ArrayList<>();
		
	// 	// parameterize this later from props file maybe?
	// 	novatouchExclude = new ArrayList<>();
	// 	novatouchExclude.add("novatouch slider");
	// 	novatouchExclude.add("novatouch stems");

	// 	dolchPacExclude = new ArrayList<>();
	// 	dolchPacExclude.add("dolch pac keys");
	// 	dolchPacExclude.add("dolch pac key set");
	// 	dolchPacExclude.add("dolch pac keyset");
	// 	dolchPacExclude.add("dolch pac keycap");
	// 	dolchPacExclude.add("dolch pac key cap");
	// 	dolchPacExclude.add("dolch pac cap");
	// 	dolchPacExclude.add("dolch pac set");

	// 	holyPandasExclude = new ArrayList<>();
	// 	holyPandasExclude.add("trash panda");
	// 	holyPandasExclude.add("panda stem");
    //     holyPandasExclude.add("gaf panda");
        
    //     nt = new SearchItem();
    //     nt.setTerm("Novatouch");
    //     nt.setExclude(novatouchExclude);

    //     dp = new SearchItem();
    //     dp.setTerm("Dolch Pac");
    //     dp.setExclude(dolchPacExclude);

    //     panda = new SearchItem();
    //     panda.setTerm("Panda");

    //     panda.setExclude(holyPandasExclude);
    //     SearchItem si = new SearchItem();
    //     si.setTerm("paypal");
    //     si.setExclude(new ArrayList<>());

	// 	searchItems.add(nt);
	// 	searchItems.add(dp);
    //     searchItems.add(panda);
    //     searchItems.add(si);
    // }

    // @Test
    // public void testCheckPotential1(){
    //     String postTitle = "[US-MD] [H] novatouch, dolch pac, invyr panda [W] paypal";
    //     String postDesc = "";

    //     Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, nt));     

    //     Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, dp));

    //     Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, panda));     
    // }

    // @Test
    // public void testCheckPotential2(){
    //     String postTitle = "[US-MD] [H] stuff [W] paypal";
    //     String postDesc = "invyr panda";

    //     Assert.assertTrue(redditChecker.checkPotential(postTitle, postDesc, panda));     
    // }

    // @Test
    // public void testCheckPotential3(){
    //     String postTitle = "[US-MD] [H] stuff [W] paypal";
    //     String postDesc = "trash panda dolch";
        
    //     Assert.assertFalse(redditChecker.checkPotential(postTitle, postDesc, nt));     
    // }

    // @Test
    // public void testCheckPotential4(){
    //     String postTitle = "[US-MD] [H] stuff [W] dolch pac";
    //     String postDesc = "novatouch";

    //     boolean match = false;

    //     for(SearchItem searchItem : searchItems){
    //         if(redditChecker.checkPotential(postTitle, postDesc, searchItem)){
    //             match = true;
    //         }
    //     }

    //     Assert.assertFalse(match);     
    // }

    // @Test
    // public void testCheckPotential5(){
    //     String postTitle = "[US-MD] [H] stuff [W] pp";
    //     String postDesc = "novatouch";

    //     boolean match = false;

    //     for(SearchItem searchItem : searchItems){
    //         if(redditChecker.checkPotential(postTitle, postDesc, searchItem)){
    //             match = true;
    //         }
    //     }

    //     Assert.assertTrue(match);    
    // }
    
    // @Test
    // public void testCheckPotential7(){
    //     String postTitle = "[gb] asdkjfhaslfhjdasd asdsd";
        
    //     Assert.assertFalse(redditChecker.isListing(postTitle));    
    // }

    // @Test
    // public void pandaTest(){
    //     String postSelftext = "I hate to do this but I'll need to try and capitalize on the meme that are pandas spent too much money on stuff need to try and recoup.  Don't buy unless you hate money.\n\n83 Invr Pandas - want $228 +ship\n\n120 Hako Tures - want $55\n120 Hako Clears- want $55\n\n49 Silent blaacks- want $19+ship\n\n95 Zealios- bought this from someone on mm a while ago.  I used the housings from the others to make silent blacks. Bag says they are 65g (I am not totally sure) some have been desoldered but seem in good condition - want $47\n\nMaxkey wob- want $70\n\nGMK 9009 Included is a GMK 9009 Round 2 keyset, consisiting of base, spacebars and add-on kits (mounted one time no shine) - want $235+ship shipped in bag (carefully)\n\n[timestamp](https://i.imgur.com/T8EjMiG.jpg)\n";
    //     String postTitle = "[US-IL] [H] 83 Invr Pandas, Hako trues/clears, Silent blacks, Zeals, Maxkey wob, GMK 9009 [W] PayPal";
        
	// 	ArrayList<String> holyPandasExclude = new ArrayList<>();
	// 	holyPandasExclude.add("trash panda");
	// 	holyPandasExclude.add("panda stem");
    //     holyPandasExclude.add("gaf panda");

    //     SearchItem panda = new SearchItem();
    //     panda.setTerm("Panda");
    //     panda.setExclude(holyPandasExclude);

    //     Assert.assertTrue(redditChecker.checkPotential(postTitle, postSelftext, panda));    
    // }
}