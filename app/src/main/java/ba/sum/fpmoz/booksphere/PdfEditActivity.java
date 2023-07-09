package ba.sum.fpmoz.booksphere;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ba.sum.fpmoz.booksphere.databinding.ActivityPdfEditBinding;
import ba.sum.fpmoz.booksphere.databinding.ActivityPdfEditBinding;

public class PdfEditActivity extends AppCompatActivity {

    private ActivityPdfEditBinding binding;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");

    private String bookId;

    private ProgressDialog progressDialog;

    private static final String TAG = "BOOK_EDIT_TAG";
    private Log log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getSupportActionBar().hide();*/

        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button submitBtn = findViewById(R.id.submitBtn);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(PdfEditActivity.this, BooksActivity.class)
                );}
        });

        bookId = getIntent().getStringExtra("bookId");
        log.d(TAG, "bookId: " + bookId);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Molimo pričekajte");
        progressDialog.setCanceledOnTouchOutside(false);

        loadBookInfo();


        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                startActivity( new Intent(PdfEditActivity.this, BooksActivity.class));
            }
        });


    }

    private void loadBookInfo() {
        log.d(TAG,"loadBookInfo:Pričekajte..");

        DatabaseReference refBooks = mDatabase.getReference("Books");
        refBooks.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String description=""+snapshot.child("description").getValue();
                        String title=""+snapshot.child("title").getValue();
                        String author=""+snapshot.child("author").getValue();

                        binding.titleEt.setText(title);
                        binding.descriptionEt.setText(description);
                        binding.authorTv.setText(author);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String title="",description="",author="";
    private void validateData(){
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        author = binding.authorTv.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Unesite naslov..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Unesite opis..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(author)){
            Toast.makeText(this, "Unesite autora", Toast.LENGTH_SHORT).show();
        }
        else{
            updateBook();
        }

    }

    private void updateBook() {
        Log.d(TAG, "updatePdf: Ažuriranje pdf u bazu...");
        progressDialog.setMessage("Ažuriranje knjige...");
        progressDialog.show();

        HashMap<String,Object >hashMap=new HashMap<>();
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("author",""+author);

        DatabaseReference ref= mDatabase.getReference("Books");
        ref.child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Ažuriranje knjige..");
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "Ažuriranje..", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure:Ažuriranje nije uspješno"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}