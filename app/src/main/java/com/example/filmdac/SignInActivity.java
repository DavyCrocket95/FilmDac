package com.example.filmdac;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.filmdac.commons.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

 private static final String TAG = "SignInActivity";
 private View baseView;

 //Init composant
    public void init()
    {
        baseView = findViewById(R.id.mainLayout);
    }
 
    //Methode gestion du clic
    public void startSignUpActivity(View v1) {
        Log.i(TAG, "btn SingIN = ok");
        signUpActivity();
    }


    private final ActivityResultLauncher<Intent> signLauncher =
            registerForActivityResult(
                    new FirebaseAuthUIActivityResultContract(),
                    new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                        @Override
                        public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                            onSignResult(result);
                        }
                    }

            );

    private void onSignResult(FirebaseAuthUIAuthenticationResult r1)
    {
        IdpResponse resp = r1.getIdpResponse();
        if(r1.getResultCode() == RESULT_OK)
        {
            // On est Connecté

            // Recup nom user connecté
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Affiche une snackBar
            Utils.showSnackBar(baseView, user + " is "+getString(R.string.connected));
        }
        else {
            //No connect why?
            if (resp == null) {
                Utils.showSnackBar(baseView, "Error, authentification failed");
            } else if(resp.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
            {
                Utils.showSnackBar(baseView, "Pas de reseaux");
            } else if(resp.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
            {
                Utils.showSnackBar(baseView, "Error non identifié");
            }
        }
    }

    private void signUpActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic__movies_black_48)
           //     .setTosAndPrivacyPolicyUrls("https://google.fr", "")      RGPD
                .setIsSmartLockEnabled(true)
                .build();

        signLauncher.launch(signInIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null)
        {
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }
}