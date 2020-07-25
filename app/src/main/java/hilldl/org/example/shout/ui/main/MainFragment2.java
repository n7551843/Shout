package hilldl.org.example.shout.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import hilldl.org.example.shout.R;
import hilldl.org.example.shout.entities.Post;
import hilldl.org.example.shout.entities.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment2 extends Fragment {
    private static final String TAG = "MainFragment2";
    private User mUser;
    private EditText mText;
    private Button mButton;
    private static MainViewModel mViewModel;

    public MainFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment MainFragment2.
     */
    public static MainFragment2 newInstance() {
        MainFragment2 fragment = new MainFragment2();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set up the layout for this fragment
        View view = inflater.inflate(R.layout.main_fragment_2, container, false);

        mText = view.findViewById(R.id.et_newPost);
        mButton = view.findViewById(R.id.btn_newPost);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Get ViewModel to post with.
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // set User data for this fragment.  This will also
        // call a method to set up the button click listener.  I had to do it
        // this way to ensure User Data was retrieved before the button (which
        // utilizes the user data) was clicked.
        setUserData();

        super.onActivityCreated(savedInstanceState);
    }

    private void setUserData() {

        mViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser = user;
                Log.d(TAG, "onChanged: muser is " + mUser.toString());

                // set up button to post
                setUpButtonClickListener();
            }
        });

    }

    private void setUpButtonClickListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Step 1 - Grab the Text
                String text = mText.getText().toString().trim();

                // Step 2 - put it in a Post object if there is something to post
                if (text.length() <= 2) {
                    mText.setError("Post text must be at least 3 characters");
                } else {
                    Post post = buildPostObject(text);

                    // Step 3 - insert post with ViewModel
                    mViewModel.insert(post);

                    // Step 4 - Return to other Fragment, which should refresh itself automatically
                    returnToFirstFragment();
                }
            }
        });
    }

    private Post buildPostObject(String text) {
        Post post = new Post();
        String poster = mUser.getUserID();
        post.setUserID(poster);
        post.setPost(text);
        post.setNickname(mUser.getNickname());
        Date currentTime = new Date();
        post.setDateAndTime(currentTime.toString());
        return post;
    }

    private void returnToFirstFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment1.newInstance())
                .commitNow();
    }
}