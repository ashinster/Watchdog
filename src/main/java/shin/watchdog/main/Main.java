package shin.watchdog.main;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static RequestConfig config = RequestConfig.custom()
		.setConnectTimeout(2 * 1000)
		.setConnectionRequestTimeout(2 * 1000)
		.setSocketTimeout(5 * 1000)
		.build();

	public static HttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
}
