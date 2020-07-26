package hilldl.org.example.shout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import hilldl.org.example.shout.ui.main.MainFragment1;
import hilldl.org.example.shout.ui.main.MainFragment2;
import hilldl.org.example.shout.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity implements MainFragment1.Callback {
    private static final String TAG = "MainActivity";
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.main_activity);

        new ViewModelProvider(this).get(MainViewModel.class);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO on Fab Click, open 'post new' Fragment

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MainFragment2.newInstance())
                        .commitNow();
            }
        });

        if (savedInstanceState == null) {
            loadFirstFragment();
        }
    }

    public void hideFab() {
        fab.setVisibility(View.GONE);
    }

    public void showFab() {
        fab.setVisibility(View.VISIBLE);
    }

    private void loadFirstFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment1.newInstance())
                .commitNow();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof MainFragment2) {
            loadFirstFragment();
        } else {
//            Snackbar snackbar = Snackbar.make(this, "Are you sure you want to return to the Login Page", Snackbar.LENGTH_INDEFINITE).show();
            super.onBackPressed();
        }
    }
}