package com.ultimatelinemanager.activity.match;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formation.utils.LogUtils;
import com.formation.utils.Utils;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.MainFragment;
import com.ultimatelinemanager.adapter.PlayerPointAdapter;
import com.ultimatelinemanager.adapter.PlayerPointWithHeaderAdapter;
import com.ultimatelinemanager.bean.PlayerPointBean;
import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;

import java.util.List;

import greendao.PlayerBean;
import greendao.PlayerPoint;
import greendao.PointBean;

/**
 * Created by amonteiro on 17/04/2015.
 */
public class PointFragment extends MainFragment implements PlayerPointAdapter.PlayerPointAdapterI, View.OnClickListener {

    private static final String TAG = LogUtils.getLogTag(PointFragment.class);

    //Composant graphique

    private ImageView pa_iv_handler;
    private ImageView pa_iv_middle;
    private ImageView pa_iv_girl;
    private ImageView pa_iv_boy;
    private ImageView pa_iv_alpha;
    private ImageView pa_iv_time;
    private ImageView pa_iv_sleep;

    private TextView paTvBoy;
    private TextView paTvGirl;

    //recycleview
    private RecyclerView pa_rv_playing;
    private RecyclerView paRvAll;
    private LinearLayoutManager lmAll, lmPlaying;
    private PlayerPointAdapter noPlayingAdapter;
    private PlayerPointWithHeaderAdapter playerInPointAdapter;

    //data
    private PointBean pointBean;
    private int filtreSelectedColor;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_point, container, false);

        if (pointBean == null) {
            LogUtils.logMessage(TAG, "PointBean à nulle");
            getFragmentManager().popBackStack();
            return null;
        }

        pa_iv_handler = (ImageView) view.findViewById(R.id.pa_iv_handler);
        pa_iv_middle = (ImageView) view.findViewById(R.id.pa_iv_middle);
        pa_iv_girl = (ImageView) view.findViewById(R.id.pa_iv_girl);
        pa_iv_boy = (ImageView) view.findViewById(R.id.pa_iv_boy);
        pa_iv_alpha = (ImageView) view.findViewById(R.id.pa_iv_alpha);
        pa_iv_time = (ImageView) view.findViewById(R.id.pa_iv_time);
        pa_iv_sleep = (ImageView) view.findViewById(R.id.pa_iv_sleep);
        paRvAll = (RecyclerView) view.findViewById(R.id.pa_rv_all);
        paTvBoy = (TextView) view.findViewById(R.id.pa_tv_boy);
        paTvGirl = (TextView) view.findViewById(R.id.pa_tv_girl);
        pa_rv_playing = (RecyclerView) view.findViewById(R.id.pa_rv_playing);

        pa_iv_handler.setOnClickListener(this);
        pa_iv_middle.setOnClickListener(this);
        pa_iv_girl.setOnClickListener(this);
        pa_iv_boy.setOnClickListener(this);
        pa_iv_alpha.setOnClickListener(this);
        pa_iv_time.setOnClickListener(this);
        pa_iv_sleep.setOnClickListener(this);

        filtreSelectedColor = Utils.getColorFromTheme(getActivity(), R.attr.color_composant_main_highlighted);

        pa_iv_handler.setColorFilter(Color.BLACK);
        pa_iv_middle.setColorFilter(Color.BLACK);
        pa_iv_girl.setColorFilter(Color.BLACK);
        pa_iv_boy.setColorFilter(Color.BLACK);
        pa_iv_alpha.setColorFilter(Color.BLACK);
        pa_iv_time.setColorFilter(Color.BLACK);
        pa_iv_sleep.setColorFilter(Color.BLACK);

        //On utilise le titre du match et non celui en selection dans le cas ou est en visite sur un autre match
        getActivity().setTitle(getString(R.string.ma_title, pointBean.getMatchBean().getTeamBean().getName(), pointBean.getMatchBean().getName()));

        switchFiltreImageViewColor(pa_iv_alpha, true);

        initRecycleView();
        initList();
        refreshView();

        return view;

    }

    /* ---------------------------------
    // click
    // -------------------------------- */
    @Override
    public void playerPointAdapter_onClick(PlayerPointBean playerPointBean) {
        //Le joueur ne joue pas on le passe dans la liste des joueurs qui joue à son role de base
        if (playerPointBean.getRoleInPoint() == null) {
            noPlayingAdapter.removeItem(playerPointBean.getPlayerBean().getId());
            playerInPointAdapter.addItem(playerPointBean, Role.getRole(playerPointBean.getPlayerBean().getRole()));
        }
        else {
            //Le joueur joue on le replace dans la liste des joueurs qui ne joue pas
            playerInPointAdapter.removeItem(playerPointBean.getPlayerBean().getId());
            noPlayingAdapter.addItem(playerPointBean);
        }

        refreshView();

    }

    @Override
    public void onClick(View v) {

        if (v == pa_iv_handler) {
            switchFiltreImageViewColor(pa_iv_handler, !pa_iv_handler.isSelected());
            switchFiltreImageViewColor(pa_iv_middle, false);
            if (pa_iv_handler.isSelected()) {
                noPlayingAdapter.setFilterRole(Role.Handler);
            }
            else {
                noPlayingAdapter.setFilterRole(Role.Both);
            }
            noPlayingAdapter.refreshFilterList();
        }
        else if (v == pa_iv_middle) {
            switchFiltreImageViewColor(pa_iv_middle, !pa_iv_middle.isSelected());
            switchFiltreImageViewColor(pa_iv_handler, false);
            if (pa_iv_middle.isSelected()) {
                noPlayingAdapter.setFilterRole(Role.Middle);
            }
            else {
                noPlayingAdapter.setFilterRole(Role.Both);
            }
            noPlayingAdapter.refreshFilterList();

        }
        else if (v == pa_iv_girl) {
            if (pa_iv_girl.isSelected()) {
                switchFiltreImageViewColor(pa_iv_girl, false);
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.BOTH);
            }
            else {
                pa_iv_girl.setSelected(true);
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.GIRL);
                pa_iv_girl.setColorFilter(getResources().getColor(R.color.girl_color));
            }
            switchFiltreImageViewColor(pa_iv_boy, false);
            noPlayingAdapter.refreshFilterList();
        }
        else if (v == pa_iv_boy) {
            if (pa_iv_boy.isSelected()) {
                switchFiltreImageViewColor(pa_iv_boy, false);
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.BOTH);
            }
            else {
                pa_iv_boy.setSelected(true);
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.BOY);
                pa_iv_boy.setColorFilter(getResources().getColor(R.color.boy_color));
            }
            switchFiltreImageViewColor(pa_iv_girl, false);
            noPlayingAdapter.refreshFilterList();

        }
        else if (v == pa_iv_alpha) {
            if (!pa_iv_alpha.isSelected()) {
                switchFiltreImageViewColor(pa_iv_alpha, true);
                switchFiltreImageViewColor(pa_iv_time, false);
                switchFiltreImageViewColor(pa_iv_sleep, false);
                noPlayingAdapter.setSortOrder(PlayerPointAdapter.SortOrder.AZ);
                noPlayingAdapter.refreshFilterList();
            }

        }
        else if (v == pa_iv_time) {
            if (!pa_iv_time.isSelected()) {
                switchFiltreImageViewColor(pa_iv_time, true);
                switchFiltreImageViewColor(pa_iv_alpha, false);
                switchFiltreImageViewColor(pa_iv_sleep, false);
                noPlayingAdapter.setSortOrder(PlayerPointAdapter.SortOrder.PLAYING_TIME);
                noPlayingAdapter.refreshFilterList();
            }

        }
        else if (v == pa_iv_sleep) {
            if (!pa_iv_sleep.isSelected()) {
                switchFiltreImageViewColor(pa_iv_sleep, !pa_iv_sleep.isSelected());
                switchFiltreImageViewColor(pa_iv_time, false);
                switchFiltreImageViewColor(pa_iv_alpha, false);
                noPlayingAdapter.setSortOrder(PlayerPointAdapter.SortOrder.SLEEP_TIME);
                noPlayingAdapter.refreshFilterList();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //On enregistre les modifications en base
        PointDaoManager.savePlayerPointList(pointBean, playerInPointAdapter.getDaoList());
    }

    /* ---------------------------------
    // private graphique
    // -------------------------------- */

    private void refreshView() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int nbBoy = 0, nbGirl = 0;
                for (PlayerPointBean playerPointBean : playerInPointAdapter.getDaoList()) {
                    if (playerPointBean.getPlayerBean().getSexe()) {
                        nbBoy++;
                    }
                    else {
                        nbGirl++;
                    }
                }

                paTvBoy.setText(nbBoy + "");
                paTvGirl.setText(nbGirl + "");

            }
        });

    }

    /* ---------------------------------
    // private
    // -------------------------------- */

    /**
     * Initialise les liste en plancant chaque joueur a son poste pour le point
     */
    private void initList() {
        //Joueur jouant ce point
        List<PlayerPoint> playerPointList = pointBean.getPlayerPointList();

        //L'ensemble des joueur de l'equipe et On l'ajoute la liste qui lui correspond
        PlayerPoint temp;
        PlayerPointBean playerPointBean;
        for (PlayerBean playerBean : PlayerDaoManager.getPlayers(pointBean.getMatchBean().getTeamId())) {
            temp = null;
            playerPointBean = new PlayerPointBean(playerBean);
            //On regarde si le joueur joue quelque part
            for (PlayerPoint playerPoint : playerPointList) {
                if (playerPoint.getId().equals(playerBean.getId())) {
                    temp = playerPoint;
                    break;
                }
            }

            //Il ne joue pas
            if (temp == null) {
                noPlayingAdapter.getDaoList().add(playerPointBean);
            }
            else {
                //Il joue
                playerPointBean.setRoleInPoint(Role.valueOf(temp.getRole()));
                playerInPointAdapter.getDaoList().add(playerPointBean);
            }
        }

        //on trie la liste des joueur qui joue par role
        playerInPointAdapter.sortList();
        playerInPointAdapter.refreshList();

        noPlayingAdapter.refreshFilterList();
        playerInPointAdapter.notifyDataSetChanged();
    }

    private void initRecycleView() {

        paRvAll.setHasFixedSize(false);
        paRvAll.setLayoutManager(lmAll = new LinearLayoutManager(getActivity()));
        paRvAll.setItemAnimator(new DefaultItemAnimator());

        pa_rv_playing.setHasFixedSize(false);
        pa_rv_playing.setLayoutManager(lmPlaying = new LinearLayoutManager(getActivity()));
        pa_rv_playing.setItemAnimator(new DefaultItemAnimator());

        paRvAll.setAdapter(noPlayingAdapter = new PlayerPointAdapter(getActivity(), this));
        pa_rv_playing.setAdapter(playerInPointAdapter = new PlayerPointWithHeaderAdapter(getActivity(), this));

    }

    private void switchFiltreImageViewColor(ImageView iv, boolean selected) {

        if (selected) {
            iv.setSelected(true);
            iv.setColorFilter(filtreSelectedColor);
        }
        else {
            iv.setSelected(false);
            iv.setColorFilter(Color.BLACK);
        }

    }

    /* ---------------------------------
    // Getter / setter
    // -------------------------------- */

    public PointBean getPointBean() {
        return pointBean;
    }

    public void setPointBean(PointBean pointBean) {
        this.pointBean = pointBean;
    }

}
