package hilldl.org.example.shout.entities;

public class Comment {

    String commenterNickname;
    String postID;
    String comment;
    String userID;
    String dateAndTime;

    public Comment() {
    }

    public String getCommenterNickname() {
        return commenterNickname;
    }

    public void setCommenterNickname(String commenterNickname) {
        this.commenterNickname = commenterNickname;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
