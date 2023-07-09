package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.BreakIterator;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailTxt;
    private Button buttonPswReset;
    private ProgressBar progressBar;

    private FirebaseAuth authProfile;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static final String TAG = "RESET_PSW";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText emailTxt = findViewById(R.id.emailTxt);
        Button buttonPswReset = findViewById(R.id.buttonPswReset);

        buttonPswReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTxt.getText().toString();

                Patterns Patters;
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Morate unijeti email adresu",Toast.LENGTH_LONG).show();
                    emailTxt.setError("Morate unijeti email adresu");
                    emailTxt.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getApplicationContext(),"Netočan email",Toast.LENGTH_SHORT).show();
                }
                else{
                    resetPassword(email);
                }
            }
        });

    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Molimo provjerite mail o promjeni lozinke",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Nešto nije u redu!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    };
}