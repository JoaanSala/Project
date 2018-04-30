package com.example.jonsa.reversi_game;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.logo_reversi_fg);
        actionBar.setTitle("PRINCIPAL");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        Button Help = findViewById(R.id.help);
        Button Start = findViewById(R.id.start);
        Button Exit = findViewById(R.id.exit);

        Help.setOnClickListener(this);
        Start.setOnClickListener(this);
        Exit.setOnClickListener(this);
    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.help:
                in = new Intent(this, HelpActivity.class);
                startActivity(in);
                break;
            case R.id.start:
                in = new Intent(this, ConfigActivity.class);
                startActivity(in);
                break;
            case R.id.exit:
                finish();
        }
    }
}
