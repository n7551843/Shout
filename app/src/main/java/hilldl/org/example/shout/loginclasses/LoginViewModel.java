package hilldl.org.example.shout.loginclasses;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginStatus> mLoginStatus;
    private LoginDatabase mLoginDatabase;

    public LoginViewModel() {
        mLoginDatabase = LoginDatabase.getInstance();
        mLoginStatus = mLoginDatabase.getAuthenticationStatus();
    }

    public void signInWithEmailAndPassword (String email, String password) {
        mLoginDatabase.signInWithEmailAndPassword(email, password);
    }

    public MutableLiveData<LoginStatus> getAuthenticationStatus() {
        return mLoginStatus;
    }

    public Exception getLoginException () {
        return mLoginDatabase.getLoginException();
    }
}
