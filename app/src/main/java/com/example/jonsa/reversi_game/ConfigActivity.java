package com.example.jonsa.reversi_game;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity{
    protected static final java.lang.String SELECTED_SIZE = "101";
    protected static final java.lang.String PLAYER_NAME = "010";
    protected static final java.lang.String TIMER = "000";
    protected String player_name;
    protected Boolean timer_on;
    protected Integer size;

    RadioGroup radioGroup;
    EditText editText;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.configuracio_fg);
        actionBar.setTitle("CONFIGURACIO");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        radioGroup = (RadioGroup) findViewById(R.id.RadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroupInfo());

        editText = (EditText) findViewById(R.id.editText);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        if(savedInstanceState !=  null) {

            editText.setText(savedInstanceState.getString(PLAYER_NAME));
            checkBox.setChecked(savedInstanceState.getBoolean(TIMER));

            int s = savedInstanceState.getInt(SELECTED_SIZE);
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                RadioButton r = (RadioButton) radioGroup.getChildAt(i);
                if (Integer.valueOf(r.getText().toString()) == s)
                    r.toggle();
            }
        }

        //initialize configuration
        this.size = getSize();
        this.timer_on = getTimer();
        this.player_name = getPlayer();
    }

    public void clickStartGame(View src) {

        if(editText.getText().toString().equals("")){
            Toast.makeText(this, R.string.ERROR_Alias, Toast.LENGTH_LONG).show();

        }else {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra(SELECTED_SIZE, this.getSize());
            i.putExtra(TIMER, this.getTimer());
            i.putExtra(PLAYER_NAME, this.getPlayer());
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(SELECTED_SIZE, this.getSize());
        savedInstanceState.putString(PLAYER_NAME, this.getPlayer());
        savedInstanceState.putBoolean(TIMER, this.getTimer());
    }

    public Integer getSize() {
        Integer s = 0;                                       //initialized jsut in case
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton r = (RadioButton) radioGroup.getChildAt(i);
            if (r.isChecked()) {
                s = Integer.valueOf(r.getText().toString());
            }
        }

        return s;
    }

    public Boolean getTimer() {
        Boolean b;
        CheckBox c = (CheckBox) findViewById(R.id.checkbox);
        b = c.isChecked();

        return b;
    }

    public String getPlayer() {
        String p;
        EditText e = (EditText) findViewById(R.id.editText);
        p = e.getText().toString();

        return p;
    }

    private class RadioGroupInfo implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                RadioButton other = (RadioButton) group.getChildAt(i);
                if (other.getId() != checkedId) {
                    if (other.isChecked())
                        other.toggle();
                }
            }
        }

    }
}
