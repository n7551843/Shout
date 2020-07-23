package hilldl.org.example.shout;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RetrieveData extends AsyncTask<Void, Void, List<Post>> {
    private static final String TAG = "RetrieveData";
    private FirebaseDatabase db;
    List<Post> mPosts = null;
    public static String ALLPOSTS = "AllPosts";
    public static final String ALLUSERS = "AllUsers";
    private DataCallBack mCallBack;

    public RetrieveData(FirebaseDatabase db, DataCallBack callBack) {
        this.db = db;
        this.mCallBack = callBack;
    }

    public interface DataCallBack {
        void onPostsUpdated(List<Post> posts);
    }

    @Override
    protected List<Post> doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: begins");
        final List<Post> listbuilder = new ArrayList<Post>();
        if (db != null) {
            Log.d(TAG, "doInBackground: db not null");
            DatabaseReference databaseReference = db.getReference();
            databaseReference.child(ALLPOSTS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: starts");
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Post post = child.getValue(Post.class);
                        listbuilder.add(post);
                    }
                    mPosts = listbuilder;
                    callback();
                }

                private void callback() {
                    if (mPosts != null) {
                        Log.d(TAG, "onDataChange: MPOSTS SIZE " + mPosts.size());
                        mCallBack.onPostsUpdated(mPosts);
                    } else {
                        Log.d(TAG, "onDataChange: MPOSTS NULL");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: called");
                    Log.d(TAG, "onCancelled: Database Error " + error.toString());
                }
            });
        }
//        if (mPosts == null) {
//            Log.d(TAG, "doInBackground: mPosts is null ***********");
//        } else {
//            Log.d(TAG, "doInBackground: returning mPosts with value " + mPosts.size());
//        }
        return mPosts;
    }

    @Override
    protected void onPostExecute(List<Post> posts) {



//        if (mPosts != null && mCallBack != null) {
//            Log.d(TAG, "onPostExecute: calling back Main Fragment");
//        } else {
//            Log.d(TAG, "onPostExecute: couldn't call back Main Fragment.  " +
//                    "Either list or callback is null");
//        }

        super.onPostExecute(posts);
    }


}
