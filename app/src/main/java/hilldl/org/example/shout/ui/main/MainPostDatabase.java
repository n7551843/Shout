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

public class MainPostDatabase {
    private static final String TAG = "MainPostDatabase";
    private static FirebaseDatabase db;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    private static String mUserID;
    public static final String ALLPOSTS = "AllPosts";

    private static MainPostDatabase instance;
    static MutableLiveData<List<Post>> allPosts;

    private MainPostDatabase() {
        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserID = mUser.getUid();
        allPosts = new MutableLiveData<List<Post>>();
        refreshData();
    }

    public static synchronized MainPostDatabase getInstance() {
        if (instance == null) {
            return new MainPostDatabase();
        } else {
            return instance;
        }
    }

    public LiveData<List<Post>> getAllPosts () {
        Log.d(TAG, "getAllPosts: this might explode bc not MutableLiveData");
        return allPosts;
    }

    public void insert (Post post) {
        new InsertPostAsyncTask().execute(post);
    }

    public void delete (Post post) {
        new DeletePostAsyncTask().execute(post);
    }


    private static class InsertPostAsyncTask extends AsyncTask<Post, Void, Void> {
        private static final String TAG = "InsertPostAsyncTask";

        @Override
        protected Void doInBackground(Post... post) {
            Log.d(TAG, "doInBackground: begins");
            db.getReference().child(RetrieveData.ALLPOSTS).push().setValue(post[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            Log.d(TAG, "onPostExecute: calling refreshData();");
//            super.onPostExecute(voids);
            refreshData();
        }
    }

    private static class DeletePostAsyncTask extends AsyncTask<Post, Void, Void> {
        private static final String TAG = "DeletePostAsyncTask";

        @Override
        protected Void doInBackground(Post... posts) {
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
//            super.onPostExecute(voids);
            refreshData();
        }
    }

    public static void refreshData() {
        Log.d(TAG, "refreshData: calling new UpdatePostsAsyncTask");
        new UpdatePostsAsyncTask().execute();
    }

    private static class UpdatePostsAsyncTask extends AsyncTask<Void, Void, List<Post>> {
        private static final String TAG = "UpdatePostsAsyncTask";
        static final List<Post> listbuilder = new ArrayList<Post>();

        @Override
        protected List<Post> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: begins");

            if (db != null) {
                DatabaseReference databaseReference = db.getReference().child(ALLPOSTS);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: begins building list");
                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Post post = child.getValue(Post.class);
                            listbuilder.add(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: cancelled");
                    }
                });

            }
            
            if (listbuilder == null) {
                Log.d(TAG, "doInBackground: listbuilder is null");
            }
            
            return listbuilder;
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            Log.d(TAG, "onPostExecute: setting new value of allPosts");
            allPosts.setValue(posts);
            
//            super.onPostExecute(posts);
        }
    }


}
