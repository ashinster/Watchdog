package shin.watchdog.service;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import shin.watchdog.data.atom.Feed;

@Service
public class GeekhackPostsService{
    final static Logger logger = LoggerFactory.getLogger(GeekhackPostsService.class);

    private static final int TIMEOUT = 10;

    private static RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(1 * 1000)
        .setConnectionRequestTimeout(1 * 1000)
        .setSocketTimeout(TIMEOUT * 1000)
        .build();

    private static HttpClient httpclient = HttpClientBuilder.create()
        .setDefaultRequestConfig(config)
        .setConnectionManager(new PoolingHttpClientConnectionManager())
        .build();

    private JAXBContext jaxbContext;
    private Unmarshaller jaxbUnmarshaller;

    public GeekhackPostsService() throws JAXBException{
        this.jaxbContext = JAXBContext.newInstance(Feed.class);
        this.jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    public Feed makeCall(String feedUrl, String boardName) {
        Feed rssFeed = null;

		HttpGet httpget = new HttpGet(feedUrl);

        // Execute and get the response.
        HttpEntity entity = null;
		try {
            HttpResponse response = httpclient.execute(httpget);

            entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() >= 300) {
				logger.error("Retrieved bad response code: " + response.getStatusLine());
			} else {
				if (entity != null) {
                    // Create the Rss object from the stream
                    // String xml = EntityUtils.toString(response.getEntity());
                    // logger.info(xml);
                    // StringReader reader = new StringReader(xml);
                    rssFeed = (Feed) jaxbUnmarshaller.unmarshal(response.getEntity().getContent());
				} else {
					logger.error("Entity from Geekhack RSS GET request was null for " + boardName);
				}
			}
        } catch (SocketTimeoutException e){
            logger.error("SocketTimeoutException getting new {} - {}", boardName, e.getMessage());
        } catch (IOException e) {
            logger.error("IO Exception getting new " + boardName, e);
        } catch (JAXBException e) {
            logger.error("Error Unmarshalling rss feed for " + boardName, e);
        } catch (Throwable e) {
            logger.error("Unknown error getting new " + boardName, e);
        } finally{
			if(entity != null){
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
                    logger.error("Error trying to consume Geekhack entity for " + boardName, e);
				}
            }
        }

        return rssFeed;
    }
}