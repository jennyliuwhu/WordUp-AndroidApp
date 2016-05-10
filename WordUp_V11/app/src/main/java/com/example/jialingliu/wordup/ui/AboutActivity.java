package com.example.jialingliu.wordup.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.jialingliu.wordup.BuildConfig;
import com.example.jialingliu.wordup.R;

/**
 * Created by jialingliu on 4/14/16.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
        versionNumber.setText("Version " + BuildConfig.VERSION_NAME +"\n" + "Enjoy Your WordUp Journey!"+"\n"+"Feel free to contact us if there's any problems or suggestions!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
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
}
