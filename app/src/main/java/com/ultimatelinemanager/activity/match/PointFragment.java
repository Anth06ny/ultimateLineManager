package com.ultimatelinemanager.activity.match;

import android.graphics.Color;
import android.os.AsyncTask;
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
import com.ultimatelinemanager.metier.GestionSharedPreference;
import com.ultimatelinemanager.metier.composant.OnSwipeTouchListener;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import greendao.MatchBean;
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
    private ImageView pa_iv_number;

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
    private MatchBean matchBean;
    private PointBean pointBean;
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
        else if (matchBean == null) {
            LogUtils.logMessage(TAG, "matchBean à nulle");
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
        pa_iv_number = (ImageView) view.findViewById(R.id.pa_iv_number);
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
        pa_iv_number.setOnClickListener(this);

        filtreSelectedColor = Utils.getColorFromTheme(getActivity(), R.attr.color_composant_main_highlighted);

        pa_iv_handler.setColorFilter(Color.BLACK);
        pa_iv_middle.setColorFilter(Color.BLACK);
        pa_iv_girl.setColorFilter(Color.BLACK);
        pa_iv_boy.setColorFilter(Color.BLACK);
        pa_iv_alpha.setColorFilter(Color.BLACK);
        pa_iv_time.setColorFilter(Color.BLACK);
        pa_iv_sleep.setColorFilter(Color.BLACK);
        pa_iv_number.setColorFilter(Color.BLACK);

        //On utilise le titre du match et non celui en selection dans le cas ou est en visite sur un autre match
        getActivity().setTitle(getString(R.string.pa_title, matchBean.getTeamBean().getName(), matchBean.getName(), pointBean.getNumber()));

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
                if (matchBean.getId() == MyApplication.getInstance().getLiveMatch().getId()) {
                    gestionOttoEvent(event);
                }
            }

        };
        MyApplication.getInstance().getBus().register(ottoListner);

    }

    @Override
    public void onResume() {
        super.onResume();

        new RefreshPLayerAT().execute();

    }

    @Override
    public void onPause() {
        super.onPause();

        //On enregistre les modifications en base
        PointDaoManager.savePlayerPointList(pointBean, playerInPointAdapter.getDaoList());

        //On sauvegarde les filtres utilisés
        GestionSharedPreference.setLastFiltreRole(noPlayingAdapter.getFilterRole());
        GestionSharedPreference.setLastFiltreSexe(noPlayingAdapter.getFilterSexe());
        GestionSharedPreference.setLastSort(noPlayingAdapter.getSortOrder());

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

        //Si c'est le dernier point du match, on affiche le point d'apres et s'il y en a plus on ajoute le plus
        if (pointBean.getNumber() == matchBean.getPointBeanList().size()) {
            menu.findItem(R.id.mp_next).setVisible(false);
            menu.findItem(R.id.mp_add).setVisible(true);
        }
        else {
            menu.findItem(R.id.mp_next).setVisible(true);
            menu.findItem(R.id.mp_add).setVisible(false);
        }

        //si c'est le premier du match
        if (pointBean.getNumber() == 1) {
            menu.findItem(R.id.mp_previous).setVisible(false);
        }
        else {
            menu.findItem(R.id.mp_previous).setVisible(true);
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

            case R.id.mp_add:
                //On ajoute un point
                PointBean newPoint = new PointBean();
                newPoint.setMatchId(pointBean.getMatchId());
                newPoint.setNumber(pointBean.getNumber() + 1);
                newPoint.setId(PointDaoManager.getPointBeanDao().insert(newPoint));

                //On l'ajoute au match
                matchBean.getPointBeanList().add(0, newPoint);

                generiqueActivity.gotoPoint(matchBean, newPoint);

                return true;

            case R.id.mp_delete:

                //On supprime le point courant
                PointDaoManager.deletePoint(matchBean, pointBean, true);
                generiqueActivity.refreshLivePoint();
                //On revient en arriere
                getFragmentManager().popBackStack();
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
            if (noPlayingAdapter.getFilterRole() != Role.Handler) {
                noPlayingAdapter.setFilterRole(Role.Handler);
            }
            else {
                noPlayingAdapter.setFilterRole(Role.Both);
            }
            noPlayingAdapter.refreshFilterList();
            refreshView();
        }
        else if (v == pa_iv_middle) {
            if (noPlayingAdapter.getFilterRole() != Role.Middle) {
                noPlayingAdapter.setFilterRole(Role.Middle);
            }
            else {
                noPlayingAdapter.setFilterRole(Role.Both);
            }
            noPlayingAdapter.refreshFilterList();
            refreshView();
        }
        else if (v == pa_iv_girl) {

            if (noPlayingAdapter.getFilterSexe() != PlayerPointAdapter.FilterSexe.GIRL) {
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.GIRL);
            }
            else {
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.BOTH);
            }

            noPlayingAdapter.refreshFilterList();
            refreshView();
        }
        else if (v == pa_iv_boy) {
            if (noPlayingAdapter.getFilterSexe() != PlayerPointAdapter.FilterSexe.BOY) {
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.BOY);
            }
            else {
                noPlayingAdapter.setFilterSexe(PlayerPointAdapter.FilterSexe.BOTH);
            }

            noPlayingAdapter.refreshFilterList();
            refreshView();

        }
        else if (v == pa_iv_alpha) {
            noPlayingAdapter.setSortOrder(PlayerPointAdapter.SortOrder.AZ);
            noPlayingAdapter.refreshFilterList();
            refreshView();
        }
        else if (v == pa_iv_number) {
            noPlayingAdapter.setSortOrder(PlayerPointAdapter.SortOrder.NUMBER);
            noPlayingAdapter.refreshFilterList();
            refreshView();

        }
        else if (v == pa_iv_time) {
            noPlayingAdapter.setSortOrder(PlayerPointAdapter.SortOrder.PLAYING_TIME);
            noPlayingAdapter.refreshFilterList();
            refreshView();

        }
        else if (v == pa_iv_sleep) {
            noPlayingAdapter.setSortOrder(PlayerPointAdapter.SortOrder.SLEEP_TIME);
            noPlayingAdapter.refreshFilterList();
            refreshView();
        }
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

                //Mise à jour du filtres Handler /Middle
                switchFiltreImageViewColor(pa_iv_middle, false);
                switchFiltreImageViewColor(pa_iv_handler, false);
                switch (noPlayingAdapter.getFilterRole()) {

                    case Handler:
                        switchFiltreImageViewColor(pa_iv_handler, true);
                        break;
                    case Middle:
                        switchFiltreImageViewColor(pa_iv_middle, true);
                        break;
                    case Both:
                        break;
                }

                //Mise a jour graphique du filtre Sexe
                switchFiltreImageViewColor(pa_iv_girl, false);
                switchFiltreImageViewColor(pa_iv_boy, false);
                switch (noPlayingAdapter.getFilterSexe()) {
                    case BOY:
                        pa_iv_boy.setColorFilter(getResources().getColor(R.color.girl_color));

                        break;
                    case GIRL:
                        pa_iv_girl.setColorFilter(getResources().getColor(R.color.girl_color));
                        break;
                    case BOTH:
                        break;
                }

                //On remet tous à 0 et on ne selectionne que le selectionner
                switchFiltreImageViewColor(pa_iv_alpha, false);
                switchFiltreImageViewColor(pa_iv_time, false);
                switchFiltreImageViewColor(pa_iv_sleep, false);
                switchFiltreImageViewColor(pa_iv_number, false);

                //Mise à jour du tri
                switch (noPlayingAdapter.getSortOrder()) {

                    case AZ:
                        switchFiltreImageViewColor(pa_iv_alpha, true);
                        break;
                    case PLAYING_TIME:
                        switchFiltreImageViewColor(pa_iv_time, true);
                        break;
                    case SLEEP_TIME:
                        switchFiltreImageViewColor(pa_iv_sleep, true);
                        break;
                    case NUMBER:
                        switchFiltreImageViewColor(pa_iv_number, true);
                        break;
                }

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
                try {
                    if (MyApplication.getInstance().getLivePoint() != null && MyApplication.getInstance().getLivePoint().getId() == pointBean.getId()) {
                        root.setBackgroundColor(getResources().getColor(R.color.bg_point_in_progress));
                    }
                    else {
                        root.setBackgroundColor(Color.WHITE);
                    }
                }
                catch (TechnicalException e) {
                    showError(e, true);
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
        for (PlayerBean playerBean : PlayerDaoManager.getPlayers(matchBean.getTeamId())) {
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

        //On positionne le filtre par defaut
        noPlayingAdapter.setFilterSexe(GestionSharedPreference.getLastFiltreSexe());
        noPlayingAdapter.setFilterRole(GestionSharedPreference.getLastFiltreRole());
        noPlayingAdapter.setSortOrder(GestionSharedPreference.getLastSort());

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

        if (pointBean.getNumber() + 1 <= matchBean.getPointBeanList().size()) {
            try {
                generiqueActivity.gotoPoint(matchBean, PointDaoManager.getPointNumber(matchBean.getPointBeanList(), pointBean.getNumber() + 1));
            }
            catch (TechnicalException e) {
                showError(e, true);
            }
        }

    }

    private void gotoPreviousPoint() {

        if (pointBean.getNumber() > 1) {
            try {
                generiqueActivity.gotoPoint(matchBean, PointDaoManager.getPointNumber(matchBean.getPointBeanList(), pointBean.getNumber() - 1));
            }
            catch (TechnicalException e) {
                showError(e, true);
            }
        }
    }

    private void refreshStatePlayer() {

        //Si le match est terminé innutile de prendre le temps courant
        long lastTime = matchBean.getEnd() != null ? Math.min(new Date().getTime(), matchBean.getEnd().getTime()) : new Date().getTime();

        List<PlayerPointBean> list = new ArrayList<>();
        list.addAll(noPlayingAdapter.getDaoList());
        list.addAll(playerInPointAdapter.getDaoList());

        //On parcourt la liste de chacun des joueurs
        for (PlayerPointBean playerPointBean : list) {

            playerPointBean.setPlayingTime((long) 0);
            if (matchBean.getStart() != null) {
                playerPointBean.setRestTime(matchBean.getStart() != null ? (lastTime - matchBean.getStart().getTime()) : 0);
            }

            //Les points qu'a fait le joueur dans l'ordre
            List<PointBean> pointBeanList = PointDaoManager.getPointPlayerOfMatch(matchBean, playerPointBean.getPlayerBean().getId());
            for (PointBean bean : pointBeanList) {
                playerPointBean.setPlayingTime(playerPointBean.getPlayingTime() + bean.getLength());

                //Temps depuis que le joueur n'a pas joué
                if (bean.getStart() != null && bean.getStop() == null) {
                    //Si le joueur joue un point en cours on met le restTime à 0
                    playerPointBean.setRestTime((long) 0);
                }
                else if (bean.getStop() != null) {
                    //Point terminée
                    playerPointBean.setRestTime(lastTime - bean.getStop().getTime());
                }
            }
            playerPointBean.setNbrPoint(pointBeanList.size());
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

    public void setMatchBean(MatchBean matchBean) {
        this.matchBean = matchBean;
    }

    private class RefreshPLayerAT extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            refreshStatePlayer();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //on met à jour l'interface graphique
            noPlayingAdapter.notifyDataSetChanged();
            playerInPointAdapter.notifyDataSetChanged();
        }
    }
}
