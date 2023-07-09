package ba.sum.fpmoz.booksphere;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import ba.sum.fpmoz.booksphere.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {


    private ActivityRegisterBinding binding;
    ImageButton userImg;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private String fullNameTxt = "", phoneTxt = "", registerEmailTxt = "", registerPasswordTxt = "", registerPasswordCnfTxt = "";

    private ProgressDialog progressDialog;
    private Uri imgUri = null;
    private static final int IMG_PICK_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Profile");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Pričekajte...");
        progressDialog.setCanceledOnTouchOutside(false);

        TextView noAccount = findViewById(R.id.noAccount);


        noAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        userImg = (ImageButton) findViewById(R.id.userImg);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPickIntent();
            }
        });

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }
    private void imgPickIntent() {
        Log.d(TAG, "imgPickIntent: početak odabira slike");
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Odaberite sliku"), IMG_PICK_CODE);
    }

    private void validateData() {
        Log.d(TAG, "validateData: validating data...");

        fullNameTxt = binding.fullNameTxt.getText().toString().trim();
        phoneTxt = binding.phoneTxt.getText().toString().trim();
        registerEmailTxt = binding.registerEmailTxt.getText().toString().trim();
        registerPasswordTxt = binding.registerPasswordTxt.getText().toString().trim();
        registerPasswordCnfTxt = binding.registerPasswordCnfTxt.getText().toString().trim();

        if (TextUtils.isEmpty(fullNameTxt)) {
            Toast.makeText(this, "Unesite ime i prezime", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneTxt)) {
            Toast.makeText(this, "Unesite broj mobitela", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(registerEmailTxt)) {
            Toast.makeText(this, "Unesite email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(registerPasswordTxt)) {
            Toast.makeText(this, "Unesite lozinku", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(registerPasswordCnfTxt)) {
            Toast.makeText(this, "Ponovite lozinku", Toast.LENGTH_SHORT).show();
        } else if (imgUri == null) {
            Toast.makeText(this, "Odaberite sliku", Toast.LENGTH_SHORT).show();
        } else {
        mAuth.createUserWithEmailAndPassword(registerEmailTxt, registerPasswordTxt)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Verificiraj email", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Slanje emaila za verifikaciju nije uspjelo!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                uploadFilesToStorage();
                            }
                        }
                    });
        }
        }

        private void uploadFilesToStorage() {
            Log.d(TAG, "uploadImgToStorage: dodavanje u pohranu");
            progressDialog.setMessage("Dodavanje slike...");
            progressDialog.show();


            String filePathAndName = "Profile/files/" + registerEmailTxt;
            final String[] uploadedImgUrl = new String[1];
            StorageReference storageReference1 = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference1.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: uspješno dodana slika");
                    Log.d(TAG, "onSuccess: dohvat slike");
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    uploadedImgUrl[0] = "" + uriTask.getResult();
                    if (uploadedImgUrl != null) {
                        uploadDataToDb(uploadedImgUrl[0], registerEmailTxt);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d(TAG, "onFailture: greška pri dodavanju" + e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Greška u dodavanju!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void uploadDataToDb(String uploadedImgUrl, String registerEmailTxt) {

            Log.d(TAG, "uploadedImgToStorage: dodavanje u bazu");
            progressDialog.setMessage("Dodavanje inf");
            String uid = mAuth.getUid();
            HashMap<String, Object> hashMap = new HashMap<>();


            hashMap.put("email", "" + registerEmailTxt);
            hashMap.put("fullNameTxt", "" + fullNameTxt);
            hashMap.put("image", "" + uploadedImgUrl);
            hashMap.put("phone", "" + phoneTxt);
            hashMap.put("uid", "" + uid);

            DatabaseReference ref = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Profile");
            ref.child("" + uid).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            progressDialog.dismiss();
                            Log.d(TAG, "onSuccess: Uspješno dodavanje u bazu ");
                            Toast.makeText(RegisterActivity.this, "Uspješno dodavanje u bazu ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.d(TAG, "onFailture: Neuspješno dodavanje u bazu " + e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Neuspješno dodavanje u bazu " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                if (requestCode == IMG_PICK_CODE) {
                    Log.d(TAG, "onActivityResult:Slika odabrana");

                    imgUri = data.getData();

                    Log.d(TAG, "onActivityResult: URIIMG: " + imgUri);
                }
            } else {
                Log.d(TAG, "onActivityResult: zatvaranje odabira");
                Toast.makeText(this, "zatvaranje odabira ", Toast.LENGTH_SHORT).show();
            }
        }

    }