package com.ultimatelinemanager.activity.match;

import android.graphics.Color;
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

import com.formation.utils.LogUtils;
import com.formation.utils.Utils;
import com.squareup.otto.Subscribe;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.MainFragment;
import com.ultimatelinemanager.adapter.PlayerPointAdapter;
import com.ultimatelinemanager.adapter.PlayerPointWithHeaderAdapter;
import com.ultimatelinemanager.bean.OttoRefreshEvent;
import com.ultimatelinemanager.bean.PlayerPointBean;
import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;
import com.ultimatelinemanager.metier.composant.OnSwipeTouchListener;

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

    private View root;

    //recycleview
    private RecyclerView pa_rv_playing;
    private RecyclerView paRvAll;
    private LinearLayoutManager lmAll, lmPlaying;
    private PlayerPointAdapter noPlayingAdapter;
    private PlayerPointWithHeaderAdapter playerInPointAdapter;

    //data
    private PointBean pointBean;
    private Integer numOfPoint;
    private int filtreSelectedColor;

    //Otto
    private Object ottoListner;

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

        root = view.findViewById(R.id.root);
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
        String numPoint = numOfPoint != null ? ("P" + numOfPoint) : "-";
        getActivity().setTitle(
                getString(R.string.pa_title, pointBean.getMatchBean().getTeamBean().getName(), pointBean.getMatchBean().getName(), numPoint));

        switchFiltreImageViewColor(pa_iv_alpha, true);

        root.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                gotoNextPoint();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                gotoPreviousPoint();
            }
        });

        initRecycleView();
        initList();
        refreshView();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        //ON s'enregistre à OTTo
        ottoListner = new Object() {

            @Subscribe
            public void listen(OttoRefreshEvent event) {
                //On ecoute les evenement du live que si c'est le match en cours
                if (pointBean.getMatchBean().getId() == MyApplication.getInstance().getLiveMatch().getId()) {
                    gestionOttoEvent(event);
                }
            }

        };
        MyApplication.getInstance().getBus().register(ottoListner);

    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().getBus().unregister(ottoListner);
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_point, menu);

        //Si c'est le dernier point du match
        if (pointBean.getMatchBean().getPointBeanList().indexOf(pointBean) == 0) {
            menu.findItem(R.id.mp_next).setVisible(false);
        }

        //si c'est le premier du match
        if (pointBean.getMatchBean().getPointBeanList().indexOf(pointBean) == pointBean.getMatchBean().getPointBeanList().size() - 1) {
            menu.findItem(R.id.mp_previous).setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mp_next:
                gotoNextPoint();
                return true;

            case R.id.mp_previous:
                gotoPreviousPoint();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /* ---------------------------------
    // Click
    // -------------------------------- */
    @Override
    public void playerPointAdapter_onClick(PlayerPointBean playerPointBean) {
        //Le joueur ne joue pas on le passe dans la liste des joueurs qui joue à son role de base
        if (playerPointBean.getRoleInPoint() == null) {
            noPlayingAdapter.removeItem(playerPointBean.getPlayerBean().getId());
            playerInPointAdapter.addItem(playerPointBean, Role.getRole(playerPointBean.getPlayerBean().getRole()));

            //La liste passe de vide à 1 élément on ajoute juste un fake joueur pour que pour que
            // generiqueActivity puisse savoir que la liste n'est pas vide et accepté le début du point
            //EN quittant l'écran il y aura une sauvegarde
            if (playerInPointAdapter.getDaoList().size() == 1) {
                pointBean.getPlayerPointList().clear();
                pointBean.getPlayerPointList().add(new PlayerPoint((long) 0));
            }
        }
        else {
            //Le joueur joue on le replace dans la liste des joueurs qui ne joue pas
            playerInPointAdapter.removeItem(playerPointBean.getPlayerBean().getId());
            noPlayingAdapter.addItem(playerPointBean);

            //On vide la liste des playerPoint du point pour generiqueActivity, en quittant l'ecran la sauvegarde se
            // fera
            if (playerInPointAdapter.getDaoList().isEmpty()) {
                pointBean.getPlayerPointList().clear();
            }
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
    // OTTO
    // -------------------------------- */

    private void gestionOttoEvent(OttoRefreshEvent event) {
        switch (event) {
            case SCORE_CHANGE:
            case MATCH_START:
            case POINT_START:
            case POINT_FINISHED:
            case MATCH_END:
            case ADD_POINT:
                //Pas d'interaction sur le point en cours
                break;

            //On suit le livePoint
            case CHANGE_POINT:
                generiqueActivity.gotoLivePoint();
                break;
        }
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

                //En fonction de si on est sur le point courant ou non on change la couleur de la barre
                if (MyApplication.getInstance().getLivePoint() != null && MyApplication.getInstance().getLivePoint().getId() == pointBean.getId()) {
                    root.setBackgroundColor(getResources().getColor(R.color.bg_point_in_progress));
                }
                else {
                    root.setBackgroundColor(Color.WHITE);
                }

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

        //L'ensemble des joueurs de l'equipe et On l'ajoute la liste qui lui correspond
        PlayerPoint temp;
        PlayerPointBean playerPointBean;
        for (PlayerBean playerBean : PlayerDaoManager.getPlayers(pointBean.getMatchBean().getTeamId())) {
            temp = null;
            playerPointBean = new PlayerPointBean(playerBean);
            //On regarde si le joueur joue quelque part
            for (PlayerPoint playerPoint : playerPointList) {
                if (playerPoint.getPlayerId() == playerBean.getId()) {
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

    private void gotoNextPoint() {
        int indexpoint = pointBean.getMatchBean().getPointBeanList().indexOf(pointBean);

        generiqueActivity.gotoPoint(pointBean.getMatchBean().getPointBeanList().get(indexpoint - 1), (pointBean.getMatchBean().getPointBeanList()
                .size() - indexpoint) + 1, true);
    }

    private void gotoPreviousPoint() {
        int indexpoint = pointBean.getMatchBean().getPointBeanList().indexOf(pointBean);

        generiqueActivity.gotoPoint(pointBean.getMatchBean().getPointBeanList().get(indexpoint + 1), (pointBean.getMatchBean().getPointBeanList()
                .size() - indexpoint) - 1, false);
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

    public void setNumOfPoint(Integer numOfPoint) {
        this.numOfPoint = numOfPoint;
    }
}
