package com.example.filmdac;

import static com.example.filmdac.commons.NodesNames.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeActivity extends AppCompatActivity {

    //Var pour les wigdet
    private RecyclerView rvFilm;
    private Context ctx;
    private AdapterFilm adapterFilm;
    private FirebaseFirestore db;

    private Toolbar myToolbar;

    public HomeActivity(Context ctx) {
        this.ctx = ctx;
    }

    private void init() {
        rvFilm =findViewById(R.id.rvFilm);
        rvFilm.setHasFixedSize(true);
        rvFilm.setLayoutManager(new LinearLayoutManagerWrapper(ctx, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();

        myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
    }

    public class LinearLayoutManagerWrapper extends LinearLayoutManager {

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            //return super.supportsPredictiveItemAnimations();
            return false;       //Pas d'animation entre les elt dans l'item recycler
        }
    }

    private void addSampleData() {
        SharedPreferences sharedPreferences = getSharedPreferences(R.class.getPackage().getName() + ".prefs", Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean(KEY_PREFS, false ))
            AddSampleDataToFirebase.addDatasToFireBase(getApplicationContext());

    }



    private void getDataFromFirebase() {
        Query query = db.collection(FIRESTORE_COLLECTION).orderBy(KEY_TITRE);

        FirestoreRecyclerOptions<ModelFilm> lstfilm =
                new FirestoreRecyclerOptions.Builder<ModelFilm>()
                .setQuery(query, ModelFilm.class)
                .build();

        adapterFilm = new AdapterFilm((lstfilm));
        rvFilm.setAdapter(adapterFilm);
    }


    @Override
    protected void onStart() {      //se mettre en union avec Firebase
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            startActivity((new Intent(HomeActivity.this, SignInActivity.class)));
        }
        else
            adapterFilm.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterFilm.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView sv1 = (SearchView) searchItem.getActionView();

        sv1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFilm(newText.toString());
                return false;
            }
        });

        return true;
    }

    private void searchFilm(String s1) {
        Query q1 = db.collection(FIRESTORE_COLLECTION);

        if(!String.valueOf(s1).equals((""))) {
            q1 = q1
                    .orderBy(KEY_TITRE_MINUS)
                    .startAt(s1)
                    .endAt(s1 + "\uf8ff");
        }

        FirestoreRecyclerOptions<ModelFilm> sf =
                new FirestoreRecyclerOptions.Builder<ModelFilm>()
                .setQuery(q1, ModelFilm.class)
                .build();

        adapterFilm = new AdapterFilm(sf);
        rvFilm.setAdapter((adapterFilm));
        adapterFilm.startListening();
        clickItem();
    }

    private void clickItem() {
        adapterFilm.setOnItemClickListener(new AdapterFilm.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                String idFilm = documentSnapshot.getId();

                Intent i2 = new Intent(HomeActivity.this, DetailActivity.class);
                i2.putExtra(KEY_ID, idFilm);
                startActivity(i2);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        addSampleData();
        getDataFromFirebase();

        clickItem();

        /*
        adapterFilm.setOnItemClickListener(new AdapterFilm.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
               //On peut créer un objet comprenant tous les données en se basant sur le modele
                ModelFilm film = documentSnapshot.toObject(ModelFilm.class);

                //Recup les data
                String titreDuFilm =film.getTitre();

                //Recup la ref du doc
                documentSnapshot.getReference();

                String idFilm = documentSnapshot.getId();
                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                intent.putExtra(KEY_ID, idFilm);
                startActivity(intent);
            }
        }); */

    }
}