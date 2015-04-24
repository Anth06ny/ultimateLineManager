package com.ultimatelinemanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.formation.utils.LogUtils;
import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.metier.exception.ExceptionA;

import greendao.PointBean;
import greendao.TeamBean;

/**
 * Created by Anthony on 11/04/2015.
 */
public class GeneriqueActivity extends AppCompatActivity implements View.OnClickListener {

    //Live Point
    private ViewStub cl_vs;
    private TextView lp_tv_title;
    private Chronometer lp_time;
    private RelativeLayout lp_rl;
    private Button lp_bt_defense;
    private Button lp_bt_offense;
    private ImageView lp_iv_play;
    private TextView lp_tv_minimize;
    private TextView lp_tv_manage_player;

    //Data
    protected TeamBean teamBean;
    protected PointBean livePoint;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_common_layout);

        teamBean = MyApplication.getInstance().getTeamBean();
        livePoint = MyApplication.getInstance().getLivePoint();

        cl_vs = (ViewStub) findViewById(R.id.cl_vs);
        if (false) {
            lp_tv_title = (TextView) findViewById(R.id.lp_tv_title);
            lp_time = (Chronometer) findViewById(R.id.lp_time);
            lp_rl = (RelativeLayout) findViewById(R.id.lp_rl);
            lp_bt_defense = (Button) findViewById(R.id.lp_bt_defense);
            lp_bt_offense = (Button) findViewById(R.id.lp_bt_offense);
            lp_iv_play = (ImageView) findViewById(R.id.lp_iv_play);
            lp_tv_minimize = (TextView) findViewById(R.id.lp_tv_minimize);
            lp_tv_manage_player = (TextView) findViewById(R.id.lp_tv_manage_player);

            lp_bt_defense.setOnClickListener(this);
            lp_bt_offense.setOnClickListener(this);
            lp_tv_minimize.setOnClickListener(this);
            lp_tv_manage_player.setOnClickListener(this);
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        // called by activity implementing UPactivity
        final View v = getLayoutInflater().inflate(layoutResID, null);
        // called by activity implementing UPactivity
        final FrameLayout container = (FrameLayout) findViewById(R.id.container);
        container.removeAllViews();
        container.addView(v);
    }

    /* ---------------------------------
    // Click
    // -------------------------------- */

    @Override
    public void onClick(View v) {
        if (v == lp_bt_defense) {
            // Handle clicks for lp_bt_defense
        }
        else if (v == lp_bt_offense) {
            // Handle clicks for lp_bt_offense
        }
        else if (v == lp_tv_minimize) {
            // Handle clicks for lp_bt_offense
        }
        else if (v == lp_tv_manage_player) {
            // Handle clicks for lp_bt_offense
        }
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    public boolean onOptionsItemSelected(MenuItem item) {

        //On surcharge la flech de retour pour eviter de perdre les objet en intent
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /* ---------------------------------
    // Methodes
    // -------------------------------- */

    protected void showError(ExceptionA exceptionA, boolean crashLytics) {
        LogUtils.logException(getClass(), exceptionA, true);
        ToastUtils.showToastOnUIThread(this, exceptionA.getMessageUtilisateur(), Toast.LENGTH_LONG);
    }

}
