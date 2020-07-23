package hilldl.org.example.shout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginFragmentFirst extends Fragment {
    private static final String TAG = "LoginFragmentFirst";
    private boolean showIncorrectDetails;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private User mCorrectUser;


    public LoginFragmentFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        
        View view = inflater.inflate(R.layout.login_fragment_first, container, false);

        final Button registerBtn = view.findViewById(R.id.button_click_to_register);
        final EditText email_editText = view.findViewById(R.id.et_email);
        final EditText password_editText = view.findViewById(R.id.et_password);
        final Button loginBtn = view.findViewById(R.id.btn_login);
        final TextView incorrectDetailsTxtVw = view.findViewById(R.id.login_incorrect_details_textview);
        final ConstraintLayout mLayout = view.findViewById(R.id.lgnfirst_constraintLayout);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragmentFirst.this)
                        .navigate(R.id.action_loginFragmentv2_to_loginFragment_Register);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "loginBtn onClick: begins");
                String currentEmail = email_editText.getText().toString().toLowerCase().trim();
                String currentPassword = password_editText.getText().toString().trim();

                if (currentEmail.isEmpty()) {
                    email_editText.setError("Field can't be empty.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                    email_editText.setError("Please enter a valid email address.");
                } else {
                    Log.d(TAG, "onClick: running singInWithFirebase");
                    signInWithFirebase(currentEmail, currentPassword);


                    if (!showIncorrectDetails) {
                        //Show incorrect details warning
                        incorrectDetailsTxtVw.setVisibility(View.VISIBLE);
                        // TODO clear keyboard focus
                    }
                }
                Log.d(TAG, "onClick: ends");
            }
        });

        return view;
    }


    private void signInWithFirebase(String email, String password) {
        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                final String userId = firebaseUser.getUid();

                                db.getReference().child(RetrieveData.ALLUSERS).child(userId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Log.d(TAG, "onDataChange: starts");
                                        Iterable<DataSnapshot> children = snapshot.getChildren();
                                        mCorrectUser = snapshot.getValue(User.class);
                                        mCorrectUser.setUserID(userId);

                                        if (mCorrectUser != null) {
                                            Log.d(TAG, "onComplete: starting new activity with User " +
                                                    mCorrectUser.getUserID());
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.putExtra(User.USER, mCorrectUser);
                                            startActivity(intent);
                                        } else {
                                            Log.d(TAG, "onComplete: mCorrectUser is null *****");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d(TAG, "onCancelled: called");
                                        Log.d(TAG, "onCancelled: Database Error " + error.toString());
                                    }
                                });
                                


                            } else {
                                // If sign in fails, display a message to the user.
                                showIncorrectDetails = false;
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(requireActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



}