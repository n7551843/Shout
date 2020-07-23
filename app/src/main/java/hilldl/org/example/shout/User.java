package hilldl.org.example.shout;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {

    public String userID = null;
    public String nickname = null;
    public String email = null;
    public String password = null;

    public static final String USER = "User";

    public User() { }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    @NonNull
    @Override
    public String toString() {
        return "UserID = " + userID + ", \n" +
                "Nickname = " + nickname + ", \n" +
                "Email = " + email + ", \n" +
                "Password = " + password ;
    }
}

