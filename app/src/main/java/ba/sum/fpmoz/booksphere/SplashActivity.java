package ba.sum.fpmoz.booksphere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private Timer splashTimer;
    private static final long DELAY=3000;
    private boolean scheduled = false;
    Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                        if(currentUser != null){
                            startActivity(new Intent(SplashActivity.this, HomepageActivity.class));
                        }else{
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
            }
        },2000);

    }
}