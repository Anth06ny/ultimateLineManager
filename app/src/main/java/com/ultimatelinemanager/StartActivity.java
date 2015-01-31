package com.ultimatelinemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity implements View.OnClickListener {

    private Button maBtNew;
    private Button maBtOld;
    private Button maBtManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        maBtNew = (Button) findViewById(R.id.ma_bt_new);
        maBtOld = (Button) findViewById(R.id.ma_bt_old);
        maBtManage = (Button) findViewById(R.id.ma_bt_manage);

        maBtNew.setOnClickListener(this);
        maBtOld.setOnClickListener(this);
        maBtManage.setOnClickListener(this);
    }

    /* ---------------------------------
    // Click
    // -------------------------------- */

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2015-01-31 16:48:20 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == maBtNew) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (v == maBtOld) {
            // Handle clicks for maBtOld
        }
        else if (v == maBtManage) {
            // Handle clicks for maBtManage
        }
    }

}
