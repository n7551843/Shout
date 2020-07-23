package hilldl.org.example.shout;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import hilldl.org.example.shout.ui.main.MainFragment1;

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
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private FirebaseUser mFirebaseUser;

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
    // TODO: Rename and change types and number of parameters
    public static MainFragment2 newInstance() {
        MainFragment2 fragment = new MainFragment2();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_fragment_2, container, false);

        //Get User Data from the intent and store it, or Crash.
        getIntentUserData();

        //Get Firebase Database references for posting
        setUpDatabaseForPosting();

        mText = view.findViewById(R.id.et_newPost);
        mButton = view.findViewById(R.id.btn_newPost);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Step 1 - Grab the Text
                String text = mText.getText().toString().trim();

                // Step 2 - put it in a Post object
                Post post = new Post();
                String poster = mUser.getUserID();
                post.setUserID(poster);
                post.setPost(text);
                post.setNickname(mUser.getNickname());
                Date currentTime = new Date();
                post.setDateAndTime(currentTime.toString());


                // Step 3 - post the object to the database
                db.getReference().child(RetrieveData.ALLPOSTS).push().setValue(post);

                // Step 4 - Return to other Fragment, which should refresh itself automatically
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MainFragment1.newInstance())
                        .commitNow();

            }
        });


        return view;
    }

    /**
     * Called internally when onCreateView is called.  Get's user data from the Activity's intent
     * and stores it in the mUser field.  Will crash the program if no User is found in the intent.
     */

    private void getIntentUserData() {
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

    private void setUpDatabaseForPosting () {
        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
    }


}