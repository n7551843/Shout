package hilldl.org.example.shout.loginclasses;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginStatus> mLoginStatus;
    private MutableLiveData<RegistrationStatus> mRegistrationStatus;
    private LoginDatabase mLoginDatabase;

    public LoginViewModel() {
        mLoginDatabase = LoginDatabase.getInstance();
        mLoginStatus = mLoginDatabase.getAuthenticationStatus();
        mRegistrationStatus = mLoginDatabase.getRegistrationStatus();
    }

    public void signInWithEmailAndPassword (String email, String password) {
        mLoginDatabase.signInWithEmailAndPassword(email, password);
    }

    public void registerEmailWithFirebase(String email, String password, String nickname) {
        mLoginDatabase.registerEmailWithFirebase(email, password, nickname);
    }

    public MutableLiveData<LoginStatus> getAuthenticationStatus() {
        return mLoginStatus;
    }

    public Exception getAuthenticationException() {
        return mLoginDatabase.getAuthenticationException();
    }

    public MutableLiveData<RegistrationStatus> getRegistrationStatus() {
        return mRegistrationStatus;
    }
}
