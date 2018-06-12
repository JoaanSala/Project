package com.example.jonsa.reversi_game;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import static com.example.jonsa.reversi_game.ConfigActivity.PLAYER_NAME;
import static com.example.jonsa.reversi_game.ConfigActivity.SELECTED_SIZE;
import static com.example.jonsa.reversi_game.ConfigActivity.TIMER;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.logo_reversi_fg);
        actionBar.setTitle("PRINCIPAL");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        Button Help = findViewById(R.id.help);
        Button Start = findViewById(R.id.start);
        Button DataBase = findViewById(R.id.menu_db);
        Button Exit = findViewById(R.id.exit);

        Help.setOnClickListener(this);
        Start.setOnClickListener(this);
        DataBase.setOnClickListener(this);
        Exit.setOnClickListener(this);
    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.help:
                in = new Intent(this, HelpActivity.class);
                startActivity(in);
                break;
            case R.id.start:
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
                break;
            case R.id.menu_db:
                in = new Intent(this, DataBase.class);
                startActivity(in);
                break;
            case R.id.exit:
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, OptionsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

    }
}
