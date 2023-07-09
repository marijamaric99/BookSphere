package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ba.sum.fpmoz.booksphere.databinding.ActivityCategoryAddBinding;


public class CategoryAddActivity extends AppCompatActivity {

    private ActivityCategoryAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Molimo pročekajte");
        progressDialog.setCanceledOnTouchOutside(false);
        ImageButton backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backBtnIntent = new Intent(CategoryAddActivity.this, CategoryActivity.class);
                startActivity(backBtnIntent);
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    public String category = "";
    private void validateData() {
        category = binding. categoryEt.getText().toString().trim();
        if(TextUtils.isEmpty(category)){
            Toast.makeText(this,"Molimo unesite kategoriju...", Toast.LENGTH_SHORT).show();
        }
        else {
            addCategoryFirebase();
        }

    }


    private void addCategoryFirebase() {
        progressDialog.setMessage("Dodajem kategoriju...");
        progressDialog.show();
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+category);
        hashMap.put("category", ""+category);
        hashMap.put("timestamp", category);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app").getReference("Categories");
        ref.child(""+category)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, "Kategorija je uspješno dodana...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}