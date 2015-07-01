package com.ultimatelinemanager.activity.list_players;

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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.MainFragment;
import com.ultimatelinemanager.adapter.PlayerPickerAdapter;
import com.ultimatelinemanager.bean.Pair;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.TeamPlayerManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.exception.LogicException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import greendao.PlayerBean;
import greendao.TeamPlayer;

/**
 * Classe permetant d'afficher une liste de joueur
 */
public class PickerPlayerFragment extends MainFragment implements PlayerPickerAdapter.PlayerPickerCB {

    //Composants graphiques
    private RecyclerView st_rv;
    private TextView st_empty;
    protected TextView st_info;

    //Autre
    protected PlayerPickerAdapter adapter;
    protected ArrayList<Pair<Boolean, PlayerBean>> playerBeanList;
    //Equipe dont on veut exclure les joueurs
    private long teamBeanId;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_activity_layout, container, false);

        //RecylceView
        st_empty = (TextView) view.findViewById(R.id.st_empty);
        st_rv = (RecyclerView) view.findViewById(R.id.st_rv);
        st_info = (TextView) view.findViewById(R.id.st_info);

        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        //Utils.getColorFromTheme(this, R.attr.color_application_bg)
        adapter = new PlayerPickerAdapter(getActivity(), playerBeanList = new ArrayList<>(), this);

        st_rv.setAdapter(adapter);

        getActivity().setTitle(R.string.lpt_no_team_title);
        st_info.setText(R.string.lpt_no_team_info);

        refreshList();

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
        inflater.inflate(R.menu.menu_list_player, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem menu_add_player = menu.findItem(R.id.menu_add_player);
        MenuItem menu_add = menu.findItem(R.id.menu_add);

        //Si des items selectionnées on affiche le bouton ajouter
        menu_add_player.setVisible(false);
        for (Pair<Boolean, PlayerBean> s : playerBeanList) {
            if (s.first) {
                menu_add_player.setVisible(true);
                break;
            }
        }
        menu_add.setVisible(!menu_add_player.isVisible());

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                //On affiche la page de séléction des joueurs enregistré dans le téléphone
                DialogUtils.getNewPlayerDialog(getActivity(), null, R.string.lpt_bt_new, R.string.add, new DialogUtils.NewPlayerPromptDialogCB() {
                    @Override
                    public void newPlayerpromptDialogCB_onPositiveClick(PlayerBean playerBean) {
                        //on ajoute le nouveau joueur
                        PlayerDaoManager.getPlayerDAO().insert(playerBean);
                        refreshList();
                        refreshView();
                    }
                }).show();
                return true;

            case R.id.menu_add_player:
                //On ajoute l'ensemble des joueurs séléctionné à l'equipe
                for (Pair<Boolean, PlayerBean> s : playerBeanList) {
                    if (s.first) {
                        try {
                            TeamPlayerManager.addPlayerToTeam(teamBeanId, s.second.getId());
                        }
                        catch (LogicException e) {
                            showError(e, true);
                            return true;
                        }
                    }
                }
                generiqueActivity.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /* ---------------------------------
    // CallBack
    // -------------------------------- */

    @Override
    public void PlayerPickerCB_onDeleteClick(final Pair<Boolean, PlayerBean> bean) {
        DialogUtils.getConfirmDialog(getActivity(), R.drawable.ic_action_delete, R.string.lpt_delete_player,
                getString(R.string.lpt_delete_player_text), new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        //On supprime le joueur de l'équipe
                        if (bean.second.getTeamPlayerList().size() > 0) {
                            String teamName = "";
                            for (TeamPlayer teamPlayer : bean.second.getTeamPlayerList()) {
                                teamName += " - " + teamPlayer.getTeamBean().getName()
                                        + StringUtils.stripToEmpty(teamPlayer.getTeamBean().getTournament()) + "\n";
                            }

                            ToastUtils.showToastOnUIThread(getActivity(), getString(R.string.lpt_delete_player_refused, teamName), Toast.LENGTH_LONG);
                        }
                        else {
                            //on peut supprimer le joueur
                            PlayerDaoManager.getPlayerDAO().delete(bean.second);
                            int index = playerBeanList.indexOf(bean);
                            if (index >= 0) {
                                playerBeanList.remove(index);
                                adapter.notifyItemRemoved(index);
                            }
                        }

                    }
                }).show();
    }

    @Override
    public void PlayerPickerCB_clickOnItem(Pair<Boolean, PlayerBean> bean) {
        //On affiche masque le bouton ajout
        //        st_tv_ajouter.setVisibility(View.GONE);
        //        for (Pair<Boolean, PlayerBean> s : playerBeanList) {
        //            if (s.first) {
        //                st_tv_ajouter.setVisibility(View.VISIBLE);
        //                break;
        //            }
        //        }
        getActivity().invalidateOptionsMenu();
    }

    /* ---------------------------------
    // Autre
    // -------------------------------- */

    protected void refreshList() {
        playerBeanList.clear();

        for (PlayerBean pb : PlayerDaoManager.getPlayerNotInTeam(teamBeanId)) {
            playerBeanList.add(new Pair(false, pb));
        }

    }

    protected void refreshView() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

                if (playerBeanList.size() > 0) {
                    st_empty.setVisibility(View.INVISIBLE);
                    st_rv.setVisibility(View.VISIBLE);
                }
                else {
                    st_empty.setVisibility(View.VISIBLE);
                    st_rv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /* ---------------------------------
    // getter / setter
    // -------------------------------- */

    public void setTeamBeanId(long teamBeanId) {
        this.teamBeanId = teamBeanId;
    }

}
