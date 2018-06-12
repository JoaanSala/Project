package com.example.jonsa.reversi_game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class DetailRegActivity extends FragmentActivity {

    public static final String EXTRA_TEXTO = "log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reg);

        Intent i = getIntent();

        RegFragment frag = (RegFragment) getFragmentManager().findFragmentById(R.id.FrgDetalle);
        if (frag != null && frag.isInLayout()) {
            frag.setDetails(i.getStringExtra(EXTRA_TEXTO));
        }
    }

    //back
    public void clicBack(View src) {
        finish();
    }
}
