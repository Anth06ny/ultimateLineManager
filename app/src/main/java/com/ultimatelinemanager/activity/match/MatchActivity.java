package com.ultimatelinemanager.activity.match;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.IntentHelper;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import java.util.Date;

import greendao.MatchBean;
import greendao.PointBean;

public class MatchActivity extends GeneriqueActivity implements View.OnClickListener {

    private static final String TAG = LogUtils.getLogTag(MatchActivity.class);

    //Composant graphique
    private TextView ma_tv_statut;
    private TextView ma_tv_playing_time;
    private TextView ma_tv_reel_playing_time;
    private TextView ma_tv_score;
    private TextView ma_tv_date;
    private TextView ma_tv_finish_stat;
    private TextView ma_tv_finish_stat_title;
    private Button ma_bt_point;
    private ImageView ma_iv_win;

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
        ma_bt_point = (Button) findViewById(R.id.ma_bt_point);
        ma_iv_win = (ImageView) findViewById(R.id.ma_iv_win);
        ma_tv_finish_stat_title = (TextView) findViewById(R.id.ma_tv_finish_stat_title);

        ma_bt_point.setOnClickListener(this);
        ma_tv_finish_stat.setOnClickListener(this);

        ma_iv_win.setColorFilter(getResources().getColor(R.color.yellow));

    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshView();
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

            case R.id.mm_points:
                pointClick();
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
        if (v == ma_bt_point) {
            pointClick();
        }
        else if (v == ma_tv_finish_stat) {
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
    private void pointClick() {
        IntentHelper.goToMatchPoint(this, matchBean.getId());
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
        setTitle(getString(R.string.ma_title, matchBean.getTeamBean().getName(), matchBean.getName()));

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

            matchBean.resetPointBeanList();
            long reelTime = 0;
            int team = 0, opponent = 0;
            for (PointBean pointBean : matchBean.getPointBeanList()) {
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

    }
}
