package com.edu.ifsp_prc.labelit;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DrawActivity extends FragmentActivity {

    private static final String XMIN = "xmin";
    private static final String YMIN = "ymin";
    private static final String XMAX = "xmax";
    private static final String YMAX = "ymax";
    private static final String TAG = "DesenhaRotulo";
    private static final String TAG_IMAGEM = "SalvaImagem";
    private static final String IDOBJETO = "id";
    private static final String DATABASE = "database";
    private static final String FILENAME = "filename";
    private static final String NOME = "name";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    // Problema em instanciar construtor, dá conflito com construtores já criados.
    //Tentar achar uma Referência alternativa para a coleção
    /*private final FirebaseFirestore db;

    DrawActivity(FirebaseFirestore db) {
        this.db = db;
    }*/

    //Button btnLoadImage; //Botão para pegar a imagem da galeria
    TextView textSource; //TextView que mostra as dimensões do retangulo, exemplo: x: 201 y: 245 largura: 200 altura: 148
    ImageView imageResult, imageDrawingPane; //imageResult é a imagem pega como resultado; imageDrawingPane é o painel da imagem para desenhar, a bitmap é criada do mesmo tamanho da imagem
    //EditText etoutro;

    final int RQS_IMAGE1 = 1;
    final int CAMERA_REQUEST = 1;

    Uri source; //Uri de onde a imagem vem, ou seja, o endereço da imagem
    Uri uri;
    Intent dados;
    Picture picture;
    Bitmap bitmapMaster; //Cria o bitmap mestre
    Canvas canvasMaster; //Cria o canvas mestre
    Bitmap bitmapDrawingPane; //Cria o bitmap para desenhar
    Canvas canvasDrawingPane; //Cria o canvas para desenhar
    projectPt startPt; //Ponto inicial
    private FirebaseFirestore db;
    DocumentReference mDocRef;
    DocumentReference iDocRef;
    DocumentReference delDocRef;
    CollectionReference mColRef;

    //Pegar uid do usuário
    String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //

    private String id_objeto;
    public String escolha;
    public int Xmin;
    public int Ymin;
    public int Xmax;
    public int Ymax;
    public String filename;
    public int width;
    public int height;
    public String database = "projeto-tech-229ed";
    public String customlbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw);

        //Define as Views
        //selecionaImagem();
        //btnLoadImage = (Button)findViewById(R.id.loadimage);
        textSource = (TextView)findViewById(R.id.sourceuri);
        imageResult = (ImageView)findViewById(R.id.result);
        imageDrawingPane = (ImageView)findViewById(R.id.drawingpane);

        /*//Função do botão para carregar a imagem
        btnLoadImage.setOnClickListener(new OnClickListener(){

            //Acessa a galeria e começa uma atividade com o resultado da imagem que foi pega
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }});*/

        //Faz um listener para caso o usuário toque na tela
        imageResult.setOnTouchListener(new OnTouchListener(){

            //Cria a função onTouch para quando o usuário tocar na tela
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction(); // Cria variável de evento para quando é feita alguma ação
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        textSource.setText("ACTION_DOWN- " + x + " : " + y + "\n");
                        startPt = projectXY((ImageView)v, bitmapMaster, x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        textSource.setText("ACTION_MOVE- " + x + " : " + y + "\n");
                        drawOnRectProjectedBitMap((ImageView)v, bitmapMaster, x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        textSource.setText("ACTION_UP- " + x + " : " + y + "\n");
                        drawOnRectProjectedBitMap((ImageView)v, bitmapMaster, x, y);
                        selecionaRotulo();
                        //finalizeDrawing();
                        id_objeto = geraId();
                        mDocRef = FirebaseFirestore.getInstance().document("rotulacoes/" + currentFirebaseUser + "/imagens/" + filename + "/objects/o-" + id_objeto);
                        break;
                }
                /*
                 * Return 'true' to indicate that the event have been consumed.
                 * If auto-generated 'false', your code can detect ACTION_DOWN only,
                 * cannot detect ACTION_MOVE and ACTION_UP.
                 */
                return true;
            }});

    }

    public void selecionaImagem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);
        builder.setTitle("Selecione uma imagem:");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(DrawActivity.this).inflate(R.layout.activity_select_image, (ViewGroup) findViewById(android.R.id.content), false);
        // Set up the input
        final Button btnLoadImage  = (Button) viewInflated.findViewById(R.id.select_image);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //Função do botão para carregar a imagem
        btnLoadImage.setOnClickListener(new OnClickListener(){

            //Acessa a galeria e começa uma atividade com o resultado da imagem que foi pega
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                dados = intent;
                startActivityForResult(intent, RQS_IMAGE1);
            }});
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Colocar aqui a função para salvar a imagem, e também para pegar o nome
                /*filename = getFileName(source);
                width = bitmapMaster.getWidth();
                height = bitmapMaster.getHeight();
                iDocRef = FirebaseFirestore.getInstance().document("rotulacoes/" + currentFirebaseUser + "/imagens/" + filename);
                salvaImagem(filename, width, height, database);*/
                atualizaCanvas();
                dialog.dismiss();

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //finish();
            }
        });

        builder.show();
    }

    public void selecionaRotulo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);
        builder.setTitle("Selecione um rotulo:");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(DrawActivity.this).inflate(R.layout.tag_inpu_selection, (ViewGroup) findViewById(android.R.id.content), false);
        // Set up the input
        final RadioGroup btGroup = (RadioGroup) viewInflated.findViewById(R.id.radioGroup);
        final RadioButton btBoi = (RadioButton) viewInflated.findViewById(R.id.radioButtonBoi);
        final RadioButton btface = (RadioButton) viewInflated.findViewById(R.id.radioButtonFace);
        final RadioButton btcao = (RadioButton) viewInflated.findViewById(R.id.radioButtonCao);
        final RadioButton btgato = (RadioButton) viewInflated.findViewById(R.id.radioButtonGato);
        final RadioButton btpessoa = (RadioButton) viewInflated.findViewById(R.id.radioButtonPessoa);
        final RadioButton btoutro = (RadioButton) viewInflated.findViewById(R.id.radioButtonOutro);
        final EditText etoutro = (EditText) viewInflated.findViewById(R.id.inputOutro);
        //handleCustomLabel(viewInflated);
        //findViewById(R.id.inputOutro).setVisibility(View.GONE);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //Função do botão para carregar a imagem
        builder.setView(viewInflated);
        /*etoutro.setEnabled(false);
        while (!btBoi.isChecked() || !btface.isChecked() || !btcao.isChecked() || !btgato.isChecked() || !btpessoa.isChecked() || !btoutro.isChecked()) {
            if (btoutro.isChecked()) {
                etoutro.setEnabled(true);
                break;
            }
        }*/
        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finalizeDrawing();
                if (btoutro.isChecked())
                {
                    customlbl = etoutro.getText().toString();
                    escolha = customlbl;
                }
                salvaRotulo(Xmin, Ymin, Xmax, Ymax, escolha);
                atualizaCanvas();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                canvasDrawingPane.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                imageDrawingPane.invalidate();
                dialog.cancel();
            }
        });
        builder.show();
    }

    /*public void handleCustomLabel(View viewInflated){
        final EditText etoutro = (EditText) viewInflated.findViewById(R.id.inputOutro);
    }*/

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonBoi:
                if (checked)
                    escolha = "Boi";
                    break;
            case R.id.radioButtonFace:
                if (checked)
                    escolha = "Face Boi";
                    break;
            case R.id.radioButtonCao:
                if (checked)
                    escolha = "Vaca";
                    break;
            case R.id.radioButtonGato:
                if (checked)
                    escolha = "Face Vaca";
                    break;
            case R.id.radioButtonPessoa:
                if (checked) {
                    escolha = "Pessoa";
                    break;
                }
            case R.id.radioButtonOutro:
                if (checked)
                    //escolha = customlbl;
                break;
        }
    }

    class projectPt{
        int x;
        int y;

        projectPt(int tx, int ty){
            x = tx;
            y = ty;
        }
    }

    private projectPt projectXY(ImageView iv, Bitmap bm, int x, int y){
        if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
            //outside ImageView
            return null;
        }else{
            int projectedX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
            int projectedY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));

            return new projectPt(projectedX, projectedY);
        }
    }

    private void drawOnRectProjectedBitMap(ImageView iv, Bitmap bm, int x, int y){
        if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
            //outside ImageView
            return;
        }else{
            int projectedX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
            int projectedY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));

            //clear canvasDrawingPane
            canvasDrawingPane.drawColor(Color.TRANSPARENT, Mode.CLEAR);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xCC00FF00);
            paint.setStrokeWidth(8);
            canvasDrawingPane.drawRect(startPt.x, startPt.y, projectedX, projectedY, paint);
            imageDrawingPane.invalidate();

            Xmin = startPt.x;
            Ymin = startPt.y;
            Xmax = projectedX;
            Ymax = projectedY;


            textSource.setText(x + ":" + y + "/" + iv.getWidth() + " : " + iv.getHeight() + "\n" +
                    projectedX + " : " + projectedY + "/" + bm.getWidth() + " : " + bm.getHeight()
            );
        }
    }

    private void finalizeDrawing(){
        canvasMaster.drawBitmap(bitmapDrawingPane, 0, 0, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap tempBitmap;

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE1:
                    source = data.getData();
                    textSource.setText(source.toString());

                    try {
                        //tempBitmap is Immutable bitmap,
                        //cannot be passed to Canvas constructor
                        tempBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source));

                        Config config;
                        if(tempBitmap.getConfig() != null){
                            config = tempBitmap.getConfig();
                        }else{
                            config = Config.ARGB_8888;
                        }

                        //bitmapMaster is Mutable bitmap
                        bitmapMaster = Bitmap.createBitmap(
                                tempBitmap.getWidth(),
                                tempBitmap.getHeight(),
                                config);

                        canvasMaster = new Canvas(bitmapMaster);
                        canvasMaster.drawBitmap(tempBitmap, 0, 0, null);

                        imageResult.setImageBitmap(bitmapMaster);

                        //Create bitmap of same size for drawing
                        bitmapDrawingPane = Bitmap.createBitmap(
                                tempBitmap.getWidth(),
                                tempBitmap.getHeight(),
                                Config.ARGB_8888);
                        canvasDrawingPane = new Canvas(bitmapDrawingPane);
                        imageDrawingPane.setImageBitmap(bitmapDrawingPane);

                        filename = getFileName(source);
                        width = bitmapMaster.getWidth();
                        height = bitmapMaster.getHeight();
                        iDocRef = FirebaseFirestore.getInstance().document("rotulacoes/" + currentFirebaseUser + "/imagens/" + filename);
                        salvaImagem(filename, width, height, database);


                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String geraId() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public void salvaImagem(String nome_imagem, int larg_imagem, int altu_imagem, String nome_banco) {

        String largura = Integer.toString(larg_imagem);
        String altura = Integer.toString(altu_imagem);


        if (nome_imagem.isEmpty() || largura.isEmpty() || altura.isEmpty() || nome_banco.isEmpty()) { return; }
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(FILENAME, nome_imagem);
        dataToSave.put(DATABASE, nome_banco);
        dataToSave.put(WIDTH, largura);
        dataToSave.put(HEIGHT, altura);
        iDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG_IMAGEM, "Imagem salva! Na classe DrawActivity!");
            }

        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG_IMAGEM, "Oh não, houve algum erro ao salvar imagem na função salvaImagem da DrawActivity!", e);
            }
        });
    }

    public void salvaRotulo(int xmin, int ymin, int xmax, int ymax, String nome) {

        String MinX = Integer.toString(xmin);
        String MinY = Integer.toString(ymin);
        String MaxX = Integer.toString(xmax);
        String MaxY = Integer.toString(ymax);

        if (MinX.isEmpty() || MinY.isEmpty() || MaxX.isEmpty() || MaxY.isEmpty() || id_objeto.isEmpty() || nome.isEmpty()) { return; }
        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put(XMIN, MinX);
        dataToSave.put(YMIN, MinY);
        dataToSave.put(XMAX, MaxX);
        dataToSave.put(YMAX, MaxY);
        dataToSave.put(IDOBJETO, id_objeto);
        dataToSave.put(NOME, nome);
        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Coordenadas do Retangulo Salvo!");
            }

        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Oh não, houve algum erro ao salvar coordenadas do retangulo na classe DrawActivity!", e);
            }
        });
    }

    public void finalizaRotulacao(View view){
        AlertDialog.Builder finalizaDialog = new AlertDialog.Builder(DrawActivity.this);
        finalizaDialog.setTitle("Finalizar Rotulação");
        finalizaDialog.setMessage("Você deseja finalizar a rotulação e enviar os dados?");

        finalizaDialog.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
                dialog.dismiss();
            }

        });

        finalizaDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        finalizaDialog.show();
    }

    public void atualizaCanvas(){
        mColRef = FirebaseFirestore.getInstance().collection("/rotulacoes/" + currentFirebaseUser + "/imagens/" + filename + "/objects");
                mColRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Imagem imagem = new Imagem();
                                ArrayList<Rotulo> rotulos = new ArrayList<Rotulo>();
                                Rotulo rotulo = new Rotulo();
                                rotulo.setId(document.getString("id"));
                                rotulo.setName(document.getString("name"));
                                rotulo.setXmax(document.getString("xmax"));
                                rotulo.setXmin(document.getString("xmin"));
                                rotulo.setYmax(document.getString("ymax"));
                                rotulo.setYmin(document.getString("ymin"));
                                rotulos.add(rotulo);
                                imagem.setRotulos(rotulos);

                                Log.d("TESTE",rotulo.getId());
                                Log.d("TESTE",rotulo.getName());
                                Log.d("TESTE",rotulo.getXmax());
                                Log.d("TESTE",rotulo.getXmin());
                                Log.d("TESTE",rotulo.getYmax());
                                Log.d("TESTE",rotulo.getYmin());
                                desenhaRetangulo(rotulo.getName(), rotulo.getXmax(), rotulo.getXmin(), rotulo.getYmax(), rotulo.getYmin());
                            }
                        } else {
                            Log.d(TAG, "Houve um erro ao atualizarCanvas em DrawActivity: ", task.getException());
                        }
                    }
                });
    }

    public void desenhaRetangulo(String Nome, String Xmax, String Xmin, String Ymax, String Ymin){

        int Xinicial = Integer.parseInt(Xmin);
        int Xfinal = Integer.parseInt(Xmax);
        int Yinicial = Integer.parseInt(Ymin);
        int Yfinal = Integer.parseInt(Ymax);
        canvasDrawingPane.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        Paint paint = new Paint();
        Paint textPaint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        textPaint.setStyle(Paint.Style.STROKE);
        Random rnd = new Random();
        int color = Color.argb(204, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        paint.setColor(color);
        textPaint.setColor(color);
        paint.setStrokeWidth(8);
        textPaint.setStrokeWidth(4);
        canvasDrawingPane.drawRect(Xinicial, Yinicial, Xfinal, Yfinal, paint);

        textPaint.setTextSize(60);
        canvasDrawingPane.drawText(Nome, Xfinal, Yfinal, textPaint);

        imageDrawingPane.invalidate();
        finalizeDrawing();
    }

    public void apagaRetangulo(){
        mColRef = FirebaseFirestore.getInstance().collection("/rotulacoes/" + currentFirebaseUser + "/imagens/" + filename + "/objects");
        mColRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String id = document.getId();
                                mColRef.document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Documento deletado com sucesso!");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Erro ao deletar documento em DrawActivity", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Houve um erro ao pegar o Documento em apagaRetangulo() em DrawActivity: ", task.getException());
                        }
                    }
                });
    }



    public void alertaDeletar (View view)
    {
        AlertDialog.Builder excluirDialog = new AlertDialog.Builder(DrawActivity.this);
        // Apenas está sem icone ".setIcon"
        excluirDialog.setTitle("Exluir rótulos e escolher outra imagem");
        excluirDialog.setMessage("Você deseja excluir todos os rótulos e escolher outra imagem?");

        excluirDialog.setPositiveButton("Excluir todos", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                apagaRetangulo();

                /*canvasDrawingPane.drawColor(Color.TRANSPARENT, Mode.CLEAR);

                canvasDrawingPane.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                imageDrawingPane.setImageBitmap(bitmapMaster);
                Bitmap tempBitmap;
                uri = dados.getData();
                textSource.setText(uri.toString());

                try {
                    grantUriPermission("com.example.aluno.projetotech", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    tempBitmap = BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(uri));
                    canvasMaster.drawBitmap(tempBitmap, 0, 0, null);
                imageDrawingPane.invalidate();
                finalizeDrawing();
                    canvasMaster.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                canvasMaster = new Canvas(bitmapMaster);
                imageResult.setImageBitmap(bitmapMaster);
                    finalizeDrawing();
                imageDrawingPane.destroyDrawingCache();
                imageDrawingPane.setImageBitmap(bitmapDrawingPane);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/

                dialog.dismiss();

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

        });

        excluirDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        excluirDialog.show();

    }
}