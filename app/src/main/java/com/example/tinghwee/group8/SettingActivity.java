package com.example.tinghwee.group8;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SettingActivity extends Activity {

    SharedPreferences preferences;

    /***
     * Text Shortcuts
     */
    private EditText shortcutEditTextF1;
    private EditText shortcutEditTextF2;
    private EditText shortcutEditTextUp;
    private EditText shortcutEditTextLeft;
    private EditText shortcutEditTextRight;
    private EditText shortcutEditTextDown;

    private EditText shortcutEditTextExplore;
    private EditText shortcutEditTextFastest;

    String f1Text = "";
    String f2Text = "";

    String upText = "";
    String leftText = "";
    String rightText = "";
    String downText = "";

    String exploreText = "";
    String fastestText = "";

    /**
     * Voice Shortcuts
     */
    private EditText shortcutEditTextUpVoice;
    private EditText shortcutEditTextLeftVoice;
    private EditText shortcutEditTextRightVoice;
    private EditText shortcutEditTextDownVoice;

    private EditText shortcutEditTextExploreVoice;
    private EditText shortcutEditTextFastestVoice;

    String upTextVoice = "";
    String leftTextVoice = "";
    String rightTextVoice = "";
    String downTextVoice = "";

    String exploreTextVoice = "";
    String fastestTextVoice = "";

    //Save Button
    private Button shortcutButtonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Dialog);
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        /**
         * Text Shortcuts
         */
        shortcutEditTextF1 = (EditText) findViewById(R.id.shortcut_edittext_f1);
        shortcutEditTextF2 = (EditText) findViewById(R.id.shortcut_edittext_f2);

        shortcutEditTextUp = (EditText) findViewById(R.id.shortcut_edittext_up);
        shortcutEditTextLeft = (EditText) findViewById(R.id.shortcut_edittext_left);
        shortcutEditTextRight = (EditText) findViewById(R.id.shortcut_edittext_right);
        shortcutEditTextDown = (EditText) findViewById(R.id.shortcut_edittext_reverse);

        shortcutEditTextExplore = (EditText) findViewById(R.id.shortcut_edittext_explore);
        shortcutEditTextFastest = (EditText) findViewById(R.id.shortcut_edittext_fastest);

        /**
         * Voice Shortcuts
         */
        shortcutEditTextUpVoice = (EditText) findViewById(R.id.shortcut_edittext_up_voice);
        shortcutEditTextLeftVoice = (EditText) findViewById(R.id.shortcut_edittext_left_voice);
        shortcutEditTextRightVoice = (EditText) findViewById(R.id.shortcut_edittext_right_voice);
        shortcutEditTextDownVoice = (EditText) findViewById(R.id.shortcut_edittext_reverse_voice);

        shortcutEditTextExploreVoice = (EditText) findViewById(R.id.shortcut_edittext_explore_voice);
        shortcutEditTextFastestVoice = (EditText) findViewById(R.id.shortcut_edittext_fastest_voice);

        loadPreferences();

        shortcutButtonSave = (Button)findViewById(R.id.shortcut_button_save);
        shortcutButtonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveCommand();
                finish();
            }
        });
    }

    private void loadPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /**
         * Text Shortcuts
         */
        f1Text = preferences.getString("F1Command", "");
        f2Text = preferences.getString("F2Command", "");

        upText = preferences.getString("UpCommand", "");
        leftText = preferences.getString("LeftCommand", "");
        rightText = preferences.getString("RightCommand", "");
        downText = preferences.getString("DownCommand", "");

        exploreText = preferences.getString("ExploreCommand", "");
        fastestText = preferences.getString("FastestCommand", "");

        shortcutEditTextExplore.setText(exploreText);
        shortcutEditTextFastest.setText(fastestText);

        shortcutEditTextUp.setText(upText);
        shortcutEditTextLeft.setText(leftText);
        shortcutEditTextRight.setText(rightText);
        shortcutEditTextDown.setText(downText);

        shortcutEditTextF1.setText(f1Text);
        shortcutEditTextF2.setText(f2Text);

        /**
         * Voice Shortcuts
         */
        upTextVoice = preferences.getString("UpCommandVoice", "");
        leftTextVoice = preferences.getString("LeftCommandVoice", "");
        rightTextVoice = preferences.getString("RightCommandVoice", "");
        downTextVoice = preferences.getString("DownCommandVoice", "");

        exploreTextVoice = preferences.getString("ExploreCommandVoice", "");
        fastestTextVoice = preferences.getString("FastestCommandVoice", "");

        shortcutEditTextExploreVoice.setText(exploreTextVoice);
        shortcutEditTextFastestVoice.setText(fastestTextVoice);

        shortcutEditTextUpVoice.setText(upTextVoice);
        shortcutEditTextLeftVoice.setText(leftTextVoice);
        shortcutEditTextRightVoice.setText(rightTextVoice);
        shortcutEditTextDownVoice.setText(downTextVoice);
    }

    private void SaveCommand() {
        SharedPreferences.Editor editor = preferences.edit();

        /**
         * Text Shortcuts
         */
        editor.putString("F1Command", shortcutEditTextF1.getText().toString());
        editor.putString("F2Command", shortcutEditTextF2.getText().toString());

        editor.putString("UpCommand", shortcutEditTextUp.getText().toString());
        editor.putString("LeftCommand", shortcutEditTextLeft.getText().toString());
        editor.putString("RightCommand", shortcutEditTextRight.getText().toString());
        editor.putString("DownCommand", shortcutEditTextDown.getText().toString());

        editor.putString("ExploreCommand", shortcutEditTextExplore.getText().toString());
        editor.putString("FastestCommand", shortcutEditTextFastest.getText().toString());

        /**
         * VoiceShortcuts
         */
        editor.putString("UpCommandVoice", shortcutEditTextUpVoice.getText().toString());
        editor.putString("LeftCommandVoice", shortcutEditTextLeftVoice.getText().toString());
        editor.putString("RightCommandVoice", shortcutEditTextRightVoice.getText().toString());
        editor.putString("DownCommandVoice", shortcutEditTextDownVoice.getText().toString());

        editor.putString("ExploreCommandVoice", shortcutEditTextExploreVoice.getText().toString());
        editor.putString("FastestCommandVoice", shortcutEditTextFastestVoice.getText().toString());

        editor.commit();

        Toast.makeText(getApplicationContext(), "Shortcuts Saved and Updated", Toast.LENGTH_SHORT).show();
    }
}

