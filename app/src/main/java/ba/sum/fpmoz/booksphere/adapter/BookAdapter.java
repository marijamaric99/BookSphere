package ba.sum.fpmoz.booksphere.adapter;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import ba.sum.fpmoz.booksphere.BookDetailsActivity;

import ba.sum.fpmoz.booksphere.PdfEditActivity;
import ba.sum.fpmoz.booksphere.R;
import ba.sum.fpmoz.booksphere.model.Book;



public class BookAdapter extends FirebaseRecyclerAdapter<Book, BookAdapter.BookViewHolder>{

    Context context;
    public static final String TAG = "BOOK_ADAPTER";
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ProgressDialog progressDialog;

    public BookAdapter(@NonNull FirebaseRecyclerOptions <Book> options, ProgressDialog progressDialog) {
        super(options);
        this.progressDialog = progressDialog;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_view, parent, false);
        return new BookAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
        String title = model.getTitle();
        String author = model.getAuthor();
        String description = model.getDescription();
        String date = model.getTimestamp();
        String timestamp = model.getTimestamp();
        String image = model.getImage();
        String categoryId = model.getCategoryId();
        Boolean saved = model.isSaved();
        String uid = model.getUid();
        holder.bind(model);




        holder.titleTv.setText(title);
        holder.authorTv.setText(author);
        holder.descriptionTv.setText(description);
        Picasso
                .get()
                .load(image)
                .into(holder.imageIv);



        loadPdfSize(model, holder);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser  = mAuth.getCurrentUser();
                if(isEmailValid(currentUser.getEmail())){
                    moreOptionsDialog(model, holder);
                }
            }
        });

        //ovo opisi da je u adapteru i da se onda izlistava ono detalji
        holder.titleTv.setOnClickListener(new View.OnClickListener() {
            String bookId = model.getTimestamp();
            @Override
            public void onClick(View view) {
                Log.d(TAG, "titleBtn:id: " + bookId);
                Intent intent = new Intent(context, BookDetailsActivity.class);
                intent.putExtra("bookId", bookId);
                context.startActivity(intent);
            }
        });

    }

    //navedi da postoji mogućnost

    private void moreOptionsDialog(Book model, BookViewHolder holder) {
        String bookId = model.getTimestamp();

        // Opcije koje će se prikazivati u dijalogu.
        String[] options = {"Uredi", "Izbriši",};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Odaberite opciju").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(which==0){
                    Intent intent = new Intent(context, PdfEditActivity.class);
                    intent.putExtra("bookId", bookId);
                    context.startActivity(intent);

                }else if(which==1){
                    deleteBook(model, holder);
                }
            }
        }).show();
    }


    private void deleteBook(Book model, BookViewHolder holder) {
        Log.d(TAG, "onDelete:uspješno učitavanje");
        String bookURL = model.getUrl();
        String bookTimetamp = model.getTimestamp();
        String bookImage = model.getImage();
        String bookTitle = model.getTitle();

        Log.d(TAG, "deleteBook: Deleting...");

        StorageReference storageReferencePdf = FirebaseStorage.getInstance().getReferenceFromUrl(bookURL);
        StorageReference storageReferenceImage = FirebaseStorage.getInstance().getReferenceFromUrl(bookImage);
        storageReferencePdf.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Deleted from storage");
                Log.d(TAG, "onSuccess: Now deleting from db");

                DatabaseReference reference = mDatabase.getReference("Books");
                Log.d(TAG, "reference:" + reference.child("gg").getParent());
                reference.child(bookTimetamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from db too");
                        Toast.makeText(context, "Knjiga uspješno izbrisana", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from db due to"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to delete pdf file from storage due to"+e.getMessage());
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        storageReferenceImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Deleted from storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to delete image from storage due to"+e.getMessage());
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPdfSize(Book model, BookViewHolder holder) {

        String pdfUrl = model.getUrl();

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSuccess: " + model.getTitle() + " " + bytes);

                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if(mb >=1){
                            holder.sizeTv.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if(kb >=1){
                            holder.sizeTv.setText(String.format("%.2f", kb)+" KB");
                        }
                        else{
                            holder.sizeTv.setText(String.format("%.2f", bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    class BookViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv, authorTv, descriptionTv, dateTv, sizeTv;
        ImageView imageIv;
        ImageButton moreBtn, savedBtn;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            authorTv = itemView.findViewById(R.id.authorTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            imageIv = itemView.findViewById(R.id.imageIv);
            dateTv = itemView.findViewById(R.id.dateTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            savedBtn = itemView.findViewById(R.id.savedBtn);
        }
        public  void  bind (Book book){
            if(book.isSaved()){
                savedBtn.setImageResource(R.drawable.ic_baseline_bookmark_24);
            }else {
                savedBtn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
            }
            savedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Books").child(book.getId());
                    if(book.isSaved()){
                        book.setSaved(false);
                        savedBtn.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                        mDatabase.child("saved").setValue(false);
                    }else {
                        book.setSaved(true);
                        savedBtn.setImageResource(R.drawable.ic_baseline_bookmark_24);
                        mDatabase.child("saved").setValue(true);
                    }
                }
            });
        }

    }

    //Validacija e-mail
    public boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "(fpmoz.sum.ba)$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}
