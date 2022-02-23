package com.example.filmdac;

import static com.example.filmdac.commons.NodesNames.*;
/*
import static com.example.filmdac.commons.NodesNames.FIRESTORE_COLLECTION;
import static com.example.filmdac.commons.NodesNames.KEY_ACTEURS;
import static com.example.filmdac.commons.NodesNames.KEY_AFFICHE;
import static com.example.filmdac.commons.NodesNames.KEY_ANNEE;
import static com.example.filmdac.commons.NodesNames.KEY_SYNOPSIS;
import static com.example.filmdac.commons.NodesNames.KEY_TITRE;
import static com.example.filmdac.commons.NodesNames.STORAGE_FOLDER;*/

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AddSampleDataToFirebase extends Application {
    //vars changer pour utilisation future

    //Le nom du fichier avec les data à deposer dans le dossier asset
    private static  final String dataFile = "datas.txt";
    private static final String separator = ";";

    /*
    //les cles pour l'association des colonnes de la db
    private static final String KEY_TITRE = "titre";
    private static final String KEY_ANNEE = "annee";
    private static final String KEY_ACTEURS = "acteurs";
    private static final String KEY_AFFICHE = "affiche";
    private static final String KEY_SYNOPSIS = "synopsis";

    //Les variables liées aux emplacements de stockage de Firebase
    private static final String FIRESTORE_COLLECTION = "films";
    private static final String STORAGE_FOLDER = "affiches_films";*/

    //Vars d'utilisation
    private static final String TAG = "AddSampleDataToFirebase";

    private static String urlStorageAffiche;

    //Lien avec la Firestore et storage
    public static CollectionReference filmRef = FirebaseFirestore.getInstance().collection(FIRESTORE_COLLECTION);
    public static StorageReference afficheRef = FirebaseStorage.getInstance().getReference(STORAGE_FOLDER);

    public static void addDatasToFireBase(Context context) {

        int imageToUpload[] = {
                R.drawable.beetlejuice,
                R.drawable.cestarrive,
                R.drawable.chatnoirchatblanc,
                R.drawable.gardenstate,
                R.drawable.ghostintheshell,
                R.drawable.gto,
                R.drawable.lattaquedelamoussaka,
                R.drawable.lodeurdelapapaye,
                R.drawable.lacitedelapeur,
                R.drawable.lamontagnesacree,
                R.drawable.lasvegasparano,
                R.drawable.lestontons,
                R.drawable.hollygraal,
                R.drawable.starwars,
                R.drawable.zabriskiepoint
        };


        InputStreamReader reader = null;
        InputStream file = null;
        BufferedReader bufferedReader = null;
        try {
            file = context.getAssets().open(dataFile);
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);
            String line = null;
            int i = 0;

            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(separator); // Le séparateur est ;
                if (data != null && data.length == 5) { // On vérifie si data est non null et sa longueur
                    // Gestion des images
                    String uriToParse = "android.resource://" + R.class.getPackage().getName() + "/" + imageToUpload[i];
                    i++;
                    Uri imageUri = Uri.parse(uriToParse);
                    Log.i(TAG, "addDatasToFireBase: " + imageUri);
//                    // On ajoute le type de chacun des fichiers ici pour plus de simplicité on utilise des jpg
                    StorageReference fileReference = afficheRef.child(System.currentTimeMillis() + ".jpg");
                    Log.i(TAG, "addDatasToFireBase: " + fileReference);
                    // On envoi l'image vers le storage de Firebase
                    fileReference.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { // Ajout du listener de réussite
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Si tout c'est bien passé alors on demande au storage de nous renvoyer l'addresse de stockage
                                    fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            // Quand on récupére l'URL on la transforme en texte pour l'insérer dans Firestore avec les autres données
                                            urlStorageAffiche = task.getResult().toString();
                                            // Puis on récupère les infos de texte
                                            String titre = data[0];
                                            String titre_minus = titre.toLowerCase();
                                            int annee = Integer.decode(data[1]);
//                                            String[] acteurs = data[2].split(",");
                                            String acteurs = data[2];
                                            String synopsis = data[4];
                                            // Et on les préparent pour les envoyer
                                            Map<String, Object> datas = new HashMap<>();
                                            datas.put(KEY_TITRE, titre);
                                            datas.put(KEY_TITRE_MINUS, titre_minus);
                                            datas.put(KEY_ANNEE, annee);
                                            datas.put(KEY_ACTEURS, acteurs);
                                            datas.put(KEY_AFFICHE, urlStorageAffiche);
                                            datas.put(KEY_SYNOPSIS, synopsis);

                                            filmRef.add(datas)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.i("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("TAG", "Error adding document", e);
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                }
            }
            // Sinon on affiche les erreurs
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                // Si la lecture du buffer n'est pas null on le ferme et on récupère les datas
                try {
                    bufferedReader.close();
                    reader.close();
                    SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // On place le boolean de embeddedDataInserted à true pour dire qu'il n'est pas vide
                    editor.putBoolean(KEY_PREFS, true);
                    editor.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
