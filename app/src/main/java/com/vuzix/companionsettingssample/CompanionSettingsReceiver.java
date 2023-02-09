package com.vuzix.companionsettingssample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vuzix.companion.settings.Settings;
import com.vuzix.companion.settings.Slider;
import com.vuzix.companion.settings.Toggle;
import com.vuzix.connectivity.sdk.Connectivity;

public class CompanionSettingsReceiver extends BroadcastReceiver {

    public static final String TOGGLE_ID = "toggle";
    public static final String SLIDER_ID = "slider";

    @Override
    public void onReceive(Context context, Intent intent) {
        // OPTIONAL: verify request came from companion app
        if (!Connectivity.get(context).verify(intent, "com.vuzix.companion")) {
            // request did not come from companion app
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if ("com.vuzix.action.GET_SETTINGS".equals(intent.getAction())) {
            Settings settings = new Settings();
            settings.addSetting(new Toggle(TOGGLE_ID, "My Toggle", prefs.getBoolean(MainActivity.PREF_KEY_TOGGLE, false)));
            settings.addSetting(new Slider(SLIDER_ID, "My Slider", 0, 100, 1, prefs.getInt(MainActivity.PREF_KEY_SLIDER, 0)));
            setResultExtras(settings.toBundle());
        } else if ("com.vuzix.action.PUT_SETTING".equals(intent.getAction())) {
            switch (intent.getStringExtra("id")) {
                case TOGGLE_ID:
                    prefs.edit().putBoolean(MainActivity.PREF_KEY_TOGGLE, intent.getBooleanExtra("value", false)).apply();
                    break;
                case SLIDER_ID:
                    prefs.edit().putInt(MainActivity.PREF_KEY_SLIDER, intent.getIntExtra("value", 0)).apply();
                    break;
            }
        }
    }
}
