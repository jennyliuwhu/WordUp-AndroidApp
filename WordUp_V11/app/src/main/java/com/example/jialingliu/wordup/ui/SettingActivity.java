package com.example.jialingliu.wordup.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.util.MySharedpreference;

/**
 * Created by jialingliu on 4/14/16.
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    protected ActionBar actionbar;
    private String handswitch;
    private CheckBoxPreference handswitchcheck;
    private MySharedpreference sharedpreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedpreference = new MySharedpreference(this);
        initActionBar();
        setTitle("Settings");
        handswitch = getResources().getString(R.string.hand_switch);
        handswitchcheck = (CheckBoxPreference) findPreference(handswitch);

        handswitchcheck.setOnPreferenceChangeListener(this);
        Boolean hand = sharedpreference.getBoolean("hand_switch");
        handswitchcheck.setChecked(hand);
    }

    private void initActionBar() {
        actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(handswitch)) {
            sharedpreference.saveBoolean("hand_switch", !handswitchcheck.isChecked());
        }else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return preference.getKey().equals(handswitch);
    }
}
