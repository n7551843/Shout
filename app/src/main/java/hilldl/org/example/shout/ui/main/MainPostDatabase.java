package hilldl.org.example.shout.ui.main;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hilldl.org.example.shout.RetrieveData;
import hilldl.org.example.shout.entities.Post;
import hilldl.org.example.shout.entities.User;

public class MainPostDatabase {
    private static final String TAG = "MainPostDatabase";
    private static FirebaseDatabase db;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;
    private static String firebaseUserID;
    private MutableLiveData<User> mUser;

    public static final String ALLPOSTS = "AllPosts";
    public static final String ALLUSERS = "AllUsers";

    private static MainPostDatabase instance;
    // this should probably be static
    MutableLiveData<List<Post>> allPosts;

    private MainPostDatabase() {
        db = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseUserID = firebaseUser.getUid();
        allPosts = new MutableLiveData<List<Post>>();
        mUser = new MutableLiveData<User>();
        setUserData();
        refreshData();
    }

    /**
     * Calls an instance of the singleton class MainPostDatabase.  If no instance exists, this
     * method will create one, otherwise it will return the instance.
     *
     * This method is thread-safe.
     * @return
     */

    public static synchronized MainPostDatabase getInstance() {
        if (instance == null) {
            return new MainPostDatabase();
        } else {
            return instance;
        }
    }

    /**
     * This method uses the firebaseUserID retrieved from FireBase when MainPostDatabase is created, and
     * sends a second call to retrieve all the data for the user.  It then stores that data in
     * a User.class object, ready to be retrieved by the repository when a separate call is made.
     * @returns nothing but sets mUser to the user data in the database object.
     */

    //TODO move the contents of this method to a new AsyncTask.

    public void setUserData() {
        Log.d(TAG, "setUserData: called");
        if (firebaseUserID != null) {
           db.getReference().child(ALLUSERS).child(firebaseUserID)
                   .addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           Log.d(TAG, "onDataChange: begin building User Data");
                           User user = snapshot.getValue(User.class);
                           user.setUserID(firebaseUserID);
                           mUser.setValue(user);
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {
                           Log.d(TAG, "onCancelled: Unable to retrieve user data. Error is " + error.toString());
                       }
                   });
        }
    }

    /**
     * returns the User object containing this user's data up the chain to the repository.
     * @return
     */

    public MutableLiveData<User> getUserData() {
            return mUser;
    }

    public LiveData<List<Post>> getAllPosts() {
        Log.d(TAG, "getAllPosts: this might explode bc not MutableLiveData");
        return allPosts;
    }

    public void insert(Post post) {
        new InsertPostAsyncTask().execute(post);
    }

    public void delete(Post post) {
        new DeletePostAsyncTask().execute(post);
    }


    private class InsertPostAsyncTask extends AsyncTask<Post, Void, Void> {
        private static final String TAG = "InsertPostAsyncTask";

        @Override
        protected Void doInBackground(Post... post) {
            Log.d(TAG, "doInBackground: begins");
            String newPost = db.getReference().child(RetrieveData.ALLPOSTS).push().getKey();
            if (newPost != null) {
                Log.d(TAG, "doInBackground: posting new post with key retrieved");
                post[0].setPostID(newPost);
                db.getReference().child(ALLPOSTS).child(newPost).setValue(post[0]);

            } else {
                Log.d(TAG, "doInBackground: cannot post as newPost key as null");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            Log.d(TAG, "onPostExecute: calling refreshData();");
            refreshData();
        }
    }

    private class DeletePostAsyncTask extends AsyncTask<Post, Void, Void> {
        private static final String TAG = "DeletePostAsyncTask";

        @Override
        protected Void doInBackground(Post... posts) {
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            refreshData();
        }
    }

    public void refreshData() {
        Log.d(TAG, "refreshData: calling new UpdatePostsAsyncTask");
        new UpdatePostsAsyncTask().execute();
    }

    private class UpdatePostsAsyncTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "UpdatePostsAsyncTask";
        final List<Post> listbuilder = new ArrayList<Post>();

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: begins");

            if (db != null) { // note - legacy null check
                DatabaseReference databaseReference = db.getReference().child(ALLPOSTS);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: begins building list");
                        int i = 1;
                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Post post = child.getValue(Post.class);
                            listbuilder.add(post);
                        }
                        postList(listbuilder);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: cancelled");
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        private void postList(List<Post> list) {
            allPosts.postValue(list);
        }

    }
}
