package shin.watchdog.service;

import java.util.List;

import shin.watchdog.interfaces.SiteData;

public interface MessageService{
    boolean sendMessage(List<SiteData> potentialPosts, List<String> users);
}