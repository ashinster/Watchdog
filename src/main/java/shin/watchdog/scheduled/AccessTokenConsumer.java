package shin.watchdog.scheduled;

import java.util.concurrent.BlockingQueue;

public class AccessTokenConsumer implements Runnable {

	private final BlockingQueue<String> queue;

	public AccessTokenConsumer(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		// When a new access token is available, 
		// consume it from the queue and set it as the access token to use
		// for API calls
		try {
			while (true) {
				// Wait til a new token is added from the token refresh process
				String token = queue.take();
				//System.out.println("Token " + token + " consumed\n");
				
				// Set token for use when sending a PM
				FetchPostRunnable.accessToken = token;
			}
		} catch (InterruptedException ex) {
		}

	}
}
