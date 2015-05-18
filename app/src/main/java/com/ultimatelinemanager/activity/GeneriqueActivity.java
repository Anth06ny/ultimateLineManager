package com.ultimatelinemanager.activity;

import android.animation.LayoutTransition;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.formation.utils.Utils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.list_players.PickerPlayerFragment;
import com.ultimatelinemanager.activity.match.MatchFragment;
import com.ultimatelinemanager.activity.match.PointFragment;
import com.ultimatelinemanager.bean.OttoRefreshEvent;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.exception.TechnicalException;

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
    private LinearLayout lp_ll_point_live, lp_ll_bar;
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

    //Data
    private PointBean livePoint;

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
        lp_ll_bar = (LinearLayout) findViewById(R.id.lp_ll_bar);

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

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Sinon probleme de memoire
        lp_time.stop();
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
        } else {
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
            if (livePoint.getStart() != null) {
                stopPoint(false);
            } else {
                //debut du point en defense
                startPoint(false);
            }

        } else if (v == lp_bt_offense) {
            //si le point est demare but pour l'equipe
            if (livePoint.getStart() != null) {
                stopPoint(true);
            } else {
                //debut du point en attaque
                startPoint(true);
            }
        } else if (v == lp_tv_manage_player) {
            gotoLivePoint();
        } else if (v == lp_bt_open) {
            //Ouvre / ferme l'extension
            openLivePoint(lp_ll_extension.getVisibility() != View.VISIBLE, true);
        } else if (v == lp_tv_previous_point) {

            //LE point 1 est le 1er point on ne revient pas avant
            if (getLiveMatch().getCurrentPoint() > 1) {
                //On revient au point d'avant
                getLiveMatch().setCurrentPoint(getLiveMatch().getCurrentPoint() - 1);
                refreshLivePoint();

                MyApplication.getInstance().getBus().post(OttoRefreshEvent.CHANGE_POINT);
            }
        } else if (v == lp_tv_next_point) {

            //On passe au point suivant  s'il y en a plus on l'ajoute
            if (getLiveMatch().getCurrentPoint() == getLiveMatch().getPointBeanList().size()) {
                PointBean pointBean = new PointBean();
                pointBean.setMatchBean(getLiveMatch());
                pointBean.setNumber(getLiveMatch().getPointBeanList().size() + 1);
                pointBean.setId(PointDaoManager.getPointBeanDao().insert(pointBean));

                getLiveMatch().getPointBeanList().add(0, pointBean);

                //ON propage l'evenement qu'un point a été ajouté
                MyApplication.getInstance().getBus().post(OttoRefreshEvent.ADD_POINT);

            }

            //On decale de 1 le current point
            getLiveMatch().setCurrentPoint(getLiveMatch().getCurrentPoint() + 1);


            refreshLivePoint();

            MyApplication.getInstance().getBus().post(OttoRefreshEvent.CHANGE_POINT);
        } else if (v == lp_iv_play) {
            if (livePoint.getPause()) {
                //ne sera pas comptabilise car point deja demarre
                startPoint(true);
            } else {
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

    /**
     * @param mainFragment
     */
    private void goTo(MainFragment mainFragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //        if (slideAnimation == null) {
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        //        }
        //        else if (slideAnimation) {
        //            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        //        }
        //        else {
        //            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        //        }
        fragmentTransaction.addToBackStack(mainFragment.getClass().getName());
        fragmentTransaction.replace(R.id.container, mainFragment, mainFragment.getClass().getName()).commit();
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
        //Si c'est le match en cours on utilise le bean du match pour le maintenir a jour.
        if (getLiveMatch() != null && getLiveMatch().getId() == matchBean.getId()) {
            matchFragment.setMatchBean(getLiveMatch());
        } else {
            matchFragment.setMatchBean(matchBean);
        }

        goTo(matchFragment);
    }

    //Redirige sur le point courant
    public void gotoLivePoint() {
        // On remonte jusqu'a l'ecran de l'équipe
        getFragmentManager().popBackStack(TeamFragment.class.getName(), 0);
        //On redirige sur le match
        gotoMatch(getLiveMatch());
        gotoPoint(getLiveMatch(), livePoint);
    }

    public void gotoPlayerPage(PlayerBean playerBean) {
        //On va sur la page d'un joueur , pas encore faite.
        ToastUtils.showNotImplementedToast(this);
    }

    public void gotoPoint(MatchBean matchBean, PointBean pointBean) {

        getFragmentManager().popBackStack(MatchFragment.class.getName(), 0);

        PointFragment pointFragment = new PointFragment();
        //Si c'est le live point on passe le live point a la place pour le mettre a jour au passage
        if (livePoint != null && livePoint.getId() == pointBean.getId()) {
            pointFragment.setPointBean(livePoint);
            pointFragment.setMatchBean(getLiveMatch());
        } else {
            pointFragment.setPointBean(pointBean);
            pointFragment.setMatchBean(matchBean);
        }

        //        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //        if (slideAnimation == null) {
        //            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.enter_from_right, R.anim.exit_to_left);
        //        }
        //        else if (slideAnimation == null || slideAnimation) {
        //            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left);
        //        }
        //        else {
        //            //Todo gerer le cas ou on recule pour l'animation
        //            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        //        }
        //        fragmentTransaction.addToBackStack(pointFragment.getClass().getName());
        //        fragmentTransaction.replace(R.id.container, pointFragment, pointFragment.getClass().getName()).commit();
        goTo(pointFragment);

    }

    /* ---------------------------------
    // LIVE POINT
    // -------------------------------- */

    public void refreshLivePoint() {
        //On met a jour le livePoint
        try {
            livePoint = MyApplication.getInstance().getLivePoint();
        } catch (TechnicalException e) {
            DialogUtils.getOkDialog(this, R.drawable.ic_launcher, R.string.erreur_generique,
                    "Erreur sur le LivePoint" + " : " + e.getMessageTechnique());
        }

        //init chrono
        if (livePoint != null) {
            //si le point est demarré
            if (livePoint.getStart() != null) {
                //Si le point est en pause, on met la duree du point
                if (livePoint.getPause()) {
                    lp_time.setBase(SystemClock.elapsedRealtime() - livePoint.getLength());
                    lp_time.stop();
                }
                //Si le point est en cours on met la duree du point + le temps depuis la derniere reprise de pause
                else {

                    lp_time.setBase(SystemClock.elapsedRealtime() - (new Date().getTime() - livePoint.getPauseTime().getTime())
                            + livePoint.getLength());
                    lp_time.start();
                }
            }
            //si point non demarre
            else {
                lp_time.setBase(SystemClock.elapsedRealtime());
                lp_time.stop();
            }
        }

        //On met a jour l'UI du point
        refreshLivePointUI();
    }

    private void openLivePoint(boolean open, boolean withAntimation) {

        lp_ll_extension.setVisibility(open ? View.VISIBLE : View.GONE);
        if (withAntimation) {
            Animation animation = AnimationUtils.loadAnimation(this, open ? R.anim.rotate_left : R.anim.rotate_right);
            lp_bt_open.startAnimation(animation);
        } else if (open) {
            Animation animation = AnimationUtils.loadAnimation(this, open ? R.anim.rotate_left : R.anim.rotate_right);
            animation.setDuration(0);
            lp_bt_open.startAnimation(animation);
            lp_bt_open.animate().cancel();
        }

    }

    protected void refreshLivePointUI() {

        if (livePoint == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lp_ll_point_live.setVisibility(View.GONE);
                }
            });
            //On s'arrete la
            return;
        }

        //On calcule le score
        int teamScore = 0;
        int opponentScore = 0;
        for (PointBean pointBean : getLiveMatch().getPointBeanList()) {
            //Uniquement les points joues
            if (pointBean.getTeamGoal() != null) {
                if (pointBean.getTeamGoal()) {
                    teamScore++;
                } else {
                    opponentScore++;
                }
            }
        }

        final String liveTitle = "(P" + livePoint.getNumber() + ") " + getTeamBean().getName() + " " + teamScore + " - " + opponentScore + " "
                + getLiveMatch().getName();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Si on est sur le fragment du point et que c'est pas le même point

                //Si on a un point en cours ou pret a demarrer
                lp_ll_point_live.setVisibility(View.VISIBLE);
                //le titre avec score
                lp_tv_title.setText(liveTitle);

                //bouton par defaut
                lp_tv_previous_point.setVisibility(View.GONE);
                lp_tv_next_point.setVisibility(View.GONE);

                //Bouton 1 defense ou but contre
                //bouton 2 offense ou but pour

                //Le point a commence
                if (livePoint.getStart() != null) {
                    lp_bt_defense.setText(getString(R.string.lp_goal_opponent));
                    lp_bt_offense.setText(getString(R.string.lp_goal_us));
                    lp_bt_defense.setTextColor(Utils.getColorFromTheme(GeneriqueActivity.this, R.attr.color_text_main));
                    lp_bt_offense.setTextColor(lp_bt_defense.getCurrentTextColor());
                    lp_iv_play.setVisibility(View.VISIBLE);

                    //Si on n'est pas en pause on affiche le bouton de pause
                    if (!livePoint.getPause()) {
                        lp_iv_play.setImageResource(R.drawable.pause);
                    } else {
                        //Bouton de lecture
                        lp_iv_play.setImageResource(R.drawable.play);
                    }

                    //Si le point est termine
                    if (livePoint.getStop() != null) {
                        //On indique en selectionne le bouton choisi
                        if (livePoint.getTeamGoal()) {
                            lp_bt_offense.setTextColor(getResources().getColor(R.color.see_green));
                        } else {
                            lp_bt_defense.setTextColor(getResources().getColor(R.color.see_green));
                        }
                        lp_tv_next_point.setVisibility(View.VISIBLE);
                    }
                } else {
                    lp_bt_defense.setText(getString(R.string.lp_defense));
                    lp_bt_offense.setText(getString(R.string.lp_offense));
                    lp_iv_play.setVisibility(View.GONE);
                    //Tant que le point n'a pas démare on peut revenir a celui d'avant s'il y en a
                    if (getLiveMatch().getCurrentPoint() > 1) {
                        lp_tv_previous_point.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    private void startPoint(boolean withOffense) {

        //On verifie si on a le point nombre de joueur
        if (livePoint.getPlayerPointList().isEmpty()) {
            ToastUtils.showToastOnUIThread(this, R.string.lp_no_player, Toast.LENGTH_LONG);
            return;
        }

        //Le point est deja demarre
        if (livePoint.getStart() != null) {
            //S'il est en pause on le relance
            if (livePoint.getPause()) {
                //On redefinit la pause
                livePoint.setPauseTime(new Date());
                livePoint.setPause(false);
                livePoint.setStop(null); //si jamais on reprend un point arrete
                livePoint.setTeamGoal(null);
            } else {
                //s'il est deja en cours on ne fait rien
                ToastUtils.showToastOnUIThread(this, "Point already in progress...");
            }
        } else {
            //Si le point n'est pas demarre on le demarre
            Date date = new Date();
            livePoint.setStart(date);
            livePoint.setPauseTime(date);
            livePoint.setPause(false);
            livePoint.setTeamOffense(withOffense);

            //Si le match n'est pas demarre on demarre le match
            if (livePoint.getMatchBean().getStart() == null) {
                livePoint.getMatchBean().setStart(date);

                //On sauvegarde le match
                MatchDaoManager.getMatchBeanDao().update(livePoint.getMatchBean());

                //On propage l'evenement que le match démarre
                MyApplication.getInstance().getBus().post(OttoRefreshEvent.MATCH_START);
            }
        }

        //on sauvegarde le point
        PointDaoManager.getPointBeanDao().update(livePoint);

        //On propage que le point est démarré
        MyApplication.getInstance().getBus().post(OttoRefreshEvent.POINT_START);

        refreshLivePointUI();
        //On lance le timer
        lp_time.setBase(SystemClock.elapsedRealtime() - livePoint.getLength());
        lp_time.start();

    }

    private void pausePoint() {
        //S'il est deja en pause
        if (!livePoint.getPause()) {
            //On met le point en pause, et on ajoute le temps joue depuis la derniere pause
            livePoint.setPause(true);
            livePoint.setLength(livePoint.getLength() + (new Date().getTime() - livePoint.getPauseTime().getTime()));
            //ON sauvegarde le point
            PointDaoManager.getPointBeanDao().update(livePoint);

        }

        //ON sauvegarde le point
        PointDaoManager.getPointBeanDao().update(livePoint);

        refreshLivePointUI();

        lp_time.stop();
    }

    private void stopPoint(boolean goalUs) {

        //On met le point en pause et en plus on met le resultat du point
        if (livePoint.getStop() == null) {
            //pour ne pas mettre a jour le temps a chaque appuis sur le bouton
            livePoint.setStop(new Date());
        }
        livePoint.setTeamGoal(goalUs);
        pausePoint();


        //On indique l'evenement que le score a changé
        MyApplication.getInstance().getBus().post(OttoRefreshEvent.SCORE_CHANGE);
        MyApplication.getInstance().getBus().post(OttoRefreshEvent.POINT_FINISHED);
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

    protected MatchBean getLiveMatch() {
        return MyApplication.getInstance().getLiveMatch();
    }

}
