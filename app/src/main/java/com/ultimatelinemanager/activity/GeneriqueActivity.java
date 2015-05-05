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
import android.widget.Toast;

import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.list_players.PickerPlayerFragment;
import com.ultimatelinemanager.activity.match.MatchFragment;
import com.ultimatelinemanager.activity.match.PointFragment;

import java.util.Date;

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
    private TextView lp_tv_previous_point;
    private TextView lp_tv_next_point;

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
        lp_tv_previous_point = (TextView) findViewById(R.id.lp_tv_previous_point);
        lp_tv_next_point = (TextView) findViewById(R.id.lp_tv_next_point);

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
        lp_tv_next_point.setOnClickListener(this);
        lp_tv_previous_point.setOnClickListener(this);
        lp_bt_open.setOnClickListener(this);
        lp_iv_play.setOnClickListener(this);

        //par defaut on n'affiche pas l'extension
        lp_ll_extension.setVisibility(View.GONE);

        goTo(new TeamFragment());

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLivePoint();

        //chrono
        if (getLivePoint() != null) {
            //si le point est demarre
            if (getLivePoint().getStart() != null) {
                //Si le point est en pause, on met la duree du point
                if (getLivePoint().getPause()) {
                    lp_time.setBase(getLivePoint().getLength());
                    lp_time.stop();
                }
                //Si le point est en cours on met la duree du point + le temps depuis la derniere reprise de pause
                else {
                    lp_time.setBase((new Date().getTime() - getLivePoint().getPauseTime().getTime()) + getLivePoint().getLength());
                    lp_time.start();
                }
            }
            //si point non demarre
            else {

                lp_time.setBase(0);
                lp_time.stop();
            }
        }
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
            //si le pint est demare but pour l'equipe adverse
            if (getLivePoint().getStart() != null) {
                stopPoint(false);
            }
            else {
                //debut du point en defense
                startPoint(false);
            }

        }
        else if (v == lp_bt_offense) {
            //si le point est demare but pour l'equipe
            if (getLivePoint().getStart() != null) {
                stopPoint(true);
            }
            else {
                //debut du point en attaque
                startPoint(true);
            }
        }
        else if (v == lp_tv_manage_player) {
            // Handle clicks for lp_bt_offense
        }
        else if (v == lp_bt_open) {
            //Ouvre / ferme l'extension
            openLivePoint(lp_ll_extension.getVisibility() != View.VISIBLE, true);
        }
        else if (v == lp_tv_previous_point) {

        }
        else if (v == lp_tv_next_point) {

        }
        else if (v == lp_iv_play) {
            if (getLivePoint().getPause()) {
                //ne sera pas comptabilise car point deja demarre
                startPoint(true);
            }
            else {
                pausePoint();
            }

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
        //Si c'est le match du point on utilise le bean du point plus a jour.
        if (getLivePoint() != null && getLivePoint().getMatchBean().getId() == matchBean.getId()) {
            matchFragment.setMatchBean(getLivePoint().getMatchBean());
        }
        else {
            matchFragment.setMatchBean(matchBean);
        }

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

    }

    protected void refreshLivePoint() {

        if (getLivePoint() == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lp_ll_point_live.setVisibility(View.GONE);
                }
            });
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
                //le titre avec score
                lp_tv_title.setText(liveTitle);

                //bouton par defaut
                lp_bt_offense.setSelected(true);
                lp_bt_defense.setSelected(true);
                lp_tv_previous_point.setVisibility(View.GONE);
                lp_tv_next_point.setVisibility(View.GONE);

                //Bouton 1 defense ou but contre
                //bouton 2 offense ou but pour

                //Le point a commence
                if (getLivePoint().getStart() != null) {
                    lp_bt_defense.setText(getString(R.string.lp_goal_opponent));
                    lp_bt_offense.setText(getString(R.string.lp_goal_us));
                    lp_iv_play.setVisibility(View.VISIBLE);

                    //Si on n'est pas en pause on affiche le bouton de pause
                    if (!getLivePoint().getPause()) {
                        lp_iv_play.setImageResource(R.drawable.pause);
                    }
                    else {
                        //Bouton de lecture
                        lp_iv_play.setImageResource(R.drawable.play);
                    }

                    //Si le point est termine
                    if (getLivePoint().getStop() != null) {
                        //On indique en selectionne le bouton choisi
                        lp_bt_offense.setSelected(getLivePoint().getTeamGoal());
                        lp_tv_next_point.setVisibility(View.VISIBLE);
                    }

                }
                else {
                    lp_bt_defense.setText(getString(R.string.lp_defense));
                    lp_bt_offense.setText(getString(R.string.lp_offense));
                    lp_iv_play.setVisibility(View.GONE);
                    //Tant que le point n'a pas redemare on peut revenir a celui d'avant
                    lp_tv_previous_point.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    private void startPoint(boolean withOffense) {

        //On verifie si on a le point nombre de joueur
        if (getLivePoint().getPlayerPointList().isEmpty()) {
            ToastUtils.showToastOnUIThread(this, R.string.lp_no_player, Toast.LENGTH_LONG);
            return;
        }

        //Le point est deja demarre
        if (getLivePoint().getStart() != null) {
            //S'il est en pause on le relance
            if (getLivePoint().getPause()) {
                //On redefinit la pause
                getLivePoint().setPauseTime(new Date());
                getLivePoint().setPause(false);
                getLivePoint().setStop(null); //si jamais on reprend un point arrete
                getLivePoint().setTeamGoal(null);
            }
            //s'il est deja en cours on ne fait rien
            ToastUtils.showToastOnUIThread(this, "Point already in progress...");
        }
        else {
            //Si le point n'est pas demarre on le demarre
            Date date = new Date();
            getLivePoint().setStart(date);
            getLivePoint().setPauseTime(date);
            getLivePoint().setPause(false);
            getLivePoint().setTeamOffense(withOffense);

            //Si le match n'est pas demarre on demarre le match
            if (getLivePoint().getMatchBean().getStart() == null) {
                getLivePoint().getMatchBean().setStart(date);
            }
        }

        refreshLivePoint();
        //On lance le timer
        lp_time.start();

    }

    private void pausePoint() {
        //S'il est deja en pause
        if (!getLivePoint().getPause()) {
            //On met le point en pause, et on ajoute le temps joue depuis la derniere pause
            getLivePoint().setPause(true);
            getLivePoint().setLength(getLivePoint().getLength() + (new Date().getTime() - getLivePoint().getPauseTime().getTime()));
        }

        refreshLivePoint();

        lp_time.stop();
    }

    private void stopPoint(boolean goalUs) {

        //On met le point en pause et en plus on met le resultat du point
        if (getLivePoint().getStop() == null) {
            //pour ne pas mettre a jour le temps a chaque appuis sur le bouton
            getLivePoint().setStop(new Date());
        }
        getLivePoint().setTeamGoal(goalUs);
        pausePoint();
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
