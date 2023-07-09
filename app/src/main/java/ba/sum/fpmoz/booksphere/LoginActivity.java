package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        EditText loginEmailTxt = findViewById(R.id.loginEmailTxt);
        EditText loginPasswordTxt = findViewById(R.id.loginPasswordTxt);
        TextView forgotPsw = findViewById(R.id.forgotPsw);
        TextView registrationTxt = findViewById(R.id.noAccount);

        forgotPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        registrationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmailTxt.getText().toString();
                String password = loginPasswordTxt.getText().toString();
                if(email.equals("") && password.equals("")){
                    Toast.makeText(getApplicationContext(), "Morate unijeti sva polja", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Dobrodošli u BookSphere!", Toast.LENGTH_SHORT).show();
                                loginEmailTxt.setText("");
                                loginPasswordTxt.setText("");
                                Intent mainIntent = new Intent(LoginActivity.this, HomepageActivity.class);
                                startActivity(mainIntent);
                            }else{
                                Toast.makeText(getApplicationContext(), "Pogrešni korisnički podaci", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}