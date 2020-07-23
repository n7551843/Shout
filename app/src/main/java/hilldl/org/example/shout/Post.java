package hilldl.org.example.shout;

public class Post {

     String nickname;
     String post;
     String userID;
     String dateAndTime;

     public static final String DATEANDTIME = "dateAndTime";

    public Post() {
    }

    public String getUserID() { return userID; }

    public String getNickname() {
        return nickname;
    }

    public String getPost() {
        return post;
    }

    public String getDateAndTime() { return dateAndTime; }

    public void setNickname(String user) {
        this.nickname = user;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setUserID(String userID) { this.userID = userID; }

    public void setDateAndTime(String dateAndTime) { this.dateAndTime = dateAndTime; }
}
