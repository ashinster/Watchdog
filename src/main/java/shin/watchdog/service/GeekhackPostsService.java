package shin.watchdog.service;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import shin.watchdog.Watchdog;
import shin.watchdog.data.atom.Feed;

@Service
public class GeekhackPostsService{
    final static Logger logger = LoggerFactory.getLogger(GeekhackPostsService.class);

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
		try(CloseableHttpResponse response = Watchdog.httpclient.execute(httpget);) {

			if (response.getStatusLine().getStatusCode() >= 300) {
				logger.error("Retrieved bad response code: " + response.getStatusLine());
			} else {
				rssFeed = (Feed) jaxbUnmarshaller.unmarshal(response.getEntity().getContent());
            }
            
        } catch (SocketTimeoutException e){
            logger.error("SocketTimeoutException getting new {} - {}", boardName, e.getMessage());
        } catch (IOException e) {
            logger.error("IO Exception getting new " + boardName, e);
        } catch (JAXBException e) {
            logger.error("Error Unmarshalling rss feed for " + boardName, e);
        } catch (Throwable e) {
            logger.error("Unknown error getting new " + boardName, e);
        }

        return rssFeed;
    }
}