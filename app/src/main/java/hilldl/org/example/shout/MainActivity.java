package hilldl.org.example.shout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hilldl.org.example.shout.ui.main.MainFragment1;
import hilldl.org.example.shout.ui.main.MainFragment2;
import hilldl.org.example.shout.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starts");
        setContentView(R.layout.main_activity);

        new ViewModelProvider(this).get(MainViewModel.class);


        FloatingActionButton fab = findViewById(R.id.fab);
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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment1.newInstance())
                    .commitNow();
        }



    }

}