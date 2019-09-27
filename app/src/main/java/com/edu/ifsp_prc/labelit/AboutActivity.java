package com.edu.ifsp_prc.labelit;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class AboutActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_about);
    }

    public void redirecionaLink(View view)
    {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://prc.ifsp.edu.br/"));
        startActivity(viewIntent);
    }

    public void sairSobre(View view)
    {
        finish();
    }
}
