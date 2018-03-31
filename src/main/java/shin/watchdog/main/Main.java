package shin.watchdog.main;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import shin.watchdog.scheduled.AccessTokenConsumer;
import shin.watchdog.scheduled.FetchPostsTask;
import shin.watchdog.scheduled.RefreshTokenTask;

public class Main {
	public static void main (String[] args) throws UnsupportedEncodingException {

		BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
		
		FetchPostsTask newPostsTask = new FetchPostsTask();
		RefreshTokenTask refreshTokenTask = new RefreshTokenTask();
		
		// Thread which consumes newly generated access tokens that RefreshTokenTask generates.
		// Sets the access token for use when sending PMs
		System.out.println("Starting consumer thread");
		Thread tokenConsumer = new Thread(new AccessTokenConsumer(blockingQueue));
		tokenConsumer.start();
		
		// Pass the blocking queue which will be used for this and the consumer
		System.out.println("Starting refresh token task");
		refreshTokenTask.start(blockingQueue);
		
		// Start the task which checks for new posts periodically
		System.out.println("Starting new posts task\n");
		newPostsTask.start();
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
