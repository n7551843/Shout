package hilldl.org.example.shout.loginclasses;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import hilldl.org.example.shout.ui.main.MainPostDatabase;

enum LoginStatus {IDLE, PENDING, SUCCESSFUL, FAILED}

public class LoginDatabase {

    private static final String TAG = "LoginDatabase";
    private static FirebaseDatabase db;
    private static FirebaseAuth mAuth;
    private MutableLiveData<LoginStatus> mLoginStatus;
    private Exception mLoginException;

    public static final String ALLPOSTS = "AllPosts";
    public static final String ALLUSERS = "AllUsers";

    private static LoginDatabase instance;

    private LoginDatabase() {
        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // if this updates to true, login was successful.
        // might have to make this an int with a switch at the top if
        // the observer in LoginFragments does not recognize it updating from false to false;
        mLoginStatus = new MutableLiveData<LoginStatus>(LoginStatus.IDLE);
    }

    public static synchronized LoginDatabase getInstance() {
        if (instance == null) {
            instance = new LoginDatabase();
        }
        return instance;
    }

    public void signInWithEmailAndPassword(String email, String password) {
        Log.d(TAG, "signInWithEmailAndPassword: called");
        new FireBaseSignInAsyncTask(email, password).execute();
    }

    public MutableLiveData<LoginStatus> getAuthenticationStatus () {
        return mLoginStatus;
    }

    public Exception getLoginException() {
        return mLoginException;
    }

    private class FireBaseSignInAsyncTask extends AsyncTask<Void, Void, Void> {

        private String email;
        private String password;

        public FireBaseSignInAsyncTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "FireBaseSignInAsyncTask doInBackground: begins");
            if (mAuth != null) {
                mLoginStatus.postValue(LoginStatus.PENDING);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onComplete: signInWithEmailAndPassword() complete");
                                if (task.isSuccessful()) {
                                    // Inform LoginViewModel of successful authentication
                                    // authentication
                                    mLoginStatus.postValue(LoginStatus.SUCCESSFUL);
                                } else {
                                    // Inform LoginViewModel of failed sign in
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    mLoginException = task.getException();
                                    mLoginStatus.postValue(LoginStatus.FAILED);
                                }
                            }
                        });
            } else {
                Log.d(TAG, "doInBackground: no firebase authority");
                mLoginStatus.postValue(LoginStatus.FAILED);
            }
            Log.d(TAG, "FirebaseSignInAsyncTask doInBackground: ends");
            return null;
        }
    }

}
