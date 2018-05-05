package com.example.jonsa.reversi_game;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity{
    protected static final java.lang.String SELECTED_SIZE = "101";
    protected static final java.lang.String PLAYER_NAME = "010";
    protected static final java.lang.String TIMER = "000";
    protected String player_name;
    protected Boolean timer_on;
    protected Integer size;

    protected RadioGroup RadioGroup;
    protected EditText editText;
    protected CheckBox checkBox;

    private Integer Grey;

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

        RadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        RadioGroup.setOnCheckedChangeListener(new RadioGroupInfo());

        editText = (EditText) findViewById(R.id.editText);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        Grey = getResources().getColor(R.color.Grey);

        if(savedInstanceState !=  null) {

            editText.setText(savedInstanceState.getString(PLAYER_NAME));
            checkBox.setChecked(savedInstanceState.getBoolean(TIMER));

            int s = savedInstanceState.getInt(SELECTED_SIZE);
            for (int i = 0; i < RadioGroup.getChildCount(); i++) {
                RadioButton r = (RadioButton) RadioGroup.getChildAt(i);
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
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_message,
                    (ViewGroup) findViewById(R.id.linearlayout_error));

            ImageView image = (ImageView) layout.findViewById(R.id.Image);
            image.setMaxWidth(75);
            image.setMaxHeight(75);
            image.setImageResource(R.drawable.profile_error);
            image.setBackgroundColor(Grey);
            TextView text = (TextView) layout.findViewById(R.id.Text);
            text.setBackgroundColor(Grey);
            text.setText(R.string.Profile);

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

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
        int count = RadioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton r = (RadioButton) RadioGroup.getChildAt(i);
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
