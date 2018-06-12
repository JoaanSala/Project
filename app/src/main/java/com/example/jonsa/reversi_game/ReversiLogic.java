package com.example.jonsa.reversi_game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ReversiLogic implements Parcelable{
    public static final Creator<ReversiLogic> CREATOR = new Creator<ReversiLogic>() {
        @Override
        public ReversiLogic createFromParcel(Parcel in) {
            return new ReversiLogic(in);
        }

        @Override
        public ReversiLogic[] newArray(int size) {
            return new ReversiLogic[size];
        }
    };

    protected static final int[] DX = {-1, 0, 1, -1, 1, -1, 0, 1};
    protected static final int[] DY = {-1, -1, -1, 0, 0, 1, 1, 1};
    protected static String Win = "Victoria";
    protected static String Lose = "Derrota";
    protected static String Draw = "Empate";
    protected static String Block = "Bloqueo";
    protected static String Over = "Tiempo Agotado";
    protected static String GiveUp = "Rendit";
    protected final int boardSize;             // gridsize 4 / 6 / 8
    protected final int time_limit;            // if ON:15xsize
    protected GridFragment frag_context;
    protected Map<Coord, Piece> pieces;        // +2+1: BLACK +1+0: WHITE
    protected GameClock timeKeeper;            // timer
    protected ArrayList<Map> turns;            // save board
    protected ArrayList<Piece> whos_turn;      // save color
    protected ArrayList<Coord> posplays;       // shows the current player s plays
    protected boolean gameover;                // marks the game as over
    protected boolean lastPlayerPassed;        // marks the palyer that can't make a play
    protected Piece current;                   // marks the current player
    protected String reason;
    protected String time_i;
    protected String time_f;
    protected String result;

    protected ReversiLogic(Parcel in) {
        boardSize = in.readInt();
        time_limit = in.readInt();
        gameover = in.readByte() != 0;
        lastPlayerPassed = in.readByte() != 0;
    }

    //create game
    public ReversiLogic(int gridSize, boolean timeON, GridFragment con) {
        boardSize = gridSize;
        pieces = new HashMap<>();
        //handle timer
        timeKeeper = new GameClock();
        timeKeeper.start();
        if (timeON) {
            time_limit = -1;
        } else {
            time_limit = 15 * gridSize;
            new LongOperation().execute("");
        }
        //place the initial pieces
        this.resetPieces();
        lastPlayerPassed = false;
        current = Piece.BLACK;
        posplays = (ArrayList<Coord>) legalPlays(pieces, current);
        frag_context = con;
        turns = new ArrayList<>();
        whos_turn = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(boardSize);
        dest.writeInt(time_limit);
        dest.writeByte((byte) (gameover ? 1 : 0));
        dest.writeByte((byte) (lastPlayerPassed ? 1 : 0));
    }

    public void changeColor(Piece color) {
        if (color == Piece.BLACK)
            current = Piece.RED;
        else
            current = Piece.BLACK;
    }

    protected void endGame() {
        View v = new View(frag_context.getActivity());
        //get reason to end game
        if (result == null) result = Over;
        open(v, reason);
    }

    public void open(final View view, String reason) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(frag_context.getActivity());
        alertDialogBuilder.setMessage(reason);
        alertDialogBuilder.setPositiveButton(R.string.GAME_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //surt
                        frag_context.gameHasEnded();
                        try {
                            this.finalize();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        if (!(frag_context.getActivity()).isFinishing()) {
            //show dialog
            alertDialog.show();
        }
    }

    protected void resetPieces() {
        pieces.clear();
        int half = boardSize / 2;
        pieces.put(new Coord(half - 1, half - 1), Piece.RED);
        pieces.put(new Coord(half, half - 1), Piece.BLACK);
        pieces.put(new Coord(half - 1, half), Piece.BLACK);
        pieces.put(new Coord(half, half), Piece.RED);
    }

    protected boolean isLegalPlay(Map<Coord, Piece> board, Piece color, Coord coord) {
        if (!inBounds(coord.x, coord.y) || board.containsKey(coord)) return false;

        // look in each direction from this piece; if we see the other piece color and then one of our
        // own, then this is a legal move
        for (int ii = 0; ii < DX.length; ii++) {
            boolean sawOther = false;
            int x = coord.x, y = coord.y;
            for (int dd = 0; dd < boardSize; dd++) {
                x += DX[ii];
                y += DY[ii];
                if (!inBounds(x, y)) break; // stop when we end up off the board
                Piece piece = board.get(new Coord(x, y));
                if (piece == null) break;
                else if (piece != color) sawOther = true;
                else if (sawOther) return true;
                else break;
            }
        }
        return false;
    }

    protected void applyPlay(Map<Coord, Piece> board, Piece color, Coord coord) {
        List<Coord> toFlip = new ArrayList<>();
        // place this piece into the game state
        board.put(coord, color);
        // determine where this piece captures other pieces
        for (int ii = 0; ii < DX.length; ii++) {
            // look in this direction for captured pieces
            int x = coord.x, y = coord.y;
            for (int dd = 0; dd < boardSize; dd++) {
                x += DX[ii];
                y += DY[ii];
                if (!inBounds(x, y)) break; // stop when we end up off the board
                Coord fc = new Coord(x, y);
                Piece piece = board.get(fc);
                if (piece == null) break;
                else if (piece != color) toFlip.add(fc);
                else { // piece == color
                    for (Coord tf : toFlip) board.put(tf, color); // flip it!
                    break;
                }
            }
            toFlip.clear();
        }
        posplays.clear();
    }

    protected List<Coord> legalPlays(Map<Coord, Piece> board, Piece color) {
        List<Coord> plays = new ArrayList<>();
        // search every board position for a legal move; the force, it's so brute!
        for (int yy = 0; yy < boardSize; yy++) {
            for (int xx = 0; xx < boardSize; xx++) {
                Coord coord = new Coord(xx, yy);
                if (board.containsKey(coord)) continue;
                if (isLegalPlay(board, color, coord)) plays.add(coord);
            }
        }
        return plays;
    }

    protected final boolean inBounds(int x, int y) {
        return (x >= 0) && (x < boardSize) && (y >= 0) && (y < boardSize);
    }

    protected int getRemainingPieces() {
        int c = getTotalPieces();
        c = c - getPieces(Piece.BLACK) - getPieces(Piece.RED);
        return c;
    }
    protected int getTotalPieces() {
        return boardSize * boardSize;
    }

    protected int getPieces(Piece color) {
        int n_B = 0;
        int n_W = 0;
        for (Piece v : pieces.values()) {
            if (v.equals(Piece.BLACK))
                n_B++;
            if (v.equals(Piece.RED))
                n_W++;
        }
        if (color.equals(Piece.BLACK))
            return n_B;
        else
            return n_W;
    }

    protected Coord parsePosition(int piece_pos) {
        return new Coord(piece_pos / boardSize, piece_pos % boardSize);
    }

    protected int getTimeElapsed() {
        timeKeeper.stop();
        return timeKeeper.getTimeElapsed();
    }

    protected int getTimeRemaining() {
        int a = time_limit - getTimeElapsed();
        return a;
    }

    protected Coord makeAIPlay(List plays) {
        List<Coord> AIplays = plays;
        int gain = 0;
        Coord best = new Coord(boardSize, boardSize);
        for (Coord c : AIplays) {
            Map<Coord, Piece> board = new HashMap<Coord, Piece>(pieces);
            applyPlay(board, Piece.RED, c);
            int new_gain = getPieces(Piece.RED);
            if (new_gain > gain) {
                gain = new_gain;
                best = c;
            }
        }
        return best;
    }

    public void playSelected(Coord coord) {
        //check if the play is legal
        if (playChecksOut(coord)) {
            //make the play
            applyPlay(pieces, current, coord);
            turns.add(pieces);
            whos_turn.add(current);
            //
            if (gameOver(pieces)) {
                endGame();
            } else {
                if (current == Piece.BLACK) {
                    //change turn to AI
                    changeColor(current);
                    ArrayList<Coord> AIplays = (ArrayList<Coord>) legalPlays(pieces, current);
                    if (AIplays.isEmpty()) {
                        //AI cant make a play so change back to player
                        lastPlayerPassed = true;
                        changeColor(current);
                        posplays = (ArrayList<Coord>) legalPlays(pieces, current);
                        if (posplays.isEmpty()) {
                            result = Block;
                            reason = frag_context.getResources().getString(R.string.TOAST_Block);
                            frag_context.playSound(R.raw.flip_sound);
                            endGame();
                        }
                    } else {
                        //AI plays
                        playSelected(makeAIPlay(AIplays));
                    }
                } else {
                    //set up the player turn
                    changeColor(current);
                    ArrayList<Coord> Pplays = (ArrayList<Coord>) legalPlays(pieces, current);
                    if (Pplays.isEmpty()) {
                        //player cant make a play so back to AI
                        lastPlayerPassed = true;
                        changeColor(current);
                        ArrayList<Coord> AIplays = (ArrayList<Coord>) legalPlays(pieces, current);
                        if (AIplays.isEmpty()) {
                            result = Block;
                            reason = frag_context.getResources().getString(R.string.TOAST_Block);
                            frag_context.playSound(R.raw.flip_sound);
                            endGame();
                        } else {
                            //AI plays
                            playSelected(makeAIPlay(AIplays));
                        }
                    } else {
                        posplays = Pplays;
                    }
                }
            }
        } else {
            String bad = frag_context.getResources().getString(R.string.TOAST_Error);
            frag_context.playSound(R.raw.wrong_click);
            Toast.makeText(this.frag_context.getActivity(), bad, Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean gameOver(Map<Coord, Piece> pieces) {
        if (pieces.size() == boardSize * boardSize) {
            //check who won
            if (getPieces(Piece.RED) > getPieces(Piece.BLACK)) {
                result = Lose;
                reason = frag_context.getResources().getString(R.string.TOAST_Lose);
                frag_context.playSound(R.raw.lose_sound);
            } else if (getPieces(Piece.RED) == getPieces(Piece.BLACK)) {
                result = Draw;
                reason = frag_context.getResources().getString(R.string.TOAST_Draw);
                frag_context.playSound(R.raw.draw_sound);
            } else {
                result = Win;
                reason = frag_context.getResources().getString(R.string.TOAST_Win);
                frag_context.playSound(R.raw.win_sound);
            }
            return true;
        }
        return false;
    }

    protected boolean playChecksOut(Coord coord) {
        return isLegalPlay(pieces, current, coord);
    }

    protected String logPlayMade(Coord c) {
        String message;
        //log play
        String play_t =
                String.format(frag_context.getResources().getString(R.string.LOG_play), c.toString());
        message = "  " + play_t + "\n";
        //log remaining
        String remain_t =
                String.format(frag_context.getResources().getString(R.string.LOG_remaining),
                        String.valueOf(getRemainingPieces()));
        message = message + "  " + remain_t + "\n";
        //log time of play
        String timeI_t =
                String.format(frag_context.getResources().getString(R.string.LOG_play_i), time_i);
        String timeF_t =
                String.format(frag_context.getResources().getString(R.string.LOG_play_f, time_f),
                        String.valueOf(getRemainingPieces()));
        message = message + "  " + timeI_t + timeF_t + "\n";
        time_f = "";
        time_i = "";
        //log remaining time
        String time_left_t =
                String.format(frag_context.getResources().getString(R.string.LOG_time_left), String.valueOf(getTimeRemaining()));
        message = message + "  " + time_left_t + "\n";
        message = message + frag_context.getResources().getString(R.string.LOG_separator) + "\n";

        return message;
    }

    protected enum Piece {BLACK, RED}

    //extra private Classes

    protected static class Coord {
        private final int x, y;

        private Coord(int x, int y) {
            assert x >= 0 && y >= 0;
            this.x = x;
            this.y = y;
        }

        private boolean equals(Coord other) {
            return other.x == x && other.y == y;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof Coord) && equals((Coord) other);
        }

        @Override
        public int hashCode() {
            return x ^ y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    protected class GameClock {
        private long begin, end;

        public void start() {
            begin = System.currentTimeMillis();
        }

        public void stop() {
            end = System.currentTimeMillis();
        }

        public double getSeconds() {
            return (end - begin) / 1000.0;
        }

        public int getTimeElapsed() {
            Double d = this.getSeconds();
            Long l = Math.round(d);
            int i = l.intValue();
            return i;
        }

        public boolean checkClock(int time_max) {
            this.stop();
            if (time_max > 0) {
                //out of time
                if ((time_max - getTimeElapsed()) <= 0) {
                    return false;
                }
            }
            //still time left
            return true;
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            for (int i = -1; i < time_limit; i++) {
                try {
                    Thread.sleep(1000);
                    if (!timeKeeper.checkClock(time_limit))
                        return "over";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            result = Over;
            reason = frag_context.getResources().getString(R.string.TOAST_Out_Time);
            endGame();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
