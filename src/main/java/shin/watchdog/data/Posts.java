package shin.watchdog.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

public class Posts implements Cloneable{

	ArrayList<String> postNames = new ArrayList<>();

	Map<String, JsonObject> postsData = new HashMap<>();
	
	public ArrayList<String> getPostNames() {
		return postNames;
	}

	public void setPostNames(ArrayList<String> postNames) {
		this.postNames = postNames;
	}

	public Map<String, JsonObject> getPostsData() {
		return postsData;
	}

	public void setPostsData(Map<String, JsonObject> posts) {
		this.postsData = posts;
	}
	
	public ArrayList<String> getNamesAsNewList(){
		ArrayList<String> tmp = new ArrayList<>();
		for(String s : postNames) {
			tmp.add(new String(s));
		}
		return tmp;
	}

	public void addPostName(String s) {
		if(postNames.size() == 50) {
			postNames.remove(0);
		}
		postNames.add(s);
		
	}
}
