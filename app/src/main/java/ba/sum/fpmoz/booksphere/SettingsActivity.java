package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import ba.sum.fpmoz.booksphere.model.UserProfile;


public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView viewLl, fullnameTv, emailTv, phoneTv;
    Button editProfile, changePassword, logoutBtn;
    FirebaseAuth mAuth;
    ShapeableImageView profImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.settings);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");


        editProfile = findViewById(R.id.editProfile);
        changePassword = findViewById(R.id.changePassword);
        logoutBtn = findViewById(R.id.logoutBtn);

        viewLl = findViewById(R.id.viewLl);
        fullnameTv = findViewById(R.id.fullnameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        profImg = findViewById(R.id.profImg);

        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setStrokeWidth(4f);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        profImg.setImageDrawable(shapeDrawable);



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            DatabaseReference profileRef = mDatabase.getReference("Profile").child(currentUser.getUid());
            profileRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    UserProfile profileUser = task.getResult().getValue(UserProfile.class);
                    if(profileUser != null){
                        viewLl.setText(profileUser.getFullNameTxt());
                        fullnameTv.setText(profileUser.getFullNameTxt());
                        emailTv.setText(profileUser.getEmail());
                        phoneTv.setText(profileUser.getPhone());
                        Picasso.get()
                                .load(profileUser.getImage())
                                .into(profImg);
                    }
                }
            });
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfileActivity = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(editProfileActivity);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changePasswordActivity = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(changePasswordActivity);
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.books:
                        startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bookmark:
                        startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user =mAuth.getCurrentUser();
        if(user != null){

        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}