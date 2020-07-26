package hilldl.org.example.shout.loginclasses;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

import hilldl.org.example.shout.MainActivity;
import hilldl.org.example.shout.R;
import hilldl.org.example.shout.RetrieveData;
import hilldl.org.example.shout.entities.User;
import hilldl.org.example.shout.ui.main.MainPostDatabase;


public class LoginFragmentFirst extends Fragment {
    private static final String TAG = "LoginFragmentFirst";
    private LoginViewModel mViewModel;

    Button loginBtn;
    Button registerBtn;
    EditText email_editText;
    TextView incorrectDetailsTxtVw;
    EditText password_editText;

    public LoginFragmentFirst() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_fragment_first, container, false);

        registerBtn = view.findViewById(R.id.button_click_to_register);
        email_editText = view.findViewById(R.id.et_email);
        password_editText = view.findViewById(R.id.et_password);
        loginBtn = view.findViewById(R.id.btn_login);
        incorrectDetailsTxtVw = view.findViewById(R.id.login_incorrect_details_textview);
        incorrectDetailsTxtVw.setVisibility(View.GONE);

//        final ConstraintLayout mLayout = view.findViewById(R.id.lgnfirst_constraintLayout);

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
                incorrectDetailsTxtVw.setVisibility(View.GONE);
                String currentEmail = email_editText.getText().toString().toLowerCase().trim();
                String currentPassword = password_editText.getText().toString().trim();

                if (currentEmail.isEmpty()) {
                    email_editText.setError("Field can't be empty.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
                    email_editText.setError("Please enter a valid email address.");
                } else {
                    signInWithFirebase(currentEmail, currentPassword);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mViewModel.getAuthenticationStatus().observe(getViewLifecycleOwner(), new Observer<LoginStatus>() {
            @Override
            public void onChanged(LoginStatus loginStatus) {
                switch (loginStatus) {
                    case IDLE:
                        loginBtn.setEnabled(true);
                        break;
                    case PENDING:
                        loginBtn.setEnabled(false);
                        break;
                    case SUCCESSFUL:
                        authenticationSuccessful();
                        break;
                    case FAILED:
                        authenticationFailed();
                        loginBtn.setEnabled(true);
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognised LoginStatus received");
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }


    private void signInWithFirebase(String email, String password) {
        mViewModel.signInWithEmailAndPassword(email, password);
    }

    /**
     * Start a new activity if the ViewModel returns that Login has been successful;
     */

    private void authenticationSuccessful() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Called if authentication fails.  Shows a Toast message and populates a field with
     * text describing the error (BETA).
     */

    private void authenticationFailed() {
        if (mViewModel.getLoginException() != null) {
            incorrectDetailsTxtVw.setText(mViewModel.getLoginException().toString());
            incorrectDetailsTxtVw.setVisibility(View.VISIBLE);
        }
        Toast.makeText(requireActivity(), "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }
}