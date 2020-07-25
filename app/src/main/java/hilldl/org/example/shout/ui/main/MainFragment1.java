package hilldl.org.example.shout.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

public class MainFragment1 extends Fragment implements MainAdapter.AdapterCallbacks {

    private static final String TAG = "MainFragment";
    private MainViewModel mViewModel;
    private MainAdapter mAdapter;
    private User mUser;
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

        // Set up the RecyclerView for populating the list
        rv = view.findViewById(R.id.main_rv_list);

        // set up SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        //Return the created view
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        //Retrieve the user data for the current user
        mViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: user details have updated");
                Log.d(TAG, "onChanged: user is " + user.toString());
                mUser = user;
            }
        });

        mAdapter = new MainAdapter(mViewModel.getAllPosts().getValue(), this);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refreshData();

            }
        });

        mViewModel.getAllPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                Log.d(TAG, "onChanged: called.  list size is " + posts.size());

                mAdapter.loadNewData(posts);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });


    }

    @Override
    public void deletePost(String postDate) {
        //TODO delete this stub
    }
}

