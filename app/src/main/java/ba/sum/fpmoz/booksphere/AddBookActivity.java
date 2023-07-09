package ba.sum.fpmoz.booksphere;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ba.sum.fpmoz.booksphere.databinding.ActivityAddBookBinding;
import kotlin.jvm.Synchronized;


public class AddBookActivity extends AppCompatActivity {

    private ActivityAddBookBinding binding;

    ImageButton backBtn, attachPdf, attachImg;
    Button submitBtn;

    private String title = "", description = "", author = "", categoryId = "";

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private Uri pdfUri = null;

    private Uri imgUri = null;

    private static final int PDF_PICK_CODE = 1000;
    private static final int IMG_PICK_CODE = 1001;

    private static final String TAG ="ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getSupportActionBar().hide();*/

        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference reference= database.getReference("Categories");
        List<String> idList= new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String id = snapshot.getKey();
                    String category = snapshot.child("Categories").getValue(String.class);
                    String idAndCategory = id;
                    idList.add(idAndCategory);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, idList);
                AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.categoryInputTxt);
                textView.setAdapter(adapter);

                textView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,long id){
                        String selectCategories = (String) parent.getItemAtPosition(position);
                        textView.setText(selectCategories);
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String categoryId1 = getIntent().getStringExtra("categoryId");

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Molimo pričekajte");
        progressDialog.setCanceledOnTouchOutside(false);

        //Povrat na prošlu stranicu
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ne radi ispravno
                //onBackPressed();
                Intent i = new Intent(AddBookActivity.this, BooksActivity.class);
                startActivity(i);
            }
        });

        //Dohvat pdf dokumenta
        attachPdf = (ImageButton) findViewById(R.id.attachPdf);
        attachPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        //Dohvat slike
        attachImg = (ImageButton) findViewById(R.id.attachImg);
        attachImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPickIntent();
            }
        });

        submitBtn =  findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    //Radi
    private void validateData(){
        Log.d(TAG, "validateData: validating data...");

        title = binding.titleInputText.getText().toString().trim();
        description = binding.descriptionInputText.getText().toString().trim();
        author = binding.authorInputText.getText().toString().trim();
        categoryId = binding.categoryInputTxt.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Unesite naslov...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "validateData: title prazan");
        }else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Unesite opis...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(author)){
            Toast.makeText(this, "Unesite autora...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(categoryId)){
            Toast.makeText(this, "Unesite kategoriju...", Toast.LENGTH_SHORT).show();
        }
        else if(pdfUri == null){
            Toast.makeText(this, "Odaberite pdf...", Toast.LENGTH_SHORT).show();
        }else if(imgUri == null) {
            Toast.makeText(this, "Odaberite sliku...", Toast.LENGTH_SHORT).show();
        }else{
            uploadFilesToStorage();
        }
    }

    private void uploadFilesToStorage() {
        Log.d(TAG, "uploadPdfToStorage: dodavanje u pohranu");

        progressDialog.setMessage("Dodavanje PDFa..");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        String filePathAndName1 = "Books/files/" + timestamp;
        String filePathAndName2 = "Books/images/" + timestamp;

        final String[] uploadedPdfUrl = new String[1];
        final String[] uploadedImgUrl = new String[1];

        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference(filePathAndName1);
        StorageReference storageReference2 = FirebaseStorage.getInstance().getReference(filePathAndName2);
        storageReference1.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: uspješno dodan pdf");
                Log.d(TAG, "onSuccess: dohvat pdf urla");

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                uploadedPdfUrl[0] = ""+uriTask.getResult();
                if(uploadedPdfUrl[0] != null && uploadedImgUrl[0] != null){
                    uploadDataToDb(uploadedPdfUrl[0], uploadedImgUrl[0], title);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailture: greška pri dodavanju pdf " + e.getMessage());
                Toast.makeText(AddBookActivity.this, "Dodavanje PDFa greška " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        storageReference2.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: uspješno dodana slika");
                Log.d(TAG, "onSuccess: dohvat urla slike");

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                uploadedImgUrl[0] = ""+uriTask.getResult();
                if(uploadedPdfUrl[0] != null && uploadedImgUrl[0] != null){
                    uploadDataToDb(uploadedPdfUrl[0], uploadedImgUrl[0], title);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailture: greška pri dodavanju slike " + e.getMessage());
                Toast.makeText(AddBookActivity.this, "Dodavanje slike greška " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Radi
    private void uploadDataToDb(String uploadedPdfUrl, String uploadedImgUrl, String timestamp) {
        Log.d(TAG, "uploadPdfToStorage: dodavanje u bazu");

        progressDialog.setMessage("Dodavanje informacija o pdfu i slici");

        String uid = firebaseAuth.getUid();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+title);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("author", ""+author);
        hashMap.put("url", ""+uploadedPdfUrl);
        hashMap.put("image", ""+uploadedImgUrl);
        hashMap.put("timestamp", ""+title);
        hashMap.put("categoryId", "" + categoryId);
        hashMap.put("saved", false);


        DatabaseReference ref = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Uspješno dodavanje u bazu ");
                        Toast.makeText(AddBookActivity.this, "Uspješno dodavanje u bazu ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailture: Neuspješno dodavanje u bazu " + e.getMessage());
                        Toast.makeText(AddBookActivity.this, "Neuspješno dodavanje u bazu " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Radi
    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: početak odabira pdf dokumta");
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Odaberite PDF"), PDF_PICK_CODE);
    }

    //Radi
    private void imgPickIntent() {
        Log.d(TAG, "imgPickIntent: početak odabira slike");
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Odaberite sliku"), IMG_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PDF_PICK_CODE){
                Log.d(TAG, "onActivityResult: PDF odabran");

                pdfUri = data.getData();

                Log.d(TAG, "onActivityResult: URIPDF: " +pdfUri);
            }
            if(requestCode == IMG_PICK_CODE){
                Log.d(TAG, "onActivityResult: slika odabran");

                imgUri = data.getData();

                Log.d(TAG, "onActivityResult: URIIMG: " +imgUri);
            }
        } else{
            Log.d(TAG, "onActivityResult: zatvaranje odabira");
            Toast.makeText(this, "zatvaranje odabira ", Toast.LENGTH_SHORT).show();
        }
    }
}