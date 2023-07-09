package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import ba.sum.fpmoz.booksphere.model.UserProfile;

public class EditProfileActivity extends AppCompatActivity {
    ImageButton backBtn;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");

    ImageButton userImg;
    ShapeableImageView profImg;

    private Uri imgUri = null;
    private static final int IMG_PICK_CODE= 1000;
    private static final String TAG ="ADD_IMG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        EditText fullnameTxt = findViewById(R.id.fullnameTxt);
        EditText emailTxt = findViewById(R.id.emailTxt);
        EditText phoneTxt = findViewById(R.id.phoneTxt);
        Button submitBtn = findViewById(R.id.submitBtn);
        ImageButton userImg = findViewById(R.id.userImg);
        profImg = findViewById(R.id.profImg);

        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setStrokeWidth(4f);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        profImg.setImageDrawable(shapeDrawable);

        if(currentUser != null){
            DatabaseReference profileRef = mDatabase.getReference("Profile").child(currentUser.getUid());

            profileRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    UserProfile userProfile= task.getResult().getValue(UserProfile.class);
                    if(userProfile != null){
                        fullnameTxt.setText(userProfile.getFullNameTxt());
                        emailTxt.setText(userProfile.getEmail());
                        phoneTxt.setText(userProfile.getPhone());
                        Picasso.get()
                                .load(userProfile.getImage())
                                .into(profImg);
                    }
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String fullname = fullnameTxt.getText().toString();
                    String email = emailTxt.getText().toString();
                    String phone = phoneTxt.getText().toString();
                    if (imgUri != null){
                        ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
                        progressDialog.setTitle("Update");
                        progressDialog.show();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile/files/" + mAuth.getCurrentUser().getEmail());
                        storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String image = uri.toString();

                                                UserProfile userProfile = new UserProfile(fullname, email, phone, image);
                                                profileRef.setValue(userProfile);

                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Uspješno ste promjenili podatke!", Toast.LENGTH_SHORT);
                                                startActivity(new Intent(EditProfileActivity.this, SettingsActivity.class));
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške prilikom spremanja slike!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        String image = userImg.toString();
                        UserProfile userProfile = new UserProfile(fullname, email, phone, image);
                        profileRef.setValue(userProfile);
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfileActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_PICK_CODE);
            }
        });


    }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMG_PICK_CODE && data !=null) {
            if (requestCode == IMG_PICK_CODE) {
                Log.d(TAG, "onActivityResult:Slika odabrana");

                imgUri = data.getData();
                //  addimg.setImageURI(imgUri);

                Log.d(TAG, "onActivityResult: URIIMG: " + imgUri);
            }
        } else {
            Log.d(TAG, "onActivityResult: zatvaranje odabira");
            Toast.makeText(this, "zatvaranje odabira ", Toast.LENGTH_SHORT).show();
        }
    }
}