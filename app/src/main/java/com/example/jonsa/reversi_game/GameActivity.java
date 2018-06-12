package com.example.jonsa.reversi_game;

import android.content.Intent;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import com.example.jonsa.reversi_game.ReversiLogic.Coord;


import static com.example.jonsa.reversi_game.ConfigActivity.PLAYER_NAME;
import static com.example.jonsa.reversi_game.ConfigActivity.SELECTED_SIZE;
import static com.example.jonsa.reversi_game.ConfigActivity.TIMER;

public class GameActivity extends AppCompatActivity implements GridFragment.PlayListener{

    private GridFragment frgGrid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_fragment);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.game_fg);
        actionBar.setTitle("JOC DEL REVERSI");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        frgGrid = (GridFragment) getFragmentManager().findFragmentById(R.id.FrgGrid);

        frgGrid.setgPlayListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.game_restart){
            restart();
        }
        if(id == R.id.game_giveup){
            giveUp(frgGrid);
        }
        return super.onOptionsItemSelected(item);
    }

    private void restart() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(SELECTED_SIZE, frgGrid.grid_size);
        i.putExtra(TIMER, frgGrid.game_timer);
        i.putExtra(PLAYER_NAME, frgGrid.game_player);
        startActivity(i);
        finish();
    }
    private void giveUp(GridFragment frgGrid) {
        frgGrid.gameHasEnded();
    }

    @Override
    public void onPlay(Coord c){
        LogFragment fgdet = (LogFragment) getFragmentManager().findFragmentById(R.id.FrgLog);
        boolean hayLog = (fgdet != null && fgdet.isInlayout());
        if (hayLog){
            frgGrid.setInitialLog(fgdet);
            fgdet.addToLog(frgGrid.game_model.logPlayMade(c));
        }
    }
}

