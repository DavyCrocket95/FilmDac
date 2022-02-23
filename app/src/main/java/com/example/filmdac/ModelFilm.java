package com.example.filmdac;

public class ModelFilm {
    private  String affiche;
    private String titre;
    private int annee;
    private String acteurs;
    private String synopsis;

    public ModelFilm() {
    }

    public ModelFilm(String titre, int annee, String acteurs, String synopsis) {
        this.titre = titre;
        this.annee = annee;
        this.acteurs = acteurs;
        this.synopsis = synopsis;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getActeurs() {
        return acteurs;
    }

    public void setActeurs(String acteurs) {
        this.acteurs = acteurs;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getAffiche() {
        return affiche;
    }

    public void setAffiche(String affiche) {
        this.affiche = affiche;
    }
}
