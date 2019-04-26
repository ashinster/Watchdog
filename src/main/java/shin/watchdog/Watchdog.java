package shin.watchdog;

import java.util.UUID;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import shin.watchdog.processor.GeekhackProcessor;
import shin.watchdog.processor.MechMarketProcessor;

@Configuration
@EnableScheduling
public class Watchdog{
    
	public static RequestConfig config = RequestConfig.custom()
		.setConnectTimeout(2 * 1000)
		.setConnectionRequestTimeout(2 * 1000)
		.setSocketTimeout(10 * 1000)
		.build();

    public static CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    
    @Autowired
    private GeekhackProcessor icProcessor;

    @Autowired
    private GeekhackProcessor gbProcessor;

    @Autowired
    private MechMarketProcessor mmProcessor;

    @Scheduled(cron = "${interval:0} * * * * *")
    public void mechmarket(){
        MDC.put("uuid", UUID.randomUUID().toString());
        mmProcessor.process();
        MDC.clear();
    }

    //@Scheduled(cron = "${interval:0} * * * * *")
    public void getInterestChecks(){
        MDC.put("uuid", UUID.randomUUID().toString());
        icProcessor.process();
        MDC.clear();
    }

    //@Scheduled(cron = "${interval:0} * * * * *")
    public void getGroupBuys(){
        MDC.put("uuid", UUID.randomUUID().toString());
        gbProcessor.process();
        MDC.clear();
    }

}