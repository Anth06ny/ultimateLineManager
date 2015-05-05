package com.ultimatelinemanager.activity;

import android.animation.LayoutTransition;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.list_players.PickerPlayerFragment;
import com.ultimatelinemanager.activity.match.MatchFragment;
import com.ultimatelinemanager.activity.match.PointFragment;

import greendao.MatchBean;
import greendao.PlayerBean;
import greendao.PointBean;
import greendao.TeamBean;

/**
 * Created by Anthony on 11/04/2015.
 */
public class GeneriqueActivity extends AppCompatActivity implements View.OnClickListener {

    //Live Point
    private LinearLayout lp_ll_point_live;
    private TextView lp_tv_title;
    private Chronometer lp_time;
    private ImageView lp_bt_open;
    private LinearLayout lp_ll_extension;
    private Button lp_bt_defense;
    private Button lp_bt_offense;
    private ImageView lp_iv_play;
    private TextView lp_tv_manage_player;

    //Graphique
    private LinearLayout cl_ll;
    private FrameLayout container;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_common_layout);

        container = (FrameLayout) findViewById(R.id.container);
        cl_ll = (LinearLayout) findViewById(R.id.cl_ll);

        //live Point
        lp_ll_point_live = (LinearLayout) findViewById(R.id.lp_ll_point_live);
        lp_tv_title = (TextView) findViewById(R.id.lp_tv_title);
        lp_time = (Chronometer) findViewById(R.id.lp_time);
        lp_bt_open = (ImageView) findViewById(R.id.lp_bt_open);
        lp_ll_extension = (LinearLayout) findViewById(R.id.lp_ll_extension);
        lp_bt_defense = (Button) findViewById(R.id.lp_bt_defense);
        lp_bt_offense = (Button) findViewById(R.id.lp_bt_offense);
        lp_iv_play = (ImageView) findViewById(R.id.lp_iv_play);
        lp_tv_manage_player = (TextView) findViewById(R.id.lp_tv_manage_player);

        //Les animation d'apparition du menu de point
        LayoutTransition lt = new LayoutTransition();
        lt.setDuration(300);
        lt.disableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        lt.disableTransitionType(LayoutTransition.APPEARING);
        lp_ll_point_live.setLayoutTransition(lt);
        lp_ll_extension.setLayoutTransition(lt);
        cl_ll.setLayoutTransition(lt);

        lp_bt_defense.setOnClickListener(this);
        lp_bt_offense.setOnClickListener(this);
        lp_tv_manage_player.setOnClickListener(this);
        lp_bt_open.setOnClickListener(this);

        //par defaut on n'affiche pas l'extension
        lp_ll_extension.setVisibility(View.GONE);

        refreshLivePoint();

        goTo(new TeamFragment());

    }

    @Override
    public void setContentView(int layoutResID) {
        // called by activity implementing UPactivity
        final View v = getLayoutInflater().inflate(layoutResID, null);
        // called by activity implementing UPactivity
        container.removeAllViews();
        container.addView(v);
    }

    @Override
    public void onBackPressed() {
        //1 car sinon le 1er revient vers un ecran blanc
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }

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
        else if (v == lp_tv_manage_player) {
            // Handle clicks for lp_bt_offense
        }
        else if (v == lp_bt_open) {
            //Ouvre / ferme l'extension
            openLivePoint(lp_ll_extension.getVisibility() != View.VISIBLE, true);
        }
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    public boolean onOptionsItemSelected(MenuItem item) {

        //On surcharge la fleche de retour pour eviter de perdre les objet en intent
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /* ---------------------------------
    // Navigation
    // -------------------------------- */

    private void goTo(MainFragment mainFragment) {
        //On redirige de base vers TeamFragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.addToBackStack(mainFragment.getClass().getName());
        fragmentTransaction.replace(R.id.container, mainFragment).commit();
    }

    public void gotoPickPlayer(Long teamId) {
        PickerPlayerFragment pickerPlayerActivity = new PickerPlayerFragment();
        pickerPlayerActivity.setTeamBeanId(teamId);
        goTo(pickerPlayerActivity);
    }

    public void goToSelectTeamActivity() {
        MyApplication.getInstance().setTeamBean(null);
        Intent intent = new Intent(this, SelectTeamActivity.class);
        startActivity(intent);
        finish();
    }

    public void gotoMatch(MatchBean matchBean) {
        MatchFragment matchFragment = new MatchFragment();
        matchFragment.setMatchBean(matchBean);
        goTo(matchFragment);
    }

    public void gotoPlayerPage(PlayerBean playerBean) {
        //On va sur la page d'un joueur , pas encore faite.
        ToastUtils.showNotImplementedToast(this);
    }

    public void gotoPoint(PointBean pointBean) {
        PointFragment pointFragment = new PointFragment();
        pointFragment.setPointBean(pointBean);
        goTo(pointFragment);
    }

    /* ---------------------------------
    // LIVE POINT
    // -------------------------------- */

    public void setLivePoint(PointBean livePoint) {
        MyApplication.getInstance().setLivePoint(livePoint);
        //On affiche le fragment du point
        refreshLivePoint();
    }

    private void openLivePoint(boolean open, boolean withAntimation) {

        lp_ll_extension.setVisibility(open ? View.VISIBLE : View.GONE);
        if (withAntimation) {
            Animation animation = AnimationUtils.loadAnimation(this, open ? R.anim.rotate_left : R.anim.rotate_right);
            lp_bt_open.startAnimation(animation);
        }
        else if (open) {
            Animation animation = AnimationUtils.loadAnimation(this, open ? R.anim.rotate_left : R.anim.rotate_right);
            animation.setDuration(0);
            lp_bt_open.startAnimation(animation);
            lp_bt_open.animate().cancel();
        }

        MyApplication.getInstance().setLivePointOpen(open);
    }

    protected void refreshLivePoint() {

        if (getLivePoint() == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lp_ll_point_live.setVisibility(View.GONE);
                }
            });
            MyApplication.getInstance().setLivePointOpen(false);
            //On s'arrete la
            return;
        }

        MatchBean matchBean = getLivePoint().getMatchBean();

        //On calcule le score
        int teamScore = 0;
        int opponentScore = 0;
        for (PointBean pointBean : matchBean.getPointBeanList()) {
            //Uniquement les points joues
            if (pointBean.getTeamGoal() != null) {
                if (pointBean.getTeamGoal()) {
                    teamScore++;
                }
                else {
                    opponentScore++;
                }
            }
        }
        final String liveTitle = matchBean.getTeamBean().getName() + " " + teamScore + " - " + opponentScore + " " + matchBean.getName();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Si on a un point en cours ou pret a demarrer
                lp_ll_point_live.setVisibility(View.VISIBLE);
                //Si le livepoint est ouvert
                openLivePoint(MyApplication.getInstance().isLivePointOpen(), false);

                lp_tv_title.setText(liveTitle);
                //Le chrono

            }
        });

    }

    /* ---------------------------------
    // getter / Setter
    // -------------------------------- */

    protected TeamBean getTeamBean() {
        return MyApplication.getInstance().getTeamBean();
    }

    protected void setTeamBean(TeamBean teamBean) {
        //On met a jour celui d'application aussi
        MyApplication.getInstance().setTeamBean(teamBean);
    }

    protected PointBean getLivePoint() {
        return MyApplication.getInstance().getLivePoint();
    }

}
