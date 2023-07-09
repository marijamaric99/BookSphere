package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import ba.sum.fpmoz.booksphere.adapter.BookAdapter;
import ba.sum.fpmoz.booksphere.model.Book;

public class BookmarkActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    BookAdapter bookAdapter;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private  static final String TAG = "BOOKMARK_SVD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bookmark);

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

        //ovo
        String saved = getIntent().getStringExtra("saved");
        recyclerView = findViewById(R.id.bookRv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1 ));

        FirebaseRecyclerOptions<Book> options = new FirebaseRecyclerOptions.Builder<Book>()
                .setQuery(mDatabase.getReference("Books").orderByChild("saved").equalTo(true),Book.class)
                .build();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.dismiss();

        bookAdapter = new BookAdapter(options , progressDialog);
        recyclerView. setAdapter(bookAdapter);
    }
    @Override
    protected void onStart(){
        super.onStart();
        bookAdapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        bookAdapter.stopListening();
    }


}