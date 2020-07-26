package hilldl.org.example.shout.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
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

import java.util.List;

import hilldl.org.example.shout.MainAdapter;
import hilldl.org.example.shout.entities.Post;
import hilldl.org.example.shout.R;
import hilldl.org.example.shout.entities.User;

public class MainFragment1 extends Fragment {

    private static final String TAG = "MainFragment";
    private MainViewModel mViewModel;
    private MainAdapter mAdapter;
    private User mUser;
    private RecyclerView rv;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Activity mActivity;

    public interface Callback {
        void hideFab();
        void showFab();
    }

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

        // set up callback for showing/hiding fab
        retrieveCallbackActivity();

        //Return the created view
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mAdapter = new MainAdapter(mViewModel.getAllPosts().getValue());

        //Retrieve the user data for the current user
        mViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: user details have updated");
                Log.d(TAG, "onChanged: user is " + user.toString());
                mUser = user;
                mAdapter.setUserID(mUser.getUserID());
            }
        });

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

    public void retrieveCallbackActivity() {
        //Called in onCreateView
        mActivity = requireActivity();
        if (!(mActivity instanceof Callback)) {
            throw new InstantiationError("The activity hosting this fragment must contain callback methods");
        } else {
            ((Callback) mActivity).showFab();
        }
    }

    @Override
    public void onDetach() {
        ((Callback) mActivity).hideFab();
        mActivity = null;
        super.onDetach();
    }
}

