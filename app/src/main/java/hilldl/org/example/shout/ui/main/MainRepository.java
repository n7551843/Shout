package hilldl.org.example.shout.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import hilldl.org.example.shout.entities.Post;

public class MainRepository {
    private static final String TAG = "MainRepository";

    private LiveData<List<Post>> allPosts;
    private MainPostDatabase database;


    public MainRepository() {
        database = MainPostDatabase.getInstance();
        allPosts = database.getAllPosts();
    }

    public LiveData<List<Post>> getAllPosts() {
        return allPosts;
    }

    public void refreshData () {
        MainPostDatabase.refreshData();
    }

    /**
     * Call the Database and tell it to execute the new post.
     * @param post - the new post
     */

    public void insert(Post post) {
        if (database != null) {
            database.insert(post);
        } else {
            Log.d(TAG, "insert: database is null");
        }
    }
}
