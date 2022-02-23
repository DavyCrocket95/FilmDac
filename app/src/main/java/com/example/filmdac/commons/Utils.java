package com.example.filmdac.commons;


import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Utils {

    public static void showSnackBar(View baseView, String mes)  {
        Snackbar.make(baseView, mes, Snackbar.LENGTH_LONG).show();
    }
}
