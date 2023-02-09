package com.vuzix.companionsettingssample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.vuzix.connectivity.sdk.Connectivity;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREF_KEY_TOGGLE = "toggle";
    public static final String PREF_KEY_SLIDER = "slider";

    private SharedPreferences prefs;

    private Switch mSwitch;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSwitch = findViewById(R.id.main_switch);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean(PREF_KEY_TOGGLE, mSwitch.isChecked()).apply();

                // OPTIONAL: notify companion app
                Intent putSetting = new Intent("com.vuzix.action.PUT_SETTING");
                putSetting.setPackage("com.vuzix.companion");
                putSetting.putExtra("id", CompanionSettingsReceiver.TOGGLE_ID);
                putSetting.putExtra("value", mSwitch.isChecked());
                Connectivity.get(getApplicationContext()).sendBroadcast(putSetting);
            }
        });

        mSeekBar = findViewById(R.id.main_seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // no-op
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // no-op
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.edit().putInt(PREF_KEY_SLIDER, seekBar.getProgress()).apply();

                // OPTIONAL: notify companion app
                Intent putSetting = new Intent("com.vuzix.action.PUT_SETTING");
                putSetting.setPackage("com.vuzix.companion");
                putSetting.putExtra("id", CompanionSettingsReceiver.SLIDER_ID);
                putSetting.putExtra("value", seekBar.getProgress());
                Connectivity.get(getApplicationContext()).sendBroadcast(putSetting);
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        updateUI();
    }

    private void updateUI() {
        mSwitch.setChecked(prefs.getBoolean(PREF_KEY_TOGGLE, false));
        mSeekBar.setProgress(prefs.getInt(PREF_KEY_SLIDER, 0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateUI();
    }

    public void refresh(View view) {
        // an example for how to request companion app query this app's settings again
        Intent putSetting = new Intent("com.vuzix.apps.action.UPDATE_SETTINGS");
        putSetting.setPackage("com.vuzix.companion");
        Connectivity.get(this).sendBroadcast(putSetting);
    }
}
