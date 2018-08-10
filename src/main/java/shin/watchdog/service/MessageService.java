package shin.watchdog.service;

import java.util.List;

import shin.watchdog.data.Post;


public interface MessageService{
    boolean sendMessage(String topic, List<Post> potentialPosts, String user);
}