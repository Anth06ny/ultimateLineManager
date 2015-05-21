package com.ultimatelinemanager.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.LogUtils;
import com.formation.utils.Utils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.adapter.SelectAdapter;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import greendao.MatchBean;
import greendao.PlayerBean;
import greendao.TeamBean;

public class TeamFragment extends MainFragment implements SelectAdapter.SelectAdapterI, TabHost.OnTabChangeListener {

    private static final String TAG = LogUtils.getLogTag(TeamFragment.class);
    private static final String TAB_TAG_KEY = "TAB_TAG_KEY";

    //Composant graphique
    private MaterialDialog dialog;
    private RecyclerView ta_rv_match, ta_rv_players;
    private TextView ta_empty_match, ta_empty_players;
    private ImageView icon_tab_match, icon_tab_players;
    private TabHost tabs;
    private String tabSelected = null;

    //Autre
    private SelectAdapter adapterMatch, adapterPlayer;
    private LinearLayoutManager lmMatch, lmPlayers;
    protected ArrayList<MatchBean> matchBeansList;
    protected ArrayList<PlayerBean> playerBeanList;
    private static String TAG_PLAYERS, TAG_MATCH;

    //autre
    private int color_composant_main;
    private TeamBean teamBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_team, container, false);

        color_composant_main = Utils.getColorFromTheme(generiqueActivity, R.attr.color_composant_main);

        ta_empty_match = (TextView) view.findViewById(R.id.ta_empty_match);
        ta_empty_players = (TextView) view.findViewById(R.id.ta_empty_players);
        ta_rv_match = (RecyclerView) view.findViewById(R.id.ta_rv_match);
        ta_rv_players = (RecyclerView) view.findViewById(R.id.ta_rv_players);

        teamBean = MyApplication.getInstance().getTeamBean();

        TAG_MATCH = getString(R.string.ta_bt_games);
        TAG_PLAYERS = getString(R.string.ta_bt_players);

        tabs = (TabHost) view.findViewById(R.id.tabHost);
        initTabHost(view);
        initRecycleView();

        //List match
        matchBeansList = new ArrayList<>();
        reloadMatch();
        adapterMatch = new SelectAdapter(generiqueActivity, matchBeansList, SelectAdapter.TYPE.MATCH, this);

        playerBeanList = new ArrayList<>();
        reloadPlayer();
        adapterPlayer = new SelectAdapter(generiqueActivity, playerBeanList, SelectAdapter.TYPE.PLAYER, this);

        ta_rv_match.setAdapter(adapterMatch);
        ta_rv_players.setAdapter(adapterPlayer);

        refreshTitle();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshView();
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_team, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_rename:
                //on demande le nouveau nom
                dialog = DialogUtils.getPromptDialog(generiqueActivity, R.drawable.ic_action_add_group, R.string.st_ask_team_name, R.string.add,
                        teamBean.getName(), new DialogUtils.PromptDialogCB() {
                            @Override
                            public void promptDialogCB_onPositiveClick(String promptText) {
                                teamBean.setName(promptText);
                                TeamDaoManager.getTeamDAO().update(teamBean);
                                refreshTitle();
                            }
                        });
                dialog.show();

                return true;

            case R.id.menu_delete:
                dialog = DialogUtils.getConfirmDialog(generiqueActivity, R.drawable.ic_action_delete, R.string.delete,
                        getString(R.string.ta_delete_confirmation), new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                deleteTeam();
                            }
                        });

                dialog.show();

                return true;

            case R.id.menu_add_team:
                DialogUtils.getPromptDialog(generiqueActivity, R.drawable.ic_action_add_group, R.string.tm_pop_up_new_match_title, R.string.add, "",
                        new DialogUtils.PromptDialogCB() {
                            @Override
                            public void promptDialogCB_onPositiveClick(String promptText) {
                                addMatch(promptText);
                                refreshView();
                            }
                        }).show();
                return true;

            case R.id.menu_add_player:
                generiqueActivity.gotoPickPlayer(teamBean.getId());
                //IntentHelper.goToPickPlayer(this, teamBean.getId(), Constante.PICK_PLAYER_REQ_CODE);
                return true;

            case R.id.menu_switch_team:
                generiqueActivity.goToSelectTeamActivity();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ---------------------------------
    // Event
    // -------------------------------- */

    @Override
    public void selectAdapter_onClick(Object bean) {
        if (bean instanceof MatchBean) {
            //on redirige sur le match
            generiqueActivity.gotoMatch((MatchBean) bean);
        } else if (bean instanceof PlayerBean) {

            //On affiche la fenetre de modification du joueur
            //On affiche la page de séléction des joueurs enregistré dans le téléphone
            DialogUtils.getNewPlayerDialog(getActivity(), (PlayerBean) bean, R.string.ta_edit_player, R.string.save, new
                    DialogUtils
                            .NewPlayerPromptDialogCB() {
                        @Override
                        public void newPlayerpromptDialogCB_onPositiveClick(PlayerBean playerBean) {
                            //on ajoute le nouveau joueur
                            PlayerDaoManager.getPlayerDAO().update(playerBean);
                            adapterPlayer.notifyDataSetChanged();
                        }
                    }).show();
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        tabSelected = tabId;
        if (tabId.equals(TAG_MATCH)) {
            //on change le color filter
            icon_tab_match.setColorFilter(color_composant_main);
            icon_tab_players.setColorFilter(Color.BLACK);

        } else if (tabId.equals(TAG_PLAYERS)) {
            icon_tab_match.setColorFilter(Color.BLACK);
            icon_tab_players.setColorFilter(color_composant_main);
        }
    }

    /* ---------------------------------
    // private Data
    // -------------------------------- */

    /**
     * Met a jour la liste de match depuis la bdd
     */
    private void reloadMatch() {
        teamBean.resetMatchBeanList();
        matchBeansList.clear();
        matchBeansList.addAll(teamBean.getMatchBeanList());

        Collections.sort(matchBeansList, new Comparator<MatchBean>() {
            @Override
            public int compare(MatchBean lhs, MatchBean rhs) {

                if (lhs.getStart() == null) {
                    return -1;
                } else if (rhs.getStart() == null) {
                    return 1;
                } else {
                    return (int) (rhs.getStart().getTime() - lhs.getStart().getTime());
                }
            }
        });

        if (adapterMatch != null) {
            adapterMatch.notifyDataSetChanged();
        }
    }

    private void reloadPlayer() {
        teamBean.resetTeamPlayerList();
        playerBeanList.clear();
        playerBeanList.addAll(PlayerDaoManager.getPlayers(teamBean));

        if (adapterPlayer != null) {
            adapterPlayer.notifyDataSetChanged();
        }
    }

    /* ---------------------------------
    // private Graphique
    // -------------------------------- */

    private void refreshTitle() {
        generiqueActivity.setTitle(StringUtils.capitalize(teamBean.getName()));
    }

    private void initTabHost(View view) {
        int dimen = getResources().getDimensionPixelSize(R.dimen.margin_5);

        tabs.setup();

        //tabs match
        TabHost.TabSpec spec = tabs.newTabSpec(TAG_MATCH);//le tag
        spec.setContent(R.id.ta_rl_match);
        spec.setIndicator(spec.getTag()); //le label
        tabs.addTab(spec);
        //on met l'image
        icon_tab_match = (ImageView) tabs.getTabWidget().getChildTabViewAt(0).findViewById(android.R.id.icon);
        icon_tab_match.setVisibility(View.VISIBLE);
        icon_tab_match.setImageResource(R.drawable.ic_action_group);
        icon_tab_match.setColorFilter(color_composant_main, PorterDuff.Mode.MULTIPLY);
        icon_tab_match.setAdjustViewBounds(true);
        icon_tab_match.setPadding(dimen, dimen, dimen, dimen);

        //tabs players
        spec = tabs.newTabSpec(TAG_PLAYERS);//le tag
        spec.setContent(R.id.ta_rl_players);
        spec.setIndicator(spec.getTag()); //le label
        tabs.addTab(spec);
        //on met l'image
        icon_tab_players = (ImageView) tabs.getTabWidget().getChildTabViewAt(1).findViewById(android.R.id.icon);
        icon_tab_players.setVisibility(View.VISIBLE);
        icon_tab_players.setImageResource(R.drawable.ic_action_user);
        icon_tab_players.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        icon_tab_players.setAdjustViewBounds(true);
        icon_tab_players.setPadding(dimen, dimen, dimen, dimen);

        tabs.setOnTabChangedListener(this);

        if (StringUtils.isNotBlank(tabSelected)) {
            tabs.setCurrentTabByTag(tabSelected);
        }
    }

    private void initRecycleView() {
        //preparation recycleview
        ta_rv_match.setHasFixedSize(false);
        ta_rv_players.setHasFixedSize(false);
        ta_rv_match.setLayoutManager(lmMatch = new LinearLayoutManager(generiqueActivity));
        ta_rv_players.setLayoutManager(lmPlayers = new LinearLayoutManager(generiqueActivity));
        ta_rv_match.setItemAnimator(new DefaultItemAnimator());
        ta_rv_players.setItemAnimator(new DefaultItemAnimator());
    }

    private void refreshView() {
        generiqueActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (playerBeanList.size() > 0) {
                    ta_empty_players.setVisibility(View.INVISIBLE);
                    ta_rv_players.setVisibility(View.VISIBLE);
                } else {
                    ta_empty_players.setVisibility(View.VISIBLE);
                    ta_rv_players.setVisibility(View.INVISIBLE);
                }

                if (matchBeansList.size() > 0) {
                    ta_empty_match.setVisibility(View.INVISIBLE);
                    ta_rv_match.setVisibility(View.VISIBLE);
                } else {
                    ta_empty_match.setVisibility(View.VISIBLE);
                    ta_rv_match.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /* ---------------------------------
    // private
    // -------------------------------- */
    private void addMatch(String oponentName) {
        MatchBean matchBean = new MatchBean();
        matchBean.setName(oponentName);
        matchBean.setTeamBean(teamBean);
        matchBean.setCurrentPoint(1);
        MatchDaoManager.getMatchBeanDao().insert(matchBean);

        //On l'ajoute en tete pour ne pas avoir à refaire des appel bdd
        matchBeansList.add(0, matchBean);
        //Mais on invalide la liste pour prendre en compte le changement
        teamBean.resetMatchBeanList();

        //insertion d'item
        adapterMatch.notifyItemInserted(0);
        if (lmMatch.findFirstCompletelyVisibleItemPosition() == 0) {
            lmMatch.scrollToPosition(0);
        }

    }


    private void deleteTeam() {
        //on suprime le TeamBean
        TeamDaoManager.deleteTeam(teamBean);

        generiqueActivity.goToSelectTeamActivity();
    }

}
