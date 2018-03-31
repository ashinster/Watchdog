package shin.watchdog.scheduled;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RefreshTokenTask {
	private final ScheduledExecutorService scheduler = 
			Executors.newScheduledThreadPool(1);
	
	ScheduledFuture<?> refreshTokenHandle;
	
	public void start(BlockingQueue<String> queue) {
		final Runnable doRefreshToken = new RefreshTokenRunnable(queue);

		// Tokens expire every 3600 seconds, but get a new token at 3000 seconds
		refreshTokenHandle = 
				scheduler.scheduleAtFixedRate(doRefreshToken, 0, 1200, TimeUnit.SECONDS);
	}

	public void stop() {
		refreshTokenHandle.cancel(false);
	}
}
