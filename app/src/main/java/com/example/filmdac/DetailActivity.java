package com.example.filmdac;

import static com.example.filmdac.commons.NodesNames.*;
        /*
import static com.example.filmdac.commons.NodesNames.FIRESTORE_COLLECTION;
import static com.example.filmdac.commons.NodesNames.KEY_ACTEURS;
import static com.example.filmdac.commons.NodesNames.KEY_AFFICHE;
import static com.example.filmdac.commons.NodesNames.KEY_ANNEE;
import static com.example.filmdac.commons.NodesNames.KEY_ID;
import static com.example.filmdac.commons.NodesNames.KEY_SYNOPSIS;
import static com.example.filmdac.commons.NodesNames.KEY_TITRE;*/

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private ImageView ivDetailAffiche;
    private TextView tvDetailAnnee;
    private TextView tvDetailTitre;
    private TextView tvDetailActeurs;
    private TextView tvDetailSynopsis;

    //Firestore
    private FirebaseFirestore db;
    private DocumentReference filmDocRef;

    private String idFilm;
    private String titre, acteurs, affiche, synopsis;
    private long annee;


    public void init() {
        ivDetailAffiche = findViewById(R.id.ivDetailAffiche);
        tvDetailActeurs = findViewById(R.id.tvDetailActeur);
        tvDetailAnnee = findViewById(R.id.tvDetailAnnee);
        tvDetailTitre = findViewById(R.id.tvDetailTitre);
        tvDetailSynopsis = findViewById(R.id.tvDetailSynopsis);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();

        //Recup idFilm
        Intent i2 = getIntent();
        idFilm = i2.getStringExtra(KEY_ID);
        Log.i(TAG, "onCreate: " + idFilm);

        filmDocRef = db.collection(FIRESTORE_COLLECTION).document(idFilm);
        getFilmDetail();

    }

    private void getFilmDetail() {
        filmDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot ds1) {
                        if(ds1.exists()) {
                            titre = ds1.getString(KEY_TITRE);
                            acteurs = ds1.getString(KEY_ACTEURS);
                            affiche = ds1.getString(KEY_AFFICHE);
                            synopsis = ds1.getString(KEY_SYNOPSIS);
                            annee = ds1.getLong(KEY_ANNEE);

                            tvDetailTitre.setText(titre);
                            tvDetailActeurs.setText(acteurs);
                            tvDetailSynopsis.setText(synopsis);
                            tvDetailAnnee.setText("Année de sortie : "+annee);

                            RequestOptions opts = new RequestOptions()
                                    .centerCrop()
                                    .error(R.drawable.ic__movies_black_48)
                                    .placeholder(R.mipmap.ic_launcher);

                            Context ctx = ivDetailAffiche.getContext();

                            Glide.with(ctx)
                                    .load(affiche)
                                    .apply(opts)
                                    .fitCenter()
                                  //  .override(100, 100)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivDetailAffiche);
                        }
                        else
                            Toast.makeText(DetailActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });

    }
}