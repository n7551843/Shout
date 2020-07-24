package hilldl.org.example.shout.entities;

public class Post {

     String nickname;
     String post;
     String userID;
     String dateAndTime;
     String postID;

     public static final String DATEANDTIME = "dateAndTime";

    public Post() {
    }

    public String getPostID() { return postID; }

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

    public void setPostID(String postID) { this.postID = postID; }
}
