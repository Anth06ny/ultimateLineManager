package com.ultimatelinemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.LogUtils;
import com.formation.utils.Utils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.adapter.SelectAdapter;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.dao.TeamPlayerManager;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.IntentHelper;
import com.ultimatelinemanager.metier.exception.ExceptionA;
import com.ultimatelinemanager.metier.exception.LogicException;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import greendao.MatchBean;
import greendao.PlayerBean;
import greendao.TeamBean;

public class TeamActivity extends GeneriqueActivity implements SelectAdapter.SelectAdapterI, TabHost.OnTabChangeListener {

    private static final String TAG = LogUtils.getLogTag(TeamActivity.class);

    //Composant graphique
    private MaterialDialog dialog;
    private RecyclerView ta_rv_match, ta_rv_players;
    private TextView ta_empty_match, ta_empty_players;
    private ImageView icon_tab_match, icon_tab_players;

    //Autre
    private SelectAdapter adapterMatch, adapterPlayer;
    private LinearLayoutManager lmMatch, lmPlayers;
    protected ArrayList<MatchBean> matchBeansList;
    protected ArrayList<PlayerBean> playerBeanList;
    private static String TAG_PLAYERS, TAG_MATCH;

    //autre
    private TeamBean teamBean;
    private int color_composant_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        long teamId = getIntent().getLongExtra(Constante.TEAM_EXTRA_ID, -1);
        teamBean = TeamDaoManager.getTeamDAO().load(teamId);

        if (teamBean == null) {
            LogUtils.logMessage(TAG, "teamBean à nulle, teamId=" + teamId);
            finish();
            return;
        }

        color_composant_main = Utils.getColorFromTheme(this, R.attr.color_composant_main);

        ta_empty_match = (TextView) findViewById(R.id.ta_empty_match);
        ta_empty_players = (TextView) findViewById(R.id.ta_empty_players);
        ta_rv_match = (RecyclerView) findViewById(R.id.ta_rv_match);
        ta_rv_players = (RecyclerView) findViewById(R.id.ta_rv_players);

        TAG_MATCH = getString(R.string.ta_bt_games);
        TAG_PLAYERS = getString(R.string.ta_bt_players);

        initTabHost();

        initRecycleView();

        //List match
        matchBeansList = new ArrayList<>();
        matchBeansList.addAll(teamBean.getMatchBeanList());
        adapterMatch = new SelectAdapter(this, matchBeansList, SelectAdapter.TYPE.MATCH, this);

        playerBeanList = new ArrayList<>();
        playerBeanList.addAll(PlayerDaoManager.getPlayers(teamBean));
        adapterPlayer = new SelectAdapter(this, playerBeanList, SelectAdapter.TYPE.PLAYER, this);

        ta_rv_match.setAdapter(adapterMatch);
        ta_rv_players.setAdapter(adapterPlayer);

        refreshTitle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constante.PICK_PLAYER_REQ_CODE && resultCode == Activity.RESULT_OK) {
            //On recupere l'id de notre joueur
            try {
                long newPlayer = data.getExtras().getLong(Constante.PLAYER_EXTRA_ID, -1);
                if (newPlayer != -1) {
                    addPlayer(newPlayer);
                }
                else {
                    throw new TechnicalException("Le playerId n'a pas été transmit");
                }

            }
            catch (ExceptionA e) {
                showError(e, true);
            }

        }
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_rename:
                //on demande le nouveau nom
                dialog = DialogUtils.getPromptDialog(this, R.drawable.ic_action_add_group, R.string.st_ask_team_name, R.string.add,
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
                dialog = DialogUtils.getConfirmDialog(this, R.drawable.ic_action_delete, R.string.delete, getString(R.string.ta_delete_confirmation),
                        new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                deleteTeam();
                            }
                        });

                dialog.show();

                return true;

            case R.id.menu_add_team:
                DialogUtils.getPromptDialog(this, R.drawable.ic_action_add_group, R.string.tm_pop_up_new_match_title, R.string.add, "",
                        new DialogUtils.PromptDialogCB() {
                            @Override
                            public void promptDialogCB_onPositiveClick(String promptText) {
                                addMatch(promptText);
                            }
                        }).show();
                return true;

            case R.id.menu_add_player:
                IntentHelper.goToPickPlayer(this, teamBean.getId(), Constante.PICK_PLAYER_REQ_CODE);
                return true;

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
            IntentHelper.goToMatch(this, ((MatchBean) bean).getId(), true);
        }
        else if (bean instanceof PlayerBean) {
            //On va sur la page d'un joueur , pas encore faite.
            IntentHelper.goToPlayerPage(this, ((PlayerBean) bean).getId());
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equals(TAG_MATCH)) {
            //on change le color filter
            icon_tab_match.setColorFilter(color_composant_main);
            icon_tab_players.setColorFilter(Color.BLACK);
        }
        else if (tabId.equals(TAG_PLAYERS)) {
            icon_tab_match.setColorFilter(Color.BLACK);
            icon_tab_players.setColorFilter(color_composant_main);
        }
    }

    /* ---------------------------------
    // private Graphique
    // -------------------------------- */

    private void refreshTitle() {
        setTitle(StringUtils.capitalize(teamBean.getName()));
    }

    private void initTabHost() {
        int dimen = getResources().getDimensionPixelSize(R.dimen.margin_5);

        //Init du Tabhost
        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
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
    }

    private void initRecycleView() {
        //preparation recycleview
        ta_rv_match.setHasFixedSize(false);
        ta_rv_players.setHasFixedSize(false);
        ta_rv_match.setLayoutManager(lmMatch = new LinearLayoutManager(this));
        ta_rv_players.setLayoutManager(lmPlayers = new LinearLayoutManager(this));
        ta_rv_match.setItemAnimator(new DefaultItemAnimator());
        ta_rv_players.setItemAnimator(new DefaultItemAnimator());
    }

    private void refreshView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (playerBeanList.size() > 0) {
                    ta_empty_players.setVisibility(View.INVISIBLE);
                    ta_rv_players.setVisibility(View.VISIBLE);
                }
                else {
                    ta_empty_players.setVisibility(View.VISIBLE);
                    ta_rv_players.setVisibility(View.INVISIBLE);
                }

                if (matchBeansList.size() > 0) {
                    ta_empty_match.setVisibility(View.INVISIBLE);
                    ta_rv_match.setVisibility(View.VISIBLE);
                }
                else {
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
        MatchDaoManager.getMatchBeanDao().insert(matchBean);

        //On l'ajoute en tete pour ne pas avoir à refaire des appel bdd
        matchBeansList.add(0, matchBean);

        //insertion d'item
        adapterMatch.notifyItemInserted(0);
        if (lmMatch.findFirstCompletelyVisibleItemPosition() == 0) {
            lmMatch.scrollToPosition(0);
        }

    }

    private void addPlayer(long newPlayerId) throws LogicException {
        //On ajoute notre nouveau joueur à l'equipe
        TeamPlayerManager.addPlayerToTeam(teamBean.getId(), newPlayerId);
        //ON recharge la liste
        playerBeanList.clear();
        playerBeanList.addAll(PlayerDaoManager.getPlayers(teamBean));
        adapterPlayer.notifyDataSetChanged();

        //On met à jour la liste et la vue
        refreshView();
    }

    private void deleteTeam() {
        TeamDaoManager.getTeamDAO().delete(teamBean);
        finish();
    }

}
