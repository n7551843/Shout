package hilldl.org.example.shout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.w3c.dom.Comment;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<Comment>> users;
    public LiveData<List<Comment>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<List<Comment>>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        // Do an asynchronous operation to fetch users.
    }





}
