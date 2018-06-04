package shin.watchdog.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.service.NewRedditPostsService;
import shin.watchdog.service.RedditMessageService;

/**
 * Used to hold information about the recent MechMarket posts. Holds a cache of previous posts to compare with newly retrieved posts.
 */
public class Subreddit {
}