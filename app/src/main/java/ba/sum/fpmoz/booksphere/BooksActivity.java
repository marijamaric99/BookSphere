package ba.sum.fpmoz.booksphere;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ba.sum.fpmoz.booksphere.adapter.BookAdapter;
import ba.sum.fpmoz.booksphere.model.Book;
import ba.sum.fpmoz.booksphere.adapter.BookAdapter;
import ba.sum.fpmoz.booksphere.model.Book;

public class BooksActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BookAdapter bookAdapter;
    ImageButton backBtn;
    SearchView searchView;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    BottomNavigationView bottomNavigationView;

    DatabaseReference booksRef;

    private static final String TAG = "BOOKS_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);


        //ovo
        booksRef = mDatabase.getReference("Books");

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        FloatingActionButton openAddBooksBtn = findViewById(R.id.openAddBooksBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.books);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.books:
                        startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.bookmark:
                        startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BooksActivity.this, CategoryActivity.class);
                startActivity(i);
            }
        });

        //ovooo
       String categoryId = getIntent().getStringExtra("categoryId");

        Log.d(TAG, "categoryId" + categoryId);

        this.recyclerView = findViewById(R.id.bookRv);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        FirebaseRecyclerOptions<Book> options = new FirebaseRecyclerOptions.Builder<Book>().setQuery(this.mDatabase.getReference("Books").orderByChild("categoryId").equalTo(categoryId), Book.class).build();
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.bookAdapter = new BookAdapter(options,progressDialog );
        this.recyclerView.setAdapter(this.bookAdapter);


        //ovo je ko i kategorije pa s ovog odes na addbook
        openAddBooksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser  = mAuth.getCurrentUser();
                if(isEmailValid(currentUser.getEmail())){
                    Intent addBookActivity = new Intent(BooksActivity.this, AddBookActivity.class);
                    startActivity(addBookActivity);
                }else{
                    Toast.makeText(getApplicationContext(), "Nemate mogućnost dodavavanja knjiga", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void performSearch(String query) {
        Query searchQuery = booksRef
                .orderByChild("title")
                .startAt(query)
                .endAt(query + "\uf8ff");
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Book book = dataSnapshot.getValue(Book.class);
                    Log.d(TAG, "Book:" + book.getTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(TAG, "Error:"  + error.getMessage());

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        this.bookAdapter.startListening();
        Log.d(TAG, "starting: početak");
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.bookAdapter.stopListening();
        Log.d(TAG, "ending: kraj");
    }

    //Validacija emaila
    public boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "(fpmoz.sum.ba)$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}