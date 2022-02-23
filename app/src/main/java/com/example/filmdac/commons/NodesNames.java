package com.example.filmdac.commons;

import com.example.filmdac.R;

public class NodesNames {
    //les cles pour l'association des colonnes de la db
    public static final String KEY_TITRE = "titre";
    public static final String KEY_TITRE_MINUS = "titre_minus";
    public static final String KEY_ANNEE = "annee";
    public static final String KEY_ACTEURS = "acteurs";
    public static final String KEY_AFFICHE = "affiche";
    public static final String KEY_SYNOPSIS = "synopsis";


    //Les variables liées aux emplacements de stockage de Firebase
    public static final String FIRESTORE_COLLECTION = "films";
    public static final String STORAGE_FOLDER = "affiches_films";

    //Cle et Fichier txt
    public static final String KEY_PREFS = "dataInsertIntoFireBase";
    public static final String  FILE_PREFS = R.class.getPackage().getName()+ ".prefs";

    //Cle doc
    public static final String KEY_ID = "idFilm";
}
