package hilldl.org.example.shout.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import hilldl.org.example.shout.entities.Post;
import hilldl.org.example.shout.entities.User;

public class MainViewModel extends ViewModel {

    private MainRepository repository;
    private LiveData<List<Post>> allPosts;

    public MainViewModel() {
        repository = new MainRepository();
        allPosts = repository.getAllPosts();
    }

    public void insert(Post post) {
        repository.insert(post);
    }

    public LiveData<List<Post>> getAllPosts() {
        return allPosts;
    }

    public void refreshData() {
        repository.refreshData();
    }

    public MutableLiveData<User> getUserData() { return repository.getUserData(); }
}