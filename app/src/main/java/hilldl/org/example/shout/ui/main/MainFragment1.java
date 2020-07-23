package hilldl.org.example.shout.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import hilldl.org.example.shout.MainAdapter;
import hilldl.org.example.shout.entities.Post;
import hilldl.org.example.shout.R;
import hilldl.org.example.shout.RetrieveData;
import hilldl.org.example.shout.entities.User;

public class MainFragment1 extends Fragment implements RetrieveData.DataCallBack,
                                                    MainAdapter.AdapterCallbacks {

    private static final String TAG = "MainFragment";
    private MainViewModel mViewModel;
    private FirebaseAuth mAuth;
    private List<Post> mPostList;
    private MainAdapter mAdapter;
    private User mUser = null;
    private FirebaseUser mFirebaseUser;
    private String mFirebaseUID;
    private FirebaseDatabase db;
    private RecyclerView rv;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static MainFragment1 newInstance() {
        return new MainFragment1();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_1, container, false);

        //Get User Data from the Intent, to identify the user while posting
        getIntentUserData();

        //Set up the RecyclerView for populating the list
        rv = view.findViewById(R.id.main_rv_list);
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                RetrieveData retrieveData = new RetrieveData(db, MainFragment1.this);
//                retrieveData.execute();
//            }
//        });

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mViewModel.getAllPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {

             for (int i = 0; i < posts.size(); i++) {
                 Post dummyPost = posts.get(i);
                 Log.d(TAG, "onChanged: ****************\n" +
                                            dummyPost.getPost() +
                                            "\n***************");
             }

            }
        });

        //Return the created view
        return view;
    }

    @Override
    public void onStart() {

        //Retrieve Firebase details.  Note this happens
        // after onViewCreated
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        //Set up the RecyclerView for populating
        mAdapter = new MainAdapter(null, mAuth, this);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(mAdapter);

        //Populate the RecyclerView with posts from the Database.
        RetrieveData retrieveData = new RetrieveData(db, this);
        retrieveData.execute();
        Log.d(TAG, "onCreate: ends");

        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    /**
     * Callback method for the 'RetrieveData' object.  Receives the list from the
     * object and sends it to the MainAdapter object created in
     * the onCreateView method, telling it to update the Recycler View.
     *
     * @param posts is the new list of Post objects retrieved from the Database.
     */

    @Override
    public void onPostsUpdated(List<Post> posts) {
        Log.d(TAG, "onPostsUpdated: called");
        mPostList = posts;
        mAdapter.loadNewData(mPostList);

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Retrieves the User object from the activity's intent.  Provided it isn't null,
     * the mUser field is then set to be the retrieved object.
     */

    public void getIntentUserData() {
        Log.d(TAG, "getIntentUserData: called");
        // Get User Data from the Login Screen
        Intent intent = requireActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mUser = (User) extras.getSerializable(User.USER);
            Log.d(TAG, "getIntentUserData: mUser is " + mUser.toString());
        } else {
            throw new InstantiationError("No User object in activity intent " +
                    "to identify the User. When this Fragment is created, the particular " +
                    "activity's intent must contain a User.class object or this activity " +
                    "will crash.  If this is occurring often, retool this structure.");
        }
    }


    /**
     * Callback for MainFragment2 to refresh the RecyclerView data.
     */
    public void deletePost(String postDate) {
        DatabaseReference reference = db.getReference();
        Query deleteQuery = reference.child(RetrieveData.ALLPOSTS).orderByChild(Post.DATEANDTIME).equalTo(postDate);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot deletePost : dataSnapshot.getChildren()) {
                    deletePost.getRef().removeValue();

                    RetrieveData retrieveData = new RetrieveData(db, MainFragment1.this);
                    retrieveData.execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

}


//        Post testPost = new Post();
//        testPost.setPost("Testing 2");
//        testPost.setUser("Test hilldl.org.example.shout.entities.User 2");
//        DatabaseReference databaseReference = db.getReference();
//        databaseReference.child(RetrieveData.ALLPOSTS).push().setValue(testPost);