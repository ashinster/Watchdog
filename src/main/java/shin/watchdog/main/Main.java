package shin.watchdog.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import shin.watchdog.data.SearchItem;
import shin.watchdog.scheduled.AccessTokenConsumer;
import shin.watchdog.scheduled.FetchPostsTask;
import shin.watchdog.scheduled.GHInterestChecksTask;
import shin.watchdog.scheduled.RefreshTokenTask;

public class Main {

	public static HttpClient httpclient = HttpClients.createDefault();

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

		BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
		
		RefreshTokenTask refreshTokenTask = new RefreshTokenTask();
		
		// Thread which consumes newly generated access tokens that RefreshTokenTask generates.
		// Sets the access token for use when sending PMs
		System.out.println("Starting token consumer thread");
		Thread tokenConsumer = new Thread(new AccessTokenConsumer(blockingQueue));
		tokenConsumer.start();
		
		// Pass the blocking queue which will be used for this and the consumer
		System.out.println("Starting refresh token task");
		refreshTokenTask.start(blockingQueue);
		
		// Start a task to check new posts periodically
		FetchPostsTask newPostsTask;
		System.out.println("Starting Reddit new posts task");
		newPostsTask = new FetchPostsTask(searchItems);
		newPostsTask.start();

		GHInterestChecksTask interestChecks;
		System.out.println("Starting Geekhack new topics task");
		interestChecks = new GHInterestChecksTask();
		interestChecks.start();

		System.out.println();

//		
//		
//        BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
//        while(true){
//            System.out.println("Press enter to exit\n");  
//            String s = null;
//            try {
//                s = br.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if(s.length() == 0){
//                System.out.println("Exiting...");
//                
//                System.out.println("Stopping token consumer");
//                tokenConsumer.interrupt();
//                
//                System.out.println("Stopping new post fetching");
//                newPostsTask.stop();
//                
//                System.out.println("Stopping refreshing token");
//                refreshTokenTask.stop();
//                
//                System.exit(0);
//            }
//        }
	}
}
