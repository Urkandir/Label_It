package com.edu.ifsp_prc.labelit;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;

public class SelectImageActivity extends FragmentActivity {

    Button btnLoadImage; //Botão para pegar a imagem da galeria
    final int RQS_IMAGE1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_select_image);

        btnLoadImage = (Button)findViewById(R.id.loadimage);

        //Função do botão para carregar a imagem
        btnLoadImage.setOnClickListener(new View.OnClickListener(){

            //Acessa a galeria e começa uma atividade com o resultado da imagem que foi pega
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }});*/
    }
}
