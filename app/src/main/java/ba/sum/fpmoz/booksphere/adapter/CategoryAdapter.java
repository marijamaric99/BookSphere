package ba.sum.fpmoz.booksphere.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ba.sum.fpmoz.booksphere.BooksActivity;
import ba.sum.fpmoz.booksphere.CategoryActivity;
import ba.sum.fpmoz.booksphere.CategoryAddActivity;
import ba.sum.fpmoz.booksphere.R;
import ba.sum.fpmoz.booksphere.model.Category;


public class CategoryAdapter extends FirebaseRecyclerAdapter<Category, CategoryAdapter.HolderCategory> {

    private Context context;
    public ArrayList<Category> categoryArrayList, filteredList;
    private ArrayList<Category> originalList;
    public static final String TAG = "CATEGORY_ADAPTER";
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;



    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options, Context context, ArrayList<Category> categoryArrayList, ArrayList<Category> filterList) {
        super(options);
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filteredList = categoryArrayList;
        this.originalList = new ArrayList<>(categoryArrayList);
    }

    public CategoryAdapter(FirebaseRecyclerOptions<Category> options) {
        super(options);

    }


    @NonNull
    @Override
    public CategoryAdapter.HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new CategoryAdapter.HolderCategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position, @NonNull Category model) {
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        String timestamp = model.getTimestamp();



        holder.categoryTv.setText(model.getCategory());
        holder.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "categoryId" + category);
                Intent intent = new Intent(context, BooksActivity.class);
                intent.putExtra("categoryId", category);
                context.startActivity(intent);
            }
        });

    }



    private void deleteBook(Category model, HolderCategory holder) {
        Log.d(TAG, "onDelete:uspješno učitavanje");
        String categoryId = model.getId();
        String categoryTimestamp = String.valueOf(model.getTimestamp());
        String categoryCategory = model.getCategory();
        String categoryUid = model.getUid();

        Log.d(TAG, "deleteBook: Deleting...");

        StorageReference storageReferencePdf = FirebaseStorage.getInstance().getReferenceFromUrl(categoryId);
        storageReferencePdf.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Deleted from storage");
                Log.d(TAG, "onSuccess: Now deleting from db");

                DatabaseReference reference = mDatabase.getReference("Categories");
                Log.d(TAG, "reference:" + reference.child("gg").getParent());
                reference.child(categoryTimestamp).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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



    }

    class HolderCategory extends RecyclerView.ViewHolder{

        TextView categoryTv;
        Menu search_menu;

        public HolderCategory(@NonNull View itemView){
            super(itemView);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            search_menu = itemView.findViewById(R.id.search);

        }
    }
}
