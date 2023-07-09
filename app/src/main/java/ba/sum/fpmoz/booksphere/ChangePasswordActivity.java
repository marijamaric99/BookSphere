package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    ImageButton backBtn;
    private String currentPsw, newPsw, repNewPsw;
    private static final String TAG = "CHANGE_PASSWORD";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_change_password);

        FirebaseUser currentUser  = mAuth.getCurrentUser();
        EditText currentPswTxt = findViewById(R.id.currentPswTxt);
        EditText newPswTxt = findViewById(R.id.newPswTxt);
        EditText repNewPswTxt = findViewById(R.id.repNewPswTxt);
        Button submitBtn = findViewById(R.id.submitBtn);

        if(currentUser == null){
            Intent i = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
            startActivity(i);
        }else{
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPsw = currentPswTxt.getText().toString();
                    newPsw = newPswTxt.getText().toString();
                    repNewPsw = repNewPswTxt.getText().toString();
                    if(currentPsw.equals("")){
                        Toast.makeText(getApplicationContext(), "Molimo unesite trenutnu lozinku", Toast.LENGTH_SHORT).show();
                    }else if (newPsw.equals("") || repNewPsw.equals("")){
                        Toast.makeText(getApplicationContext(), "Molimo unesite novu lozinku", Toast.LENGTH_SHORT).show();
                    }else if(!newPsw.equals(repNewPsw)){
                        Toast.makeText(getApplicationContext(), "Lozinke se ne podudaraju", Toast.LENGTH_SHORT).show();
                    }else if(currentPsw.equals(newPsw)){
                        Toast.makeText(getApplicationContext(), "Stara lozinka i nova su iste", Toast.LENGTH_SHORT).show();
                    }else{
                        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPsw);

                        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    currentUser.updatePassword(newPsw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "Uspješno ste promijenili lozinku", Toast.LENGTH_SHORT).show();
                                                currentPswTxt.setText("");
                                                newPswTxt.setText("");
                                                repNewPswTxt.setText("");
                                            }else{
                                                try{
                                                    throw task.getException();
                                                }catch(Exception e){
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(getApplicationContext(), "Niste unijeli ispravnu lozinku", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ne radi ispravno
                //onBackPressed();
                Intent i = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }
}