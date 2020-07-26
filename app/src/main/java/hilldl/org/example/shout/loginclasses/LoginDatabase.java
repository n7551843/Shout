package hilldl.org.example.shout.loginclasses;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import hilldl.org.example.shout.R;
import hilldl.org.example.shout.RetrieveData;
import hilldl.org.example.shout.entities.User;

enum LoginStatus {IDLE, PENDING, SUCCESSFUL, FAILED}
enum RegistrationStatus {IDLE, PENDING, SUCCESSFUL, FAILED}

public class LoginDatabase {

    private static final String TAG = "LoginDatabase";
    private static FirebaseDatabase db;
    private static FirebaseAuth mAuth;
    private MutableLiveData<LoginStatus> mAuthenticationStatus;
    private MutableLiveData<RegistrationStatus> mRegistrationStatus;
    private Exception mAuthenticationException;

    public static final String ALLPOSTS = "AllPosts";
    public static final String ALLUSERS = "AllUsers";

    private static LoginDatabase instance;

    private LoginDatabase() {
        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuthenticationStatus = new MutableLiveData<LoginStatus>(LoginStatus.IDLE);
        mRegistrationStatus = new MutableLiveData<RegistrationStatus>(RegistrationStatus.IDLE);
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

    public void registerEmailWithFirebase(String email, String password, String nickname) {
        new FireBaseRegistrationAsyncTask(email, password, nickname).execute();
    }

    public MutableLiveData<LoginStatus> getAuthenticationStatus() {
        return mAuthenticationStatus;
    }

    public Exception getAuthenticationException() {
        return mAuthenticationException;
    }

    public MutableLiveData<RegistrationStatus> getRegistrationStatus() {
        return mRegistrationStatus;
    }

    /**
     * Method called by FirebaseRegistrationAsyncTask if it receives confirmation
     * of a successful user registration.  This method triggers another AsyncTask to store
     * the user's data on the database itself.
     * @param user - the User.class object holding the user's details, to be saved on the database
     *             itself.
     */

    private void saveUserOnSuccessfulRegistration(User user) {
        new SaveUserAsyncTask(user).execute();
    }

    /**
     * Internal class called by signInWithEmailAndPassword() method
     * to complete the sign in authentication process on a background thread.
     */

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
                mAuthenticationStatus.postValue(LoginStatus.PENDING);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onComplete: signInWithEmailAndPassword() complete");
                                if (task.isSuccessful()) {
                                    // Inform LoginViewModel of successful authentication
                                    // authentication
                                    mAuthenticationStatus.postValue(LoginStatus.SUCCESSFUL);
                                } else {
                                    // Inform LoginViewModel of failed sign in
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    mAuthenticationException = task.getException();
                                    mAuthenticationStatus.postValue(LoginStatus.FAILED);
                                }
                            }
                        });
            } else {
                Log.d(TAG, "doInBackground: no firebase authority");
                mAuthenticationStatus.postValue(LoginStatus.FAILED);
            }
            Log.d(TAG, "FirebaseSignInAsyncTask doInBackground: ends");
            return null;
        }
    }

    /**
     * Internal class called to Register the user with Firebase on a background thread.
     * If successful, will call saveUserOnSuccessfulRegistration(), which will in turn set
     * off another AsyncTask call for saving all the user's details in Shout database.
     *
     * Only when that task returns successful as well will the ViewModel be informed that
     * the task has been successful (and the app can navigate to the next activity).  This
     * has been done because MainActivity has an initialisation call retrieving the user's
     * details off the Realtime Database.
     */

    private class FireBaseRegistrationAsyncTask extends AsyncTask<Void, Void, Void> {

        private String email;
        private String password;
        private String nickname;

        public FireBaseRegistrationAsyncTask(String email, String password, String nickname) {
            this.email = email;
            this.password = password;
            this.nickname = nickname;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mRegistrationStatus.postValue(RegistrationStatus.PENDING);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Registration success, Create new User on db with
                                // the User's UID
                                Log.d(TAG, "createUserWithEmail:success");

                                // Step 1 - Get all user info and put into a User.class object
                                User user = new User();
                                user.setEmail(email);
                                user.setNickname(nickname);
                                user.setPassword(password);
                                saveUserOnSuccessfulRegistration(user);

                            } else {
                                Log.d(TAG, "onComplete: createUserWithEmail:failure", task.getException());
                                mRegistrationStatus.postValue(RegistrationStatus.FAILED);
                            }
                        }
                    });
            return null;
        }
    }

    private class SaveUserAsyncTask extends AsyncTask<Void, Void, Void> {
        private User mUser;

        public SaveUserAsyncTask(User user) { mUser = user; }

        @Override
        protected Void doInBackground(Void... voids) {
            String userID = mAuth.getCurrentUser().getUid();
            mUser.setUserID(userID);
            db.getReference().child(ALLUSERS).child(userID).setValue(mUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRegistrationStatus.postValue(RegistrationStatus.SUCCESSFUL);
                        }
                    });
            return null;
        }
    }
}
