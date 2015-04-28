package com.ultimatelinemanager.activity.match;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.DateUtils;
import com.formation.utils.LogUtils;
import com.formation.utils.ToastUtils;
import com.formation.utils.Utils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.GeneriqueActivity;
import com.ultimatelinemanager.adapter.PointAdapter;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.IntentHelper;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import greendao.MatchBean;
import greendao.PointBean;

public class MatchActivity extends GeneriqueActivity implements View.OnClickListener, PointAdapter.PointAdapterCB {

    private static final String TAG = LogUtils.getLogTag(MatchActivity.class);

    //Composant graphique
    private TextView ma_tv_statut;
    private TextView ma_tv_playing_time;
    private TextView ma_tv_reel_playing_time;
    private TextView ma_tv_score;
    private TextView ma_tv_date;
    private TextView ma_tv_finish_stat;
    private TextView ma_tv_finish_stat_title;
    private ImageView ma_iv_win;
    private TextView st_empty;

    //RecycleView
    private RecyclerView st_rv;
    private LinearLayoutManager lm;
    private PointAdapter adapter;
    private ArrayList<PointBean> pointBeanList;

    //Data
    private MatchBean matchBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        long matchId = getIntent().getLongExtra(Constante.MATCH_EXTRA_ID, -1);
        matchBean = MatchDaoManager.getMatchBeanDao().load(matchId);

        if (matchBean == null) {
            LogUtils.logMessage(TAG, "matchBean à nulle, matchId=" + matchId);
            finish();
            return;
        }

        ma_tv_statut = (TextView) findViewById(R.id.ma_tv_statut);
        ma_tv_date = (TextView) findViewById(R.id.ma_tv_date);
        ma_tv_playing_time = (TextView) findViewById(R.id.ma_tv_playing_time);
        ma_tv_reel_playing_time = (TextView) findViewById(R.id.ma_tv_reel_playing_time);
        ma_tv_score = (TextView) findViewById(R.id.ma_tv_score);
        ma_tv_finish_stat = (TextView) findViewById(R.id.ma_tv_finish_stat);
        ma_iv_win = (ImageView) findViewById(R.id.ma_iv_win);
        ma_tv_finish_stat_title = (TextView) findViewById(R.id.ma_tv_finish_stat_title);
        st_empty = (TextView) findViewById(R.id.st_empty);
        st_rv = (RecyclerView) findViewById(R.id.st_rv);

        ma_tv_finish_stat.setOnClickListener(this);

        ma_iv_win.setColorFilter(getResources().getColor(R.color.yellow));

        //recycleview
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(lm = new LinearLayoutManager(this));
        st_rv.setItemAnimator(new DefaultItemAnimator());
        pointBeanList = new ArrayList<>();
        pointBeanList.addAll(matchBean.getPointBeanList());

        sortList();
        adapter = new PointAdapter(this, pointBeanList, this);
        st_rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //On ajoute le 1er point
        if (pointBeanList.isEmpty()) {
            addNewPoint();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //On regarde si le requestCode correspont  à un point pour le rafraichir
        if (requestCode != 0) {
            int i = 0;
            for (PointBean pointBean : pointBeanList) {
                if (pointBean.getId() == requestCode) {
                    adapter.notifyItemChanged(i);
                    break;
                }
                i++;
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match, menu);
        menu.findItem(R.id.mm_end_game).setVisible(matchBean.getStart() != null && matchBean.getEnd() == null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mm_state_player:
                statePlayerClick();
                return true;

            case R.id.mm_new_point:
                addNewPoint();
                return true;

            case R.id.mm_end_game:
                if (matchBean.getStart() == null) {
                    showError(new TechnicalException("Click sur le menu terminé alors que le match n'a pas commencé"), true);
                }
                else if (matchBean.getEnd() != null) {
                    showError(new TechnicalException("Click sur le menu terminé alors que le match est déjà terminé"), true);
                }
                else {
                    endGameClick();
                }

                return true;

            case R.id.mm_delete_game:
                deleteGame();
                return true;

            case R.id.mm_rename_opponent:
                renameOpponent();
                return true;

            case R.id.mm_stat:
                statisticClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /* ---------------------------------
    // Click
    // -------------------------------- */

    @Override
    public void onClick(View v) {
        if (v == ma_tv_finish_stat) {
            //En fonction de l'état du match pas la même action
            if (matchBean.getStart() == null) {
                showError(new TechnicalException("Click sur terminé alors que le match n'a pas commencé"), true);
                return;
            }
            //On arrete le match
            else if (matchBean.getEnd() == null) {
                endGameClick();
            }
            //on affiche les stat du match
            else {
                statisticClick();
            }
        }
        else {
            super.onClick(v);
        }
    }

    /* ---------------------------------
    // Adapter callback
    // -------------------------------- */

    @Override
    public void pointAdapter_click(PointBean bean) {

        //Si  ce match n'est pas terminé
        if (matchBean.getEnd() == null) {
            //Si on n'a pas de match en cours ou si le match en cours n'est pas celui la et qu'il n'est pas commencé.
            if (getLivePoint() == null || (getLivePoint().getMatchId() != matchBean.getId() && getLivePoint().getMatchBean().getStart() == null)) {
                //On positionne le 1er point en point courant
                setLivePoint(pointBeanList.get(0));
            }
        }

        IntentHelper.goToPointActivity(this, bean.getId());
    }

    @Override
    public void pointAdapter_deletePoint(PointBean bean) {

        int position = -1;
        for (int i = 0; i < pointBeanList.size(); i++) {
            if (pointBeanList.get(i).getId() == bean.getId()) {
                position = i;
                break;
            }
        }

        PointDaoManager.deletePoint(bean);

        //on essaye de le retirer en mode optimiser
        if (position >= 0) {
            pointBeanList.remove(position);
            adapter.notifyItemRemoved(position);
        }
        //Sinon de maniere normal
        else {
            pointBeanList.remove(bean);
            adapter.notifyDataSetChanged();
        }

        if (pointBeanList.isEmpty()) {
            refreshView();
        }

    }

    /* ---------------------------------
    // Click action
    // -------------------------------- */

    /**
     * Affiche l'etat des joueurs
     */
    private void statePlayerClick() {
        IntentHelper.goToMatchStatePlayer(this, matchBean.getId());
    }

    /**
     * affiche la liste des points du match
     */
    private void addNewPoint() {
        //on crée un nouveau point qu'on ajoute
        PointBean pointBean = new PointBean();
        pointBean.setMatchBean(matchBean);
        pointBean.setId(PointDaoManager.getPointBeanDao().insert(pointBean));
        pointBeanList.add(0, pointBean);

        if (pointBeanList.size() == 1) {
            refreshView();
        }

        //On indique une insertion d'item
        adapter.notifyItemInserted(0);
        if (lm.findFirstCompletelyVisibleItemPosition() == 0) {
            lm.scrollToPosition(0);
        }

    }

    /**
     * retmine le match
     */
    private void endGameClick() {
        matchBean.setEnd(new Date());
        MatchDaoManager.getMatchBeanDao().update(matchBean);
        refreshView();
    }

    /**
     * Supprimer le match
     */
    private void deleteGame() {
        DialogUtils.getConfirmDialog(this, R.drawable.ic_action_delete, R.string.ma_menu_delete_match, getString(R.string.ma_delete_game_question),
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        //On supprime le match de la base et on termine l'activité
                        MatchDaoManager.getMatchBeanDao().delete(matchBean);
                        ToastUtils.showToastOnUIThread(MatchActivity.this, R.string.ma_delete_game_confirmation, Toast.LENGTH_LONG);
                        //Pour prévenir qu'il faut rafraichir
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }).show();
    }

    /**
     * Renome le nom de l'équipe adverse
     */
    private void renameOpponent() {
        DialogUtils.getPromptDialog(this, R.drawable.ic_action_edit, R.string.ma_menu_rename_opponent, R.string.rename, matchBean.getName(),
                new DialogUtils.PromptDialogCB() {
                    @Override
                    public void promptDialogCB_onPositiveClick(String promptText) {
                        matchBean.setName(promptText);
                        MatchDaoManager.getMatchBeanDao().update(matchBean);
                        //Pour prévenir qu'il faudra rafraichir
                        setResult(Activity.RESULT_OK);
                        refreshView();
                    }
                }).show();
    }

    /**
     * Aller sur la page de statistique
     */
    private void statisticClick() {
        IntentHelper.goToMatchStatistic(this, matchBean.getId());
    }

    /* ---------------------------------
    // private
    // -------------------------------- */

    private void refreshView() {

        //titre
        setTitle(getString(R.string.ma_title, getTeamBean().getName(), matchBean.getName()));

        if (matchBean.getStart() != null) {

            //Statut
            //match non terminée
            if (matchBean.getEnd() == null) {
                ma_tv_statut.setTextColor(Utils.getColorFromTheme(this, R.attr.color_text_main));
                ma_tv_statut.setText(getString(R.string.tm_list_statut_in_progress));
            }
            else {
                ma_tv_statut.setTextColor(getResources().getColor(R.color.vivid_green));
                ma_tv_statut.setText(getString(R.string.tm_list_statut_finish));
            }

            //Date
            String dateFormat = DateUtils.getFormat(this, DateUtils.DATE_FORMAT.ddMMyyyy_HHmm);
            ma_tv_date.setText(DateUtils.dateToString(matchBean.getStart(), dateFormat));

            //Playing time
            ma_tv_playing_time.setText(Utils.timeToHHMM(matchBean.getEnd().getTime() - matchBean.getStart().getTime()));

            long reelTime = 0;
            int team = 0, opponent = 0;
            for (PointBean pointBean : pointBeanList) {
                if (pointBean.getLength() != null) {
                    reelTime += pointBean.getLength();

                    //Si le point est termine
                    if (pointBean.getTeamGoal() != null) {
                        if (pointBean.getTeamGoal()) {
                            team++;
                        }
                        else {
                            opponent++;
                        }
                    }
                }
            }

            //reel playing time
            ma_tv_reel_playing_time.setText(Utils.timeToHHMM(reelTime));

            //Score
            ma_tv_score.setText(team + " - " + opponent);

            //Win
            ma_iv_win.setVisibility(team > opponent ? View.VISIBLE : View.INVISIBLE);

            //bouton end game ou statistics
            ma_tv_finish_stat_title.setVisibility(View.VISIBLE);
            ma_tv_finish_stat.setVisibility(View.VISIBLE);
            ma_tv_finish_stat.setText(matchBean.getEnd() == null ? getString(R.string.ma_menu_end_match) : getString(R.string.stat));

        }
        //Match non commencé
        else {

            //Statut
            ma_tv_statut.setText(getString(R.string.tm_list_statut_not_start));
            ma_tv_statut.setTextColor(getResources().getColor(R.color.red));
            //date
            ma_tv_date.setText(Constante.EMPTY);

            //playing time
            ma_tv_playing_time.setText(Constante.EMPTY);

            //reel playing time
            ma_tv_reel_playing_time.setText(Constante.EMPTY);

            //Score
            ma_tv_score.setText(Constante.EMPTY);

            //Win
            ma_iv_win.setVisibility(View.INVISIBLE);

            //bouton finish stat invisible
            ma_tv_finish_stat_title.setVisibility(View.GONE);
            ma_tv_finish_stat.setVisibility(View.GONE);

        }

        //On invalide le menu
        invalidateOptionsMenu();

        //RecycleView
        if (pointBeanList.size() > 0) {
            st_empty.setVisibility(View.INVISIBLE);
            st_rv.setVisibility(View.VISIBLE);
        }
        else {
            st_empty.setVisibility(View.VISIBLE);
            st_rv.setVisibility(View.INVISIBLE);
        }

    }

    private void sortList() {
        Collections.sort(pointBeanList, new Comparator<PointBean>() {
            @Override
            public int compare(PointBean p1, PointBean p2) {
                if (p1.getStart() == null && p2.getStart() == null) {
                    //Si les 2 sont nulle on garde l'autre des id
                    return (int) (p1.getId() - p2.getId());

                }
                else if (p1.getStart() == null) {
                    return -1;
                }
                else if (p2.getStart() == null) {
                    return 1;
                }
                else {
                    return (int) (p1.getStart().getTime() - p2.getStart().getTime());
                }
            }
        });
    }
}
