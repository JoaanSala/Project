package com.example.jonsa.reversi_game;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import static com.example.jonsa.reversi_game.GameActivity.GRID;
import static com.example.jonsa.reversi_game.GameActivity.NAME;
import static com.example.jonsa.reversi_game.GameActivity.CPU_LIST;
import static com.example.jonsa.reversi_game.GameActivity.PLAYER_LIST;
import static com.example.jonsa.reversi_game.GameActivity.REMAINING;
import static com.example.jonsa.reversi_game.GameActivity.TIME;

public class TestActivity extends AppCompatActivity {
    private static final String DATE = "001";
    private static final String LOG = "010";
    private static final String MAIL = "100";

    protected String alias;
    protected int grid;
    protected int remaining;
    protected int player;
    protected int npc;
    protected int clock;
    private EditText Email;
    private EditText Log;
    private EditText Day_Hour;
    private Intent in;

    Button buttonReturn;
    Button buttonSendEmail;
    Button buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.save_fg);
        actionBar.setTitle("PROVA");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Email = (EditText)findViewById(R.id.Email_ToSend);
        Log = (EditText)findViewById(R.id.Log);
        Day_Hour = (EditText)findViewById(R.id.Day_Hour);

        //load data
        if (savedInstanceState != null) {
            //load date
            Day_Hour.setText(savedInstanceState.getString(DATE));
            //load log
            Log.setText(savedInstanceState.getString(LOG));
            //load mail
            Email.setText(savedInstanceState.getString(MAIL));
        }

        Intent i = getIntent();
        alias = i.getStringExtra(NAME);
        grid = i.getIntExtra(GRID, 4);
        remaining = i.getIntExtra(REMAINING, 4);
        player = i.getIntExtra(PLAYER_LIST, 2);
        npc = i.getIntExtra(CPU_LIST, 2);
        clock = i.getIntExtra(TIME, 0);

        this.createLog();

        Date d = new Date();
        if (Day_Hour.getText() != null)
            Day_Hour.setText(d.toString());
    }

    private void createLog() {
        String log = "";
        //afegir nom
        log = log + "Alias: " + alias + ". ";
        //afegir mida
        log = log + "Mida graella: " + grid + ". ";
        //affegir temps
        log = log + "Temps Total: " + clock + ". ";
        //afegir dades partida
        if (player > npc)
            log = log + "Has guanyat !!";
        else if (npc > player)
            log = log + "Has perdut !!";
        else
            log = log + "Heu empatat ";
        //dades dels jugadors
        log = log + "Tu: " + player + ". ";
        log = log + "Oponent: " + npc + ". ";
        int difference = Math.abs(player - npc);
        log = log + difference + " caselles de diferencia !";
        if (remaining > 0)
            log = log + "Han quedat " + remaining + " caselles per cobrir.";

        Log.setText(log);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //save data
        savedInstanceState.putString(DATE, this.getDate());
        savedInstanceState.putString(LOG, this.getLog());
        savedInstanceState.putString(MAIL, this.getMail());
    }

    //send button
    public void clickSend(View src) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/html");
        i.setData(Uri.parse("mailto:" + this.getMail()));
        i.putExtra(Intent.EXTRA_SUBJECT, "PracticaAndroid1");
        i.putExtra(Intent.EXTRA_TEXT, this.getLog());
        startActivity(i);

        startActivity(i);
        finish();
    }

    //new button
    public void clickNew(View src) {
        Intent i = new Intent(this, ConfigActivity.class);
        startActivity(i);
        finish();
    }

    //exit button
    public void clickExit(View src) {
        finish();
    }

    public String getMail() {
        return Email.getText().toString();
    }

    public String getLog() {
        return Log.getText().toString();
    }

    public String getDate() {
        return Day_Hour.getText().toString();
    }

}
