package com.edu.ifsp_prc.labelit;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button rotular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        rotular = findViewById(R.id.buttonDraw);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Vê se o usuário está logado (non-null) e atualiza a UI de acordo.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void sendMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
        startActivity(intent);
    }

    public void consultFirebase(View view)
    {
        Intent intent = new Intent(MainActivity.this, FirebaseConsultActivity.class);
        startActivity(intent);
    }

    public void saveRotulacao(View view)
    {
        Intent intent = new Intent(MainActivity.this, FirebaseSaveActivity.class);
        startActivity(intent);
    }

    public void desenhaImagem(View view)
    {
        Intent intent = new Intent(MainActivity.this, DrawActivity.class);
        startActivity(intent);
    }

    public void sobreAplicativo(View view)
    {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            rotular.setEnabled(true);
            findViewById(R.id.buttonDraw).setEnabled(true);
            findViewById(R.id.textViewLoged).setVisibility(View.GONE);
        } else {
            rotular.setEnabled(false);
            findViewById(R.id.buttonDraw).setEnabled(false);
            findViewById(R.id.textViewLoged).setVisibility(View.VISIBLE);
        }
    }
}
