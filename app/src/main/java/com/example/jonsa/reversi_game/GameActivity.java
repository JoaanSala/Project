package com.example.jonsa.reversi_game;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.example.jonsa.reversi_game.Game_Model.*;

import java.util.ArrayList;

import static com.example.jonsa.reversi_game.ConfigActivity.PLAYER_NAME;
import static com.example.jonsa.reversi_game.ConfigActivity.SELECTED_SIZE;
import static com.example.jonsa.reversi_game.ConfigActivity.TIMER;

public class GameActivity extends AppCompatActivity {

    protected static final String PLAYER_LIST = "00";
    protected static final String CPU_LIST = "11";
    protected static final String REMAINING = "111";
    protected static final String TIME = "000";
    protected static final String NAME = "001";
    protected static final String GRID = "010";
    private static final String REASON = "110";

    protected int grid_size;
    protected boolean game_timer;

    protected String game_player;
    protected Game_Model game_model;
    protected String current_turn;
    private MediaPlayer player;
    private Integer Grey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.game_fg);
        actionBar.setTitle("JOC DEL REVERSI");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Grey = getResources().getColor(R.color.Grey);

        String templateMesageTurn = getString(R.string.game_turn);
        String templateScorePlayer = getString(R.string.game_score_player);
        String templateScoreCPU = getString(R.string.game_score_cpu);
        String templateRemaining = getString(R.string.game_c_left);
        String templateInitialTime = getString(R.string.game_time);

        if (savedInstanceState != null) {
            //load stuff
            grid_size = savedInstanceState.getInt(SELECTED_SIZE);
            game_timer = savedInstanceState.getBoolean(TIMER);
            game_player = savedInstanceState.getString(PLAYER_NAME);

            ArrayList<Integer> p_list = savedInstanceState.getIntegerArrayList(PLAYER_LIST);
            ArrayList<Integer> cpu_list = savedInstanceState.getIntegerArrayList(CPU_LIST);
            //set up initial grid
            game_model = new Game_Model(grid_size, null, null);
            game_model.setCPUPieces(cpu_list);
            game_model.setPlayerPieces(p_list);
        } else {
            //load config
            Intent i = getIntent();
            grid_size = i.getIntExtra(SELECTED_SIZE, 4);
            game_timer = i.getBooleanExtra(TIME, false);
            game_player = i.getStringExtra(PLAYER_NAME);

            //set up initial grid
            game_model = new Game_Model(grid_size, null, null);
        }

        //time text
        TextView time_t = (TextView) findViewById(R.id.game_time);

        if (!game_timer) {
            int t_limit = 0;
            String t = "∞ ";
            String m_t = String.format(templateInitialTime, t);
            time_t.setText(m_t);
            //time_t.setVisibility(View.INVISIBLE);
        } else {
            int t_limit = grid_size * 15;
            String m_t =
                    String.format(templateInitialTime, String.valueOf(t_limit));
            time_t.setText(m_t);
        }

        //turn text
        TextView turn = (TextView) findViewById(R.id.game_turn);
        String m_t = String.format(templateMesageTurn, game_player);
        turn.setText(m_t);

       //player score text
        TextView score_p = (TextView) findViewById(R.id.game_score_player);
        String m_p = String.format(templateScorePlayer, String.valueOf(game_model.getPlayerCount()));
        score_p.setText(m_p);

        //cpu score text
        TextView score_cpu = (TextView) findViewById(R.id.game_score_cpu);
        String m_n = String.format(templateScoreCPU, String.valueOf(game_model.getCPUCount()));
        score_cpu.setText(m_n);

        //remaining cells
        TextView remaining = (TextView) findViewById(R.id.game_c_left);
        String m_r = String.format(templateRemaining, String.valueOf(game_model.getRemainingCount()));
        remaining.setText(m_r);


        //set up grid
        GridView grid = (GridView) findViewById(R.id.grid);
        grid.setNumColumns(grid_size);
        grid.setVerticalSpacing(2);
        grid.setHorizontalSpacing(2);
        grid.setAdapter(new ReversiAdapter(this, game_model.getPieces(Game_Model.PC),
                game_model.getPieces(Game_Model.CPU), game_model.getPieces(Game_Model.PCp),
                game_model.getPieces(Game_Model.CPUp)));
        grid.setOnItemClickListener(new GridInfo());

        current_turn = game_model.PC;
        int t_limit = grid_size * 15;
        //game_model.playGame(t_limit);
    }

    private void playSound(int soundId) {
        player = MediaPlayer.create(this, soundId);
        player.start();
    }

    private void showMesage(String mesage) {
        Toast.makeText(this, mesage, Toast.LENGTH_SHORT).show();
    }

    private boolean checkCell(int position) {
        if (game_model.isPlayerTurn(current_turn)) {
            Game_Model.Coordinates c = game_model.parsePosition(position);
            return game_model.checkPlayerPossible(c);
        } else {
            Game_Model.Coordinates c = game_model.parsePosition(position);
            return game_model.checkCPUPossible(c);
        }
    }

    //save state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //save data
        savedInstanceState.putInt(SELECTED_SIZE, this.grid_size);
        savedInstanceState.putString(PLAYER_NAME, this.game_player);
        savedInstanceState.putBoolean(TIMER, this.game_timer);

        //save game model
        savedInstanceState.putIntegerArrayList(PLAYER_LIST, this.game_model.getPieces(Game_Model.PC));
        savedInstanceState.putIntegerArrayList(CPU_LIST, this.game_model.getPieces(Game_Model.CPU));

    }

    public void clickResults(View src) {
        //stop clock
        Intent i = new Intent(this, TestActivity.class);
        //load data
        i.putExtra(NAME, this.game_player);
        i.putExtra(GRID, this.grid_size);
        i.putExtra(REMAINING, this.getRemaining());
        i.putExtra(PLAYER_LIST, this.game_model.getPlayerCount());
        i.putExtra(CPU_LIST, this.game_model.getCPUCount());
        i.putExtra(TIME, game_model.getTimeElapsed());
        i.putExtra(REASON, game_model.getReason());
        startActivity(i);
        finish();
    }


    protected int getCellSize() {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290 / grid_size, r.getDisplayMetrics());
        return Math.round(px);
    }


    public int getRemaining() { return this.game_model.getRemainingCount();}


    protected class ReversiAdapter extends BaseAdapter {

        private GameActivity mContext;
        private ArrayList<Integer> player;
        private ArrayList<Integer> cpu;
        private ArrayList<Integer> cpu_pos;
        private ArrayList<Integer> player_pos;

        private ReversiAdapter(GameActivity c, ArrayList<Integer> pc, ArrayList<Integer> n_pc,
                               ArrayList<Integer> pc_p, ArrayList<Integer> npc_p) {
            mContext = c;
            player = pc;
            player_pos = pc_p;
            cpu_pos = npc_p;
            cpu = n_pc;
        }

        @Override
        public int getCount() {
            return this.mContext.game_model.getTotalCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView cell;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                cell = new ImageView(mContext);
            } else {
                cell = (ImageView) convertView;
            }
            cell.setLayoutParams(new GridView.LayoutParams(getCellSize(), getCellSize()));

            //call the model to return the cell
            this.setCell(cell, position);

            return cell;
        }

        private void setCell(ImageView cell, int position) {
            if (player.contains(position))
                cell.setBackgroundResource(R.drawable.black_chip);
            else if (player_pos.contains(position))
                cell.setBackgroundResource(R.drawable.black_circle);
            else if (cpu.contains(position))
                cell.setBackgroundResource(R.drawable.red_chip);
            else if (cpu_pos.contains(position))
                cell.setBackgroundResource(R.drawable.red_circle);
            else
                cell.setBackgroundResource(R.drawable.empty_cell);
        }
    }

    private class GridInfo implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            //play game and stuff
            if (checkCell(position)) {
                //play sound
                game_model.setPlayerMove(game_model.parsePosition(position));
                game_model.move =1;
                playSound(R.raw.good_click);
            } else {
                playSound(R.raw.wrong_click);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_message,
                        (ViewGroup) findViewById(R.id.linearlayout_error));

                ImageView image = (ImageView) layout.findViewById(R.id.Image);
                image.setMaxWidth(75);
                image.setMaxHeight(75);
                image.setImageResource(R.drawable.cruz_roja);
                image.setBackgroundColor(Grey);
                TextView text = (TextView) layout.findViewById(R.id.Text);
                text.setBackgroundColor(Grey);
                text.setText(R.string.Error);

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                //String mes = "La casella " + position + " no es valida";
               // showMesage(mes);
            }
        }
    }
}

