package com.edu.ifsp_prc.labelit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseSaveActivity extends FragmentActivity {

    private static final String FOLDER = "folder";
    private static final String FILENAME = "filename";
    private static final String PATH = "path";
    private static final String DATABASE = "database";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String DEPTH = "depth";
    private static final String SEGMENTED = "segmented";
    private static final String TAG = "Rotulação";
    private String nome_arquivo;
    private DocumentReference mDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_save);
        selecionaNomeArquivo();
    }

    public void selecionaNomeArquivo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirebaseSaveActivity.this);
        builder.setTitle("Nome do arquivo");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(FirebaseSaveActivity.this).inflate(R.layout.text_inpu_password, (ViewGroup) findViewById(android.R.id.content), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                nome_arquivo = input.getText().toString();
                mDocRef = FirebaseFirestore.getInstance().document("rotulacoes/" + nome_arquivo);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void salvaRotulo(View view) {
        EditText folderView = (EditText) findViewById(R.id.editTextFolder);
        EditText pathView = (EditText) findViewById(R.id.editTextPath);
        EditText databaseView = (EditText) findViewById(R.id.editTextDatabase);
        EditText widthView = (EditText) findViewById(R.id.editTextWidth);
        EditText heightView = (EditText) findViewById(R.id.editTextHeight);
        EditText depthView = (EditText) findViewById(R.id.editTextDepth);
        EditText segmentedView = (EditText) findViewById(R.id.editTextSegmented);

        String folderText = folderView.getText().toString();
        String fileNameText = nome_arquivo;
        String pathText = pathView.getText().toString();
        String databaseText = databaseView.getText().toString();
        String widthText = widthView.getText().toString();
        String heightText = heightView.getText().toString();
        String depthText = depthView.getText().toString();
        String segmentedText = segmentedView.getText().toString();


        if (folderText.isEmpty() || fileNameText.isEmpty() || pathText.isEmpty() || databaseText.isEmpty() || widthText.isEmpty() || heightText.isEmpty() || depthText.isEmpty() || segmentedText.isEmpty()) { return; }
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(FOLDER, folderText);
        dataToSave.put(FILENAME, fileNameText);
        dataToSave.put(PATH, pathText);
        dataToSave.put(DATABASE, databaseText);
        dataToSave.put(WIDTH, widthText);
        dataToSave.put(HEIGHT, heightText);
        dataToSave.put(DEPTH, depthText);
        dataToSave.put(SEGMENTED, segmentedText);
        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Rotulação salva!");
            }

        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Oh não, houve algum erro ao salvar rotulo na função salvaRotulo da FirebaseSaveActivity!", e);
            }
        });
    }
}
