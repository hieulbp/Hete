package henho.ketban.hete.Matches;

/**
 * Created by manel on 10/31/2017.
 */

public class MatchesObject {
    private String  userId,
            name,
            lastMessage,
            chatId,
            profileImageUrl,
            active;


    public MatchesObject(String userId, String name, String profileImageUrl, String chatId, String lastMessage, String active){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.lastMessage = lastMessage;
        this.chatId = chatId;
        this.active = active;
    }

    public String getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getLastMessage(){
        return lastMessage;
    }
    public String getChatId(){
        return chatId;
    }
    public String getProfileImageUrl(){
        return profileImageUrl;
    }

    public String getActive() {
        return active;
    }

    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
    public void setChatId(String chatId){
        this.chatId = chatId;
    }
}
