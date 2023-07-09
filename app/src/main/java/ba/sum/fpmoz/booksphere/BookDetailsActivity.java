package ba.sum.fpmoz.booksphere;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;

import ba.sum.fpmoz.booksphere.adapter.CommentAdapter;
import ba.sum.fpmoz.booksphere.databinding.ActivityBookDetailsBinding;
import ba.sum.fpmoz.booksphere.databinding.DialogCommentAddBinding;
import ba.sum.fpmoz.booksphere.model.Comments;
import ba.sum.fpmoz.booksphere.model.UserProfile;

public class BookDetailsActivity extends AppCompatActivity {
    String bookId, urlPdf, titleBook;
    ImageButton backBtn, addComment;
    TextView titleTv, descriptionTv, authorTv, dateTv, sizeTv;
    ImageView imageIv;
    Button downloadBookBtn, readBookBtn;
    private ActivityBookDetailsBinding binding;

    public static final String TAG = "BOOK_DETAILS";
    private static final String TAG_DOWNLOAD = "TAG_DOWNLOAD";
    private static final String TAG_READ = "TAG_READ";

    private ProgressDialog progressDialog;
    private ArrayList<Comments> commentsArrayList;
    private CommentAdapter commentAdapter;
    RecyclerView commentsRv;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Pričekajte...");
        progressDialog.setCanceledOnTouchOutside(false);

        setContentView(R.layout.activity_book_details);
        downloadBookBtn = findViewById(R.id.downloadBookBtn);
        downloadBookBtn.setVisibility(View.GONE);

        readBookBtn = findViewById(R.id.readBookBtn);

        titleTv = findViewById(R.id.titleTv);
        descriptionTv = findViewById(R.id.descriptionTv);
        authorTv = findViewById(R.id.authorTv);
        dateTv = findViewById(R.id.dateTv);
        sizeTv = findViewById(R.id.sizeTv);
        imageIv = findViewById(R.id.imageIv);
        commentsRv = findViewById(R.id.commentsRv);


        Intent intent=getIntent();
        bookId= intent.getStringExtra("bookId");

        //ove dvi metode
        loadBookDetails();
        loadComments();

        //ovo
        downloadBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_DOWNLOAD, "onclick za preuziamnje:");
                Toast.makeText(BookDetailsActivity.this, "Preuzimanje...", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPdf));
                startActivity(intent);

            }
        });

        //ovo
        readBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_READ, "onclick za čitanje pdf-a:");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(urlPdf), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Intent newIntent = Intent.createChooser(intent, "Open File");
                startActivity(newIntent);
            }
        });

        backBtn =  findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BookDetailsActivity.this, BooksActivity.class);
                startActivity(i);
            }
        });
        //metoda addcomentdialog
        addComment = findViewById(R.id.addComment);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentDialog();
            }
        });
    }


    //ovo
   private void loadComments() {
       commentsArrayList = new ArrayList<>();
       DatabaseReference reference = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Books");
       DatabaseReference bookReference = reference.child(bookId).child("comment");

       bookReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               commentsArrayList.clear();
               for (DataSnapshot ds : snapshot.getChildren()) {
                   Comments comment = ds.getValue(Comments.class);
                   commentsArrayList.add(comment);
               }
               commentAdapter = new CommentAdapter(BookDetailsActivity.this, commentsArrayList);
               commentsRv.setAdapter(commentAdapter);
               commentsRv.setLayoutManager(new LinearLayoutManager(BookDetailsActivity.this));

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
   }




    private String comment = "" , name ="", image= "";
    private void addCommentDialog() {
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = commentAddBinding.commentEt.getText().toString().trim();
                if(TextUtils.isEmpty(comment)){
                    Toast.makeText(BookDetailsActivity.this, "Dodajte svoj komentar...", Toast.LENGTH_SHORT).show();

                }else {

                    addComment();
                }
            }
        });
    }
    //nastavi na onu gori
    private void addComment() {
        progressDialog.setMessage("Dodajem komentar...");
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+bookId);
        hashMap.put("bookId", ""+bookId);
        hashMap.put("timestamp", ""+bookId);
        hashMap.put("comment", ""+comment);
      //  hashMap.put("uid", ""+firebaseAuth.getUid());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference profileReference = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Profile")
                    .child(user.getUid());

            profileReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        String name = userProfile.getFullNameTxt();
                        String image = userProfile.getImage();


                        hashMap.put("name", name);
                        hashMap.put("image",image);

                        DatabaseReference reference = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/")
                                .getReference("Books");
                        reference.child(bookId).child("comment").child(timestamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(BookDetailsActivity.this,"Uspješno dodavanje komentara",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(BookDetailsActivity.this,"Neuspješno dodavanje komentara"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Upravljanje greškama ako je dohvat podataka profila neuspješan
                    Toast.makeText(BookDetailsActivity.this,"Greška pri dohvaćanju profila",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




    //obavezno
    private void loadBookDetails() {
        DatabaseReference ref= mDatabase.getReference("Books");
        ref.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                titleBook=""+snapshot.child("title").getValue();
                String author=""+snapshot.child("author").getValue();
                String description=""+snapshot.child("description").getValue();
                urlPdf=""+snapshot.child("url").getValue();
                String image=""+snapshot.child("image").getValue();
                String timestamp=""+snapshot.child("timestamp").getValue();
                downloadBookBtn.setVisibility(View.VISIBLE);

               // String date = MyApplication.formatTimestamp(timestamp);
                loadPdfSize(urlPdf, sizeTv);
                titleTv.setText(titleBook);
                descriptionTv.setText(description);
                authorTv.setText(author);
              //  dateTv.setText(date);
                Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .into(imageIv);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //ovde sve
    private void loadPdfSize(String pdfUrl, TextView sizeTv) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();

                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if(mb >=1){
                            sizeTv.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if(kb >=1){
                            sizeTv.setText(String.format("%.2f", kb)+" KB");
                        }
                        else{
                            sizeTv.setText(String.format("%.2f", bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }



}
