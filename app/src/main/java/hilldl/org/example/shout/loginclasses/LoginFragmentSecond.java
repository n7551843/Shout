package hilldl.org.example.shout.loginclasses;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import hilldl.org.example.shout.MainActivity;
import hilldl.org.example.shout.R;
import hilldl.org.example.shout.RetrieveData;
import hilldl.org.example.shout.entities.User;

public class LoginFragmentSecond extends Fragment {

    private static final String TAG = "LoginFragmentSecond";
    private LoginViewModel mViewModel;
    TextView txtvw_name;
    TextView txtvw_email;
    TextView txtvw_password;
    TextView txtvw_repassword;
    TextView txtvw_nomatch;
    Button btn_register;
    Button returnButton;
    private boolean passwordsMatch;

    public LoginFragmentSecond() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        passwordsMatch = true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment_second, container, false);
        txtvw_name = view.findViewById(R.id.et_name);
        txtvw_email = view.findViewById(R.id.et_email);
        txtvw_password = view.findViewById(R.id.et_password);
        txtvw_repassword = view.findViewById(R.id.et_repassword);
        txtvw_nomatch = view.findViewById(R.id.txtvw_password_no_match);
        btn_register = view.findViewById(R.id.btn_register);
        returnButton = view.findViewById(R.id.button_return_to_login);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mViewModel.getRegistrationStatus().observe(getViewLifecycleOwner(), new Observer<RegistrationStatus>() {
            @Override
            public void onChanged(RegistrationStatus registrationStatus) {
                switch (registrationStatus) {
                    case IDLE:
                        btn_register.setEnabled(true);
                        break;
                    case PENDING:
                        btn_register.setEnabled(false);
                        break;
                    case SUCCESSFUL:
                        registrationSuccessful();
                        break;
                    case FAILED:
                        registrationFailed();
                        btn_register.setEnabled(true);
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognised LoginStatus received");
                }
            }
        });

        // set onClickListener for button to return to Login Page.
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragmentSecond.this)
                        .navigate(R.id.action_fragment_login_second_to_fragment_login_first);
            }
        });

        // set onClickListener for button to register an address.
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordsMatch = true;
                txtvw_nomatch.setVisibility(View.GONE);
                txtvw_email.setError(null);
                txtvw_name.setError(null);

                String name = txtvw_name.getText().toString().trim();
                String email = txtvw_email.getText().toString().toLowerCase().trim();
                String password = txtvw_password.getText().toString().trim();
                String repassword = txtvw_repassword.getText().toString().trim();

                if (email.isEmpty() || name.isEmpty()) {
                    txtvw_name.setError("These fields cannot be empty.");
                    txtvw_email.setError("These fields cannot be empty.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtvw_email.setError("Please enter a valid email address.");
                } else {
                    validateEmail(name, email, password, repassword);
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Final check to make sure new password and repassword match, before attempting to register the
     * email with FireBase by calling the registerEmailWithFirebase method and passing it email and password
     *
     * @param alias      - the name entered into the Name textview, grabbed in the onClickListener.
     * @param email      - the email entered into the Email textview, grabbed in the onClickListener.
     * @param password   - the password entered into the Password textview, grabbed in the onClickListener.
     * @param repassword - the re-enter password entered into the RePassword textview, grabbed in the onClickListener.
     */

    private void validateEmail(String alias, String email, String password, String repassword) {
        if (password.equals(repassword)) {
            Log.d(TAG, "onClick: passwords match, calling registerEmailWithFireBase");
            registerEmailWithFirebase(email, password, alias);
        } else {
            Log.d(TAG, "onClick: passwords don't match");
            passwordsMatch = false;
        }
        if (!passwordsMatch) {
            txtvw_nomatch.setVisibility(View.VISIBLE);
            txtvw_nomatch.setText(R.string.tooltip_passwords_dont_match);
        }
    }

    /**
     * This method called internally once the email and password have been checked with regular expression.
     * Note: the checks mentioned below occur in the button's OnClickListener.
     *
     * @param email    - is the new email entered into the TextView, once it has been checked.
     * @param password - is the new password, also once it has been checked.
     */

    private void registerEmailWithFirebase(String email, String password, String nickname) {
        Log.d(TAG, "registerEmailWithFirebase: begins");
        mViewModel.registerEmailWithFirebase(email, password, nickname);
    }

    private void registrationSuccessful() {
        Log.d(TAG, "registrationSuccessful: starting MainActivity");
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    private void registrationFailed() {
//        Log.d(TAG, "registrationFailed: failed");
//        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
//            txtvw_nomatch.setVisibility(View.VISIBLE);
//            txtvw_nomatch.setText(R.string.tooltip_email_already_used);
//        }
        Toast.makeText(requireActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
        //TODO (eventually) catch all the exceptions and set appropriate errors

    }
}
