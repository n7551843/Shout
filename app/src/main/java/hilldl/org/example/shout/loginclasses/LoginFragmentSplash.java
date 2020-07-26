package hilldl.org.example.shout.loginclasses;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import hilldl.org.example.shout.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragmentSplash#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragmentSplash extends Fragment {

    public static final int LOAD_DELAY = 3000;
    CardView cv;
    ImageButton button;

    public LoginFragmentSplash() {
        // Required empty public constructor
    }


    public static LoginFragmentSplash newInstance(String param1, String param2) {
        LoginFragmentSplash fragment = new LoginFragmentSplash();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment_splash, container, false);
        cv = view.findViewById(R.id.initialise_cardview);
        button = view.findViewById(R.id.loading_logo_button);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        Animation rotate = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate);
        cv.startAnimation(rotate);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NavHostFragment.findNavController(LoginFragmentSplash.this)
                        .navigate(R.id.action_loginFragmentInitialize_to_fragment_login_second);
            }
        }, LOAD_DELAY);
    }
}