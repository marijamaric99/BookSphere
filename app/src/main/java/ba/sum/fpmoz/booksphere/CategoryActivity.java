package ba.sum.fpmoz.booksphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ba.sum.fpmoz.booksphere.adapter.CategoryAdapter;
import ba.sum.fpmoz.booksphere.model.Book;
import ba.sum.fpmoz.booksphere.model.Category;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    SearchView searchView;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://booksphere-57afc-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    DatabaseReference categoryRef;

    BottomNavigationView bottomNavigationView;

    private static final String TAG = "CATEGORY_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryRef = mDatabase.getReference("Category");

        searchView = findViewById(R.id.search);
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


        this.recyclerView = findViewById(R.id.categoriesRv);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(this.mDatabase.getReference("Categories"), Category.class)
                .build();
        this.categoryAdapter = new CategoryAdapter(options);
        this.recyclerView.setAdapter(this.categoryAdapter);



        Button addCategoryBtn = findViewById(R.id.addCategoryBtn);
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (isEmailValid(currentUser.getEmail())) {
                    Intent addBookActivity = new Intent(CategoryActivity.this, CategoryAddActivity.class);
                    startActivity(addBookActivity);
                } else {
                    Toast.makeText(getApplicationContext(), "Nemate mogućnost dodavavanja knjiga", Toast.LENGTH_SHORT).show();
                }
            }
        });


        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryAddIntent = new Intent(CategoryActivity.this, CategoryAddActivity.class);
                startActivity(categoryAddIntent);
            }
        });



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
    }

    //ovo isto(nemoras kod ako nes)
    private void performSearch(String query) {
        Query searchQuery = categoryRef
                .orderByChild("category")
                .startAt(query)
                .endAt(query + "\uf8ff");
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Category category = dataSnapshot.getValue(Category.class);
                    Log.d(TAG, "Category:" + category.getCategory());
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
        this.categoryAdapter.startListening();
        Log.d(TAG, "starting: početak");
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.categoryAdapter.stopListening();
        Log.d(TAG, "ending: kraj");
    }


    public boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "(fpmoz.sum.ba)$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

}