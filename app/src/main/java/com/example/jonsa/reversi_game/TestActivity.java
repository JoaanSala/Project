package com.example.jonsa.reversi_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import static com.example.jonsa.reversi_game.ConfigActivity.PLAYER_NAME;
import static com.example.jonsa.reversi_game.ConfigActivity.SELECTED_SIZE;
import static com.example.jonsa.reversi_game.ConfigActivity.TIMER;

import static com.example.jonsa.reversi_game.GridFragment.G_GRID;
import static com.example.jonsa.reversi_game.GridFragment.G_NAME;
import static com.example.jonsa.reversi_game.GridFragment.G_CPU_LIST;
import static com.example.jonsa.reversi_game.GridFragment.G_PLAYER_LIST;
import static com.example.jonsa.reversi_game.GridFragment.G_REASON;
import static com.example.jonsa.reversi_game.GridFragment.G_REMAINING;
import static com.example.jonsa.reversi_game.GridFragment.G_TIME;

public class TestActivity extends AppCompatActivity {

    private static final String RES_DATE = "001";
    private static final String RES_LOG = "010";
    private static final String RES_MAIL = "100";

    protected String alias;
    protected int grid;
    protected int remaining;
    protected int player;
    protected int cpu;
    protected int clock;
    protected String reason;
    private EditText Day_Hour;
    private EditText Log;
    private EditText Email;
    private Intent in;

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

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container2, new MainActivity.PlaceholderFragment()).commit();
        }

        //load data
        if (savedInstanceState != null) {
            //load date
            Day_Hour.setText(savedInstanceState.getString(RES_DATE));
            //load log
            Log.setText(savedInstanceState.getString(RES_LOG));
            //load mail
            Email.setText(savedInstanceState.getString(RES_MAIL));
        }

        in = getIntent();
        alias = in.getStringExtra(G_NAME);
        grid = in.getIntExtra(G_GRID, 4);
        remaining = in.getIntExtra(G_REMAINING, 4);
        player = in.getIntExtra(G_PLAYER_LIST, 2);
        cpu = in.getIntExtra(G_CPU_LIST, 2);
        clock = in.getIntExtra(G_TIME, 0);
        reason = in.getStringExtra(G_REASON);


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
        if (reason.equals(getString(R.string.TOAST_Block)))
            log = log + "T'has quedat sense poder completar la graella !!";
        else if (reason.equals(getString(R.string.TOAST_Lose)))
            log = log + "Has perdut !!";
        else if (reason.equals(getString(R.string.TOAST_Draw)))
            log = log + "Has empatat !!";
        else if (reason.equals(getString(R.string.TOAST_Out_Time)))
            log = log + "Has esgotat el temps !!";
        else
            log = log + "Has guanyat";
        //dades dels jugadors
        log = log + "Tu: " + player + ". ";
        log = log + "Oponent: " + cpu + ". ";
        int difference = Math.abs(player - cpu);
        log = log + difference + " caselles de diferencia !";
        if (remaining > 0)
            log = log + "Han quedat " + remaining + " caselles per cobrir.";

        Log.setText(log);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //save data
        savedInstanceState.putString(RES_DATE, this.getDate());
        savedInstanceState.putString(RES_LOG, this.getLog());
        savedInstanceState.putString(RES_MAIL, this.getMail());
    }

    //send button
    public void clickSend(View src) {
        in = new Intent(Intent.ACTION_SENDTO);
        in.setType("text/html");
        in.setData(Uri.parse("mailto:" + this.getMail()));
        in.putExtra(Intent.EXTRA_SUBJECT, "PracticaAndroid1");
        in.putExtra(Intent.EXTRA_TEXT, this.getLog());
        startActivity(in);

        startActivity(in);
        finish();
    }

    //new button
    public void clickNew(View src) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String Alias = SP.getString("option1", "USER");
        boolean Clock = SP.getBoolean("option2", false);
        String gridSize = SP.getString("option3", "4");
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(SELECTED_SIZE, Integer.valueOf(gridSize));
        i.putExtra(TIMER, Clock);
        i.putExtra(PLAYER_NAME, Alias);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:
                Intent i = new Intent(this, OptionsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
