package shin.watchdog.data;

public class AlertTopic{
    private String roleId;
    private String topic;

    /**
     * @param topicName the topicName to set
     */
    public void setRoleId(String topicName) {
        this.roleId = topicName;
    }

    /**
     * @param topicAuthor the topicAuthor to set
     */
    public void setTopic(String topicAuthor) {
        this.topic = topicAuthor;
    }

    /**
     * @return the topicName
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * @return the topicAuthor
     */
    public String getTopic() {
        return topic;
    }
}