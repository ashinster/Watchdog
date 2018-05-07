package shin.watchdog.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import shin.watchdog.scheduled.FetchPostRunnable;

public class TestPotentialLogic {
    JsonObject postData;
    FetchPostRunnable fpr;

    @Before
    public void setup(){
        postData = new JsonObject();

        Map<String, ArrayList<String>> searchItems = new HashMap<>();
		
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

		searchItems.put("Novatouch", novatouchExclude);
		searchItems.put("Dolch Pac", dolchPacExclude);
		searchItems.put("Panda", holyPandasExclude);
        
		searchItems.put("novatouch", new ArrayList<>());
        fpr = new FetchPostRunnable(searchItems, true);
    }

    @Test
    public void testCheckPotential1(){
        String postTitle = "[US-MD] [H] novatouch, dolch pac, invyr panda [W] paypal";
        String postDesc = "";

        Assert.assertTrue(fpr.checkPotential(postTitle, postDesc));     
    }

    @Test
    public void testCheckPotential2(){
        String postTitle = "[US-MD] [H] stuff [W] paypal";
        String postDesc = "invyr panda";

        Assert.assertTrue(fpr.checkPotential(postTitle, postDesc));     
    }

    @Test
    public void testCheckPotential3(){
        String postTitle = "[US-MD] [H] stuff [W] paypal";
        String postDesc = "trash panda dolch";

        Assert.assertFalse(fpr.checkPotential(postTitle, postDesc));     
    }

    @Test
    public void testCheckPotential4(){
        String postTitle = "[US-MD] [H] stuff [W] dolch pac";
        String postDesc = "novatouch";

        Assert.assertFalse(fpr.checkPotential(postTitle, postDesc));     
    }
}