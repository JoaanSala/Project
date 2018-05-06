package com.example.jonsa.reversi_game;

import java.util.ArrayList;

/**
 * Created by jonsa on 02/05/2018.
 */

public class Game_Model {

    protected final static String PC = "PLAYER";
    protected final static String CPU = "CPU";
    protected final static String PCp = "PLAYER_POSSIBLE";
    protected final static String CPUp = "NPC_POSSIBLE";
    private static final String BLOCK = "cant move";
    private static final String TIMEOUT = "out of time";


    private ArrayList<Coordinates> CPU_pieces;          //computer
    private ArrayList<Coordinates> PLAYER_pieces;       //player
    private ArrayList<Coordinates> PLAYER_possible_moves;      //possible player
    private ArrayList<Coordinates> CPU_possible_moves;      //possible computer

    private int size;                                   //grid length
    public int move = 0;
    private Coordinates last_move;
    private GameTimer clock = new GameTimer();
    private String reason;

    protected Game_Model(int size, ArrayList<Coordinates> player, ArrayList<Coordinates> cpu) {
        this.size = size;
        this.CPU_pieces = (cpu == null) ? new ArrayList<Coordinates>() : cpu;
        this.PLAYER_pieces = (player == null) ? new ArrayList<Coordinates>() : player;
        this.PLAYER_possible_moves = new ArrayList<>();
        this.CPU_possible_moves = new ArrayList<>();
        this.setInitialModel(size);
    }

    //get N of atlas pieces
    protected int getCPUCount() {
        return this.CPU_pieces.size();
    }

    //get N of pbody pieces
    protected int getPlayerCount() {
        return this.PLAYER_pieces.size();
    }

    //get number of remaining pieces
    protected int getRemainingCount() {
        return this.getTotalCount() - this.getCPUCount() - this.getPlayerCount();
    }

    //get total N of pieces
    protected int getTotalCount() {

        return this.size * this.size;
    }

    //coordinate to position
    protected int parseCoordinates(Game_Model.Coordinates c) {
        int r = c.row * this.size;
        return r + c.col;
    }

    //position to coordinate
    protected Coordinates parsePosition(int r) {
        Coordinates c = new Coordinates(0, 0);
        c.row = r / this.size;
        c.col = r % this.size;

        return c;
    }

    protected boolean isPlayerTurn(String turn) {
        return (turn.equals(PC));
    }

    //set starting grid
    protected void setInitialModel(int size) {
        //first white pieces
        Game_Model.Coordinates first_cpu = new Game_Model.Coordinates(size / 2, size / 2);
        Game_Model.Coordinates second_cpu = new Game_Model.Coordinates(first_cpu.row - 1, first_cpu.col - 1);
        this.CPU_pieces.add(first_cpu);
        this.CPU_pieces.add(second_cpu);

        //first black pieces
        Game_Model.Coordinates first_player = new Game_Model.Coordinates(first_cpu.row - 1, size / 2);
        Game_Model.Coordinates second_player = new Game_Model.Coordinates(size / 2, first_cpu.col - 1);
        this.PLAYER_pieces.add(first_player);
        this.PLAYER_pieces.add(second_player);
    }

    protected int getTimeElapsed(){
        clock.stop();
        return clock.getTimeElapsed();
    }

    protected String getReason(){
        return reason;
    }

    protected void setPlayerMove(Coordinates c){
        last_move = c;
    }

    public boolean checkPlayerPossible(Coordinates c) {
        return this.PLAYER_possible_moves.contains(c);
    }

    public boolean checkCPUPossible(Coordinates c) {
        return this.CPU_possible_moves.contains(c);
    }

    public ArrayList<Integer> getPieces(String who) {
        ArrayList<Integer> result = new ArrayList<>();
        if (who == PC) {
            for (Game_Model.Coordinates c : this.PLAYER_pieces) {
                result.add(this.parseCoordinates(c));
            }
        } else if (who == CPU) {
            for (Game_Model.Coordinates c : this.CPU_pieces) {
                result.add(this.parseCoordinates(c));
            }
        } else if (who == PCp) {
            for (Game_Model.Coordinates c : this.PLAYER_possible_moves) {
                result.add(this.parseCoordinates(c));
            }
        } else {
            for (Game_Model.Coordinates c : this.CPU_possible_moves) {
                result.add(this.parseCoordinates(c));
            }
        }
        return result;
    }

    public void setPlayerPieces(ArrayList<Integer> positions) {
        this.PLAYER_pieces = parseList(positions);
    }

    public void setCPUPieces(ArrayList<Integer> positions) {
        this.CPU_pieces = parseList(positions);
    }

    public ArrayList<Coordinates> parseList(ArrayList<Integer> pieces) {
        ArrayList<Coordinates> result = new ArrayList<>();
        for (Integer position : pieces) {
            result.add(this.parsePosition(position));
        }
        return result;
    }

    protected class Coordinates {

        private int row;                          //position x
        private int col;                          //position y

        protected Coordinates(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    protected class GameTimer {
        private long begin, end;

        public void start() {
            begin = System.currentTimeMillis();
        }

        public void stop() {
            end = System.currentTimeMillis();
        }

        public long getTime() {
            return end - begin;
        }

        public double getSeconds() {
            return (end - begin) / 1000.0;
        }

        public int getTimeElapsed() {
            Double d = this.getSeconds();
            Long l = Math.round(d);
            int i = Integer.valueOf(l.intValue());
            return i;
        }

        public boolean checkClock(int time_max) {
            this.stop();
            if (time_max>0){
                if ((time_max-getTimeElapsed()) <= 0){
                    return false;
                }
            }
            return true;
        }
    }
}
