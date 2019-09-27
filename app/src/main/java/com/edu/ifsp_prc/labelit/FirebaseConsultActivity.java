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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseConsultActivity extends FragmentActivity {

    public static final String TAG = "Rotulo";

    TextView mFolderView;
    TextView mFilenameView;
    TextView mPathView;
    TextView mDatabaseView;
    TextView mWidthView;
    TextView mHeightView;
    TextView mDepthView;
    TextView mSegmentedView;
    private String nome_arquivo;
    private DocumentReference mDocRef;
    private CollectionReference mColRef;

    /*private Collection mDocListRef = FirebaseFirestore.getInstance()
            .collection("/foto/dDpy7laY4wVyJEKuWOzA/rotulos")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                    }
                }
            }); */






    /*voce consegue acessar o documento rotulos e "percorrer sua lista de subdocumentos"? */
    // 1o. elemento da lista: r1
    // 2o. r2
    //... 4o. r4





    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_consult);

        mFolderView = (TextView) findViewById(R.id.textViewFolder);
        mFilenameView = (TextView) findViewById(R.id.textViewFileName);
        mPathView = (TextView) findViewById(R.id.textViewPath);
        mDatabaseView = (TextView) findViewById(R.id.textViewDatabase);
        mWidthView = (TextView) findViewById(R.id.textViewWidth);
        mHeightView = (TextView) findViewById(R.id.textViewHeight);
        mDepthView = (TextView) findViewById(R.id.textViewDepth);
        mSegmentedView = (TextView) findViewById(R.id.textViewSegmented);
    }

    public void selecionaNomeArquivo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirebaseConsultActivity.this);
        builder.setTitle("Consultar Documento com nome:");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(FirebaseConsultActivity.this).inflate(R.layout.text_inpu_password, (ViewGroup) findViewById(android.R.id.content), false);
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
                mDocRef = FirebaseFirestore.getInstance().document("/rotulacoes/BaMLqla890VHGxFajjyEi9KGJoY2/imagens/383.jpg/objects/o-0f05f9b5-4241-4845-8763-909e11794f80");
                mColRef = FirebaseFirestore.getInstance().collection("/rotulacoes/BaMLqla890VHGxFajjyEi9KGJoY2/imagens/");
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

    public void consultaBanco(View view){
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Rotulo meuRotulo = documentSnapshot.toObject(Rotulo.class);
                    mFolderView.setText(meuRotulo.getId());
                    mFilenameView.setText(meuRotulo.getName());
                    mPathView.setText(meuRotulo.getXmax());
                    mDatabaseView.setText(meuRotulo.getXmin());
                    mWidthView.setText(meuRotulo.getYmax());
                    mHeightView.setText(meuRotulo.getYmin());
                }
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Ah nao! Algo deu errado na função consultaBanco da classe FirebaseConsultActivity", e);
            }
        });
    }

    //private DocumentReference testDocRef = FirebaseFirestore.getInstance().document("/foto/dDpy7laY4wVyJEKuWOzA/rotulos/r1/respostas_rot/Sim)");
    //Query query = mDocRef.("state", "CA");
    //var query = citiesRef.where("escolhido", "==", "false");

    //Ver o primeiro vídeo que o cara salva as frases inspiracionais, lá ele ensina a ler e a colocar em um textView pelo menos
}
