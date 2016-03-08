package achamp.project.org.achamp;

/**
 * Created by Nima on 3/4/2016.
 */
public class AchampFriend {
    private String friendID;


    private String userName;
    private boolean isFriend;

    public AchampFriend(String newFriendID, String newName, boolean tempIsFriend)
    {
        friendID = newFriendID;
        userName = newName;
        isFriend = tempIsFriend;
    }



    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

}
