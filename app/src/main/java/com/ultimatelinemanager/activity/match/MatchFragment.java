package com.ultimatelinemanager.activity.match;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.DateUtils;
import com.formation.utils.LogUtils;
import com.formation.utils.ToastUtils;
import com.formation.utils.Utils;
import com.squareup.otto.Subscribe;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.MainFragment;
import com.ultimatelinemanager.adapter.PointAdapter;
import com.ultimatelinemanager.bean.OttoRefreshEvent;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;
import com.ultimatelinemanager.metier.utils.DialogUtils;
import com.ultimatelinemanager.metier.IntentHelper;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import greendao.MatchBean;
import greendao.PointBean;

public class MatchFragment extends MainFragment implements View.OnClickListener, PointAdapter.PointAdapterCB {

    private static final String TAG = LogUtils.getLogTag(MatchFragment.class);

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

    //Data
    private MatchBean matchBean;

    //Otto
    private Object ottoListner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_match, container, false);

        if (matchBean == null) {
            LogUtils.logMessage(TAG, "matchBean à nulle ");
            getFragmentManager().popBackStack();
            return null;
        }

        ma_tv_statut = (TextView) view.findViewById(R.id.ma_tv_statut);
        ma_tv_date = (TextView) view.findViewById(R.id.ma_tv_date);
        ma_tv_playing_time = (TextView) view.findViewById(R.id.ma_tv_playing_time);
        ma_tv_reel_playing_time = (TextView) view.findViewById(R.id.ma_tv_reel_playing_time);
        ma_tv_score = (TextView) view.findViewById(R.id.ma_tv_score);
        ma_tv_finish_stat = (TextView) view.findViewById(R.id.ma_tv_finish_stat);
        ma_iv_win = (ImageView) view.findViewById(R.id.ma_iv_win);
        ma_tv_finish_stat_title = (TextView) view.findViewById(R.id.ma_tv_finish_stat_title);
        st_empty = (TextView) view.findViewById(R.id.st_empty);
        st_rv = (RecyclerView) view.findViewById(R.id.st_rv);

        ma_tv_finish_stat.setOnClickListener(this);

        ma_iv_win.setColorFilter(getResources().getColor(R.color.yellow));

        //recycleview
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(lm = new LinearLayoutManager(generiqueActivity));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        sortList();
        adapter = new PointAdapter(generiqueActivity, matchBean.getPointBeanList(), this);
        st_rv.setAdapter(adapter);


        //On ajoute le 1er point
        if (matchBean.getPointBeanList().isEmpty()) {
            addNewPoint();
            matchBean.setCurrentPoint(1);
        }

        //On recalcule le point courant pour ne pas avoir d'incohérence mais si
        //le point courant correspon au dernier point terminée ou au premier point non commencé on ne touche pas
        MatchDaoManager.recalculateCurrentPoint(matchBean);

        //ON s'enregistre à OTTo
        ottoListner = new Object() {

            @Subscribe
            public void listen(OttoRefreshEvent event) {
                //On ecoute les evenement du live que si c'est le match courant
                if (MyApplication.getInstance().getLiveMatch() != null && MyApplication.getInstance().getLiveMatch().getId() == matchBean.getId()) {
                    gestionOttoEvent(event);
                }
            }

        };

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        MyApplication.getInstance().getBus().register(ottoListner);

        refreshView();
    }

    @Override
    public void onResume() {
        super.onResume();

        MatchBean liveMatch = MyApplication.getInstance().getLiveMatch();
        if (liveMatch == null || liveMatch.getId() != matchBean.getId()) {
            if (matchBean.getStart() != null && matchBean.getEnd()
                    == null) {
                //Si le match est en cours et que ce n'est pas le match courant on le passe en match courant
                MyApplication.getInstance().setLiveMatch(matchBean);
                generiqueActivity.refreshLivePoint();
            }
        }

        refreshView();

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //On regarde si le requestCode correspont  à un point pour le rafraichir
        if (requestCode != 0) {
            int i = 0;
            for (PointBean pointBean : matchBean.getPointBeanList()) {
                if (pointBean.getId() == requestCode) {
                    adapter.notifyItemChanged(i);
                    break;
                }
                i++;
            }

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().getBus().unregister(ottoListner);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_match, menu);
        menu.findItem(R.id.mm_end_game).setVisible(matchBean.getStart() != null && matchBean.getEnd() == null);

        super.onCreateOptionsMenu(menu, inflater);
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
                } else if (matchBean.getEnd() != null) {
                    showError(new TechnicalException("Click sur le menu terminé alors que le match est déjà terminé"), true);
                } else {
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
    }

    /* ---------------------------------
    // Adapter callback
    // -------------------------------- */

    @Override
    public void pointAdapter_click(PointBean bean) {

        //Si  ce match n'est pas terminé
        if (matchBean.getEnd() == null) {

            //Si on n'a pas de match en cours
            if (MyApplication.getInstance().getLiveMatch() == null) {
                //On positionne le match courant
                MyApplication.getInstance().setLiveMatch(matchBean);
                generiqueActivity.refreshLivePoint();
            }
            //ou si le match en cours n'est pas celui la et qu'il n'est pas commencé ou terminé.
            else if (MyApplication.getInstance().getLiveMatch().getId() != matchBean.getId()) {
                if (MyApplication.getInstance().getLiveMatch().getStart() == null || MyApplication.getInstance().getLiveMatch().getEnd() != null) {
                    //On positionne le match courant
                    MyApplication.getInstance().setLiveMatch(matchBean);
                    generiqueActivity.refreshLivePoint();
                }
            }
        }

        //On va sur le point
        generiqueActivity.gotoPoint(matchBean, bean);
    }

    @Override
    public void pointAdapter_deletePoint(PointBean bean) {

        int position = -1;
        for (int i = 0; i < matchBean.getPointBeanList().size(); i++) {
            if (matchBean.getPointBeanList().get(i).getId() == bean.getId()) {
                position = i;
                break;
            }
        }

        PointDaoManager.deletePoint(matchBean, bean, true);

        //on essaye de le retirer en mode optimiser
        if (position >= 0) {
            adapter.notifyItemRemoved(position);
        }
        //Sinon de maniere normal
        else {
            adapter.notifyDataSetChanged();
        }

        if (matchBean.getPointBeanList().isEmpty()) {
            refreshView();
        }

        //Si jamais c'est le point du live qui a ete modifié
        generiqueActivity.refreshLivePoint();

    }


    /* ---------------------------------
    // OTTO
    // -------------------------------- */

    private void gestionOttoEvent(OttoRefreshEvent event) {
        switch (event) {
            //Un point à été ajouté on met à jour l'UI
            case ADD_POINT:
                addNewPointUI();
                break;

            case MATCH_START:
            case SCORE_CHANGE:
                refreshView();
                break;

            case POINT_START:
            case POINT_FINISHED:
                adapter.notifyItemChanged(matchBean.getCurrentPoint());
                break;

            case CHANGE_POINT:
                //Rien à faire

                break;

        }
    }

    /* ---------------------------------
    // Click action
    // -------------------------------- */

    /**
     * Affiche l'etat des joueurs
     */
    private void statePlayerClick() {
        IntentHelper.goToMatchStatePlayer(getActivity(), matchBean.getId());
    }

    /**
     * affiche la liste des points du match
     */
    private void addNewPoint() {
        //on crée un nouveau point qu'on ajoute
        PointBean pointBean = new PointBean();
        pointBean.setMatchBean(matchBean);
        pointBean.setNumber(matchBean.getPointBeanList().size() + 1);
        pointBean.setId(PointDaoManager.getPointBeanDao().insert(pointBean));
        matchBean.getPointBeanList().add(0, pointBean);
        addNewPointUI();

    }

    private void addNewPointUI() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Change l'interface graphique
                if (matchBean.getPointBeanList().size() == 1) {
                    refreshView();
                }

                //On indique une insertion d'item
                adapter.notifyItemInserted(0);
                if (lm.findFirstCompletelyVisibleItemPosition() == 0) {
                    lm.scrollToPosition(0);
                }
            }
        });
    }

    /**
     * retmine le match
     */
    private void endGameClick() {

        //Pour terminer un match le match doit être démarré
        if (matchBean.getStart() == null) {
            showError(new TechnicalException("Click sur le menu terminé alors que le match n'a pas commencé"), true);
        } else if (matchBean.getEnd() != null) {
            showError(new TechnicalException("Click sur le menu terminé alors que le match est déjà terminé"), true);
        } else {
            //Pour terminer un match le point ne doit pas être en cours
            try {
                PointBean currentPoint = PointDaoManager.getPointNumber(matchBean.getPointBeanList(), matchBean.getCurrentPoint());
                if (currentPoint.getStart() != null && currentPoint.getStop() == null) {
                    ToastUtils.showToastOnUIThread(getActivity(), R.string.ma_end_game_point_in_progress_message, Toast.LENGTH_SHORT);
                } else {
                    matchBean.setEnd(new Date());
                    MatchDaoManager.getMatchBeanDao().update(matchBean);
                    refreshView();

                    MyApplication.getInstance().getBus().post(OttoRefreshEvent.MATCH_END);
                }
            } catch (TechnicalException e) {
                showError(e, true);
            }

        }
    }

    /**
     * Supprimer le match
     */
    private void deleteGame() {

        DialogUtils.getConfirmDialog(generiqueActivity, R.drawable.ic_action_delete, R.string.ma_menu_delete_match,
                getString(R.string.ma_delete_game_question), new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        //On supprime le match de la base et on termine l'activité
                        MatchDaoManager.deleteMatch(matchBean, true);
                        //On invalide la liste des matches
                        MyApplication.getInstance().getTeamBean().resetMatchBeanList();
                        //Si c'etait le match en cours on l'enleve
                        if (MyApplication.getInstance().getLiveMatch() != null
                                && matchBean.getId() == MyApplication.getInstance().getLiveMatch().getId()) {
                            MyApplication.getInstance().setLiveMatch(null);
                            generiqueActivity.refreshLivePoint();
                        }
                        //On clear la session
                        MyApplication.getInstance().getDaoSession().clear();

                        ToastUtils.showToastOnUIThread(generiqueActivity, R.string.ma_delete_game_confirmation, Toast.LENGTH_LONG);
                        //on revient en arriere
                        generiqueActivity.getFragmentManager().popBackStack();
                    }
                }).show();
    }

    /**
     * Renome le nom de l'équipe adverse
     */
    private void renameOpponent() {
        DialogUtils.getPromptDialog(generiqueActivity, R.drawable.ic_action_edit, R.string.ma_menu_rename_opponent, R.string.rename,
                matchBean.getName(), new DialogUtils.PromptDialogCB() {
                    @Override
                    public void promptDialogCB_onPositiveClick(String promptText) {
                        matchBean.setName(promptText);
                        MatchDaoManager.getMatchBeanDao().update(matchBean);
                        //On invalide la liste des matches
                        MyApplication.getInstance().getTeamBean().resetMatchBeanList();
                        //Pour prévenir qu'il faudra rafraichir
                        //setResult(Activity.RESULT_OK);
                        refreshView();
                    }
                }).show();
    }

    /**
     * Aller sur la page de statistique
     */
    private void statisticClick() {
        generiqueActivity.gotoStatistic(matchBean);
    }

    /* ---------------------------------
    // private
    // -------------------------------- */

    private void refreshView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //titre
                generiqueActivity.setTitle(getString(R.string.ma_title, MyApplication.getInstance().getTeamBean().getName(), matchBean.getName()));

                if (matchBean.getStart() != null) {

                    long reelTime = 0;
                    int team = 0, opponent = 0;
                    for (PointBean pointBean : matchBean.getPointBeanList()) {
                        reelTime += pointBean.getLength();

                        //Si le point est termine
                        if (pointBean.getTeamGoal() != null) {
                            if (pointBean.getTeamGoal()) {
                                team++;
                            } else {
                                opponent++;
                            }
                        }
                    }

                    //Statut
                    //match non terminée
                    if (matchBean.getEnd() == null) {
                        ma_tv_statut.setTextColor(getResources().getColor(R.color.in_progress_color));
                        ma_tv_statut.setText(getString(R.string.tm_list_statut_in_progress));
                        //Playing time
                        ma_tv_playing_time.setText(Utils.timeToHHMM(new Date().getTime() - matchBean.getStart().getTime()));

                        //Win
                        ma_iv_win.setVisibility(View.INVISIBLE);
                    } else {
                        ma_tv_statut.setTextColor(getResources().getColor(R.color.finish_color));
                        ma_tv_statut.setText(getString(R.string.tm_list_statut_finish));
                        //Playing time
                        ma_tv_playing_time.setText(Utils.timeToHHMM(matchBean.getEnd().getTime() - matchBean.getStart().getTime()));

                        //Win
                        ma_iv_win.setVisibility(team > opponent ? View.VISIBLE : View.INVISIBLE);
                    }

                    //Date
                    String dateFormat = DateUtils.getFormat(generiqueActivity, DateUtils.DATE_FORMAT.ddMMyyyy_HHmm);
                    ma_tv_date.setText(DateUtils.dateToString(matchBean.getStart(), dateFormat));

                    //reel playing time
                    ma_tv_reel_playing_time.setText(Utils.timeToHHMM(reelTime));

                    //Score
                    ma_tv_score.setText(team + " - " + opponent);

                    //bouton end game ou statistics
                    ma_tv_finish_stat_title.setVisibility(View.VISIBLE);
                    ma_tv_finish_stat.setVisibility(View.VISIBLE);
                    ma_tv_finish_stat.setText(matchBean.getEnd() == null ? getString(R.string.ma_menu_end_match) : getString(R.string.stat));

                }
                //Match non commencé
                else {

                    //Statut
                    ma_tv_statut.setText(getString(R.string.tm_list_statut_not_start));
                    ma_tv_statut.setTextColor(getResources().getColor(R.color.not_start_color));
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
                generiqueActivity.invalidateOptionsMenu();

                //RecycleView
                if (matchBean.getPointBeanList().isEmpty()) {
                    st_empty.setVisibility(View.VISIBLE);
                    st_rv.setVisibility(View.INVISIBLE);
                } else {
                    st_empty.setVisibility(View.INVISIBLE);
                    st_rv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sortList() {
        Collections.sort(matchBean.getPointBeanList(), new Comparator<PointBean>() {
            @Override
            public int compare(PointBean p1, PointBean p2) {
                //Trier par ordre inverse
                return p2.getNumber() - p1.getNumber();
            }
        });
    }


    /* ---------------------------------
    // Getter Setter
    // -------------------------------- */
    public void setMatchBean(MatchBean matchBean) {
        this.matchBean = matchBean;
    }
}
