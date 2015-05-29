package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formation.utils.Utils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.bean.PlayerPointBean;
import com.ultimatelinemanager.bean.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class PlayerPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum FilterSexe {
        BOY, GIRL, BOTH
    }

    public enum SortOrder {
        AZ, PLAYING_TIME, SLEEP_TIME, NUMBER
    }

    protected List<PlayerPointBean> daoList;
    protected List<PlayerPointBean> filterList;
    protected PlayerPointAdapterI selectAdapterI;
    protected Context context;

    //config
    private int girlColor, boyColor, iceColor;
    private FilterSexe filterSexe;
    private Role filterRole;
    private SortOrder sortOrder;

    /**
     * @param context
     * @param selectAdapterI
     */
    public PlayerPointAdapter(Context context, PlayerPointAdapterI selectAdapterI) {
        this.context = context;
        this.daoList = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.selectAdapterI = selectAdapterI;

        girlColor = context.getResources().getColor(R.color.girl_color);
        boyColor = context.getResources().getColor(R.color.boy_color);
        iceColor = context.getResources().getColor(R.color.ice_color);

        //Par defaut pas de filtre
        filterSexe = FilterSexe.BOTH;
        filterRole = Role.Both;
        sortOrder = SortOrder.AZ;
    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player_point, parent, false);
        return new ViewHolderItem(view, selectAdapterI);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        onBindViewHolderItem((ViewHolderItem) h, position);
    }

    public void onBindViewHolderItem(ViewHolderItem holder, int position) {
        PlayerPointBean playerPointBean = getItem(position);
        holder.playerPointBean = playerPointBean;

        //Role
        switch (Role.getRole(playerPointBean.getPlayerBean().getRole())) {
            case Handler:
                holder.rpr_iv_role.setImageResource(R.drawable.handler);
                break;
            case Middle:
                holder.rpr_iv_role.setImageResource(R.drawable.middle);
                break;
            case Both:
                holder.rpr_iv_role.setImageResource(R.drawable.both);
                break;
        }
        //Couleur de l'image
        if (playerPointBean.getPlayerBean().getSexe()) {
            holder.rpr_iv_role.setColorFilter(boyColor);
        }
        else {
            holder.rpr_iv_role.setColorFilter(girlColor);
        }

        //Nom
        String number = playerPointBean.getPlayerBean().getNumber() > 0 ? (playerPointBean.getPlayerBean().getNumber() + " - ") : "";
        holder.rpr_tv_name.setText(number + playerPointBean.getPlayerBean().getName());
        //playingTime
        holder.rpr_tv_run.setText(playerPointBean.getNbrPoint() + "pt(" + Utils.timeToMMSS(playerPointBean.getPlayingTime()) + ")");
        //sleep
        holder.rpr_tv_sleep.setText(playerPointBean.getRestTime() / Constante.PLAYING_TIME_DIVISE + "min");
        //indicateur fatigue
        holder.rpr_iv_indicator.setImageResource(R.drawable.cold);
        holder.rpr_iv_indicator.setColorFilter(iceColor);
        holder.rpr_iv_indicator.setVisibility(View.VISIBLE);
        holder.rpr_tv_indicator.setText("");
    }

    public PlayerPointBean getItem(int position) {
        return filterList.get(position);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    /* ---------------------------------
    // Public
    // -------------------------------- */

    /**
     * trie par ordre alphabetique
     *
     * @param playerPointBean
     */
    public void addItem(PlayerPointBean playerPointBean) {
        int size = daoList.size();
        int value = 0;
        //En fonction de la position du joueur dans le tri
        for (int i = 0; i < size; i++) {
            if (sortOrder == SortOrder.AZ && playerPointBean.getPlayerBean().getName().compareTo(daoList.get(i).getPlayerBean().getName()) < 0) {
                value = i;
                break;
            }
            else if (sortOrder == SortOrder.PLAYING_TIME && playerPointBean.getPlayingTime().compareTo(daoList.get(i).getPlayingTime()) < 0) {
                value = i;
                break;
            }
            else if (sortOrder == SortOrder.SLEEP_TIME && playerPointBean.getPlayingTime().compareTo(daoList.get(i).getPlayingTime()) > 0) {
                value = i;
                break;
            }
        }
        daoList.add(value, playerPointBean);

        //Meme chose avec la liste qui est affiche
        if (isFilterOk(playerPointBean)) {
            size = filterList.size();
            value = 0;
            for (int i = 0; i < size; i++) {
                if (sortOrder == SortOrder.AZ && playerPointBean.getPlayerBean().getName().compareTo(filterList.get(i).getPlayerBean().getName()) < 0) {
                    value = i;
                    break;
                }
                else if (sortOrder == SortOrder.PLAYING_TIME && playerPointBean.getPlayingTime().compareTo(filterList.get(i).getPlayingTime()) < 0) {
                    value = i;
                    break;
                }
                else if (sortOrder == SortOrder.SLEEP_TIME && playerPointBean.getPlayingTime().compareTo(filterList.get(i).getPlayingTime()) > 0) {
                    value = i;
                    break;
                }
            }
            filterList.add(value, playerPointBean);
            //pour l'animation
            notifyItemInserted(value);
        }
    }

    public void removeItem(long playerId) {

        int size = daoList.size();
        for (int i = 0; i < size; i++) {
            if (daoList.get(i).getPlayerBean().getId().equals(playerId)) {
                daoList.remove(i);
                break;
            }
        }

        //meme chose avec la liste qui est affiche
        size = filterList.size();
        for (int i = 0; i < size; i++) {
            if (filterList.get(i).getPlayerBean().getId().equals(playerId)) {
                filterList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    /**
     * Remplit la liste de filtre
     */
    public void refreshFilterList() {

        //D'abord on trie la liste de base en fonction
        Collections.sort(daoList, new Comparator<PlayerPointBean>() {
            @Override
            public int compare(PlayerPointBean lhs, PlayerPointBean rhs) {

                switch (sortOrder) {

                    case AZ:
                        return lhs.getPlayerBean().getName().compareTo(rhs.getPlayerBean().getName());

                    case PLAYING_TIME:
                        return lhs.getPlayingTime().compareTo(rhs.getPlayingTime());
                    case SLEEP_TIME:
                        //sens inverse
                        return rhs.getRestTime().compareTo(lhs.getRestTime());

                    case NUMBER:
                        return lhs.getPlayerBean().getNumber() - rhs.getPlayerBean().getNumber();
                }

                return 0;
            }
        });

        filterList.clear();
        //Ensuite on la parcourt et on ajoute uniquement les joueurs qui correspondent au filtre
        for (PlayerPointBean playerPointBean : daoList) {
            if (isFilterOk(playerPointBean)) {
                filterList.add(playerPointBean);
            }
        }

        notifyDataSetChanged();
    }

    /* ---------------------------------
    // private
    // -------------------------------- */

    private boolean isFilterOk(PlayerPointBean playerPointBean) {
        switch (filterSexe) {
            case BOY:
                //Si ce n'est pas un garcon
                if (!playerPointBean.getPlayerBean().getSexe()) {
                    return false;
                }
                break;
            case GIRL:
                //Si ce n'est pas une fille
                if (playerPointBean.getPlayerBean().getSexe()) {
                    return false;
                }
                break;
        }
        switch (filterRole) {
            case Handler:
                if (Role.getRole(playerPointBean.getPlayerBean().getRole()) == Role.Middle) {
                    return false;
                }
                break;
            case Middle:
                if (Role.getRole(playerPointBean.getPlayerBean().getRole()) == Role.Handler) {
                    return false;
                }
                break;
        }

        return true;
    }

    /* ---------------------------------
    // View Holder
    // -------------------------------- */

    protected static class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Composant graphique
        public View root; //Pour le onclick
        private ImageView rpr_iv_role;
        private ImageView rpr_iv_indicator;
        private TextView rpr_tv_indicator;
        private TextView rpr_tv_name;
        private ImageView rpr_iv_run;
        private TextView rpr_tv_run;
        private ImageView rpr_iv_sleep;
        private TextView rpr_tv_sleep;

        //Data
        public PlayerPointBean playerPointBean;
        private PlayerPointAdapterI callBack;

        public ViewHolderItem(View itemView, PlayerPointAdapterI callBack) {
            super(itemView);
            rpr_iv_role = (ImageView) itemView.findViewById(R.id.rpr_iv_role);
            rpr_iv_indicator = (ImageView) itemView.findViewById(R.id.rpr_iv_indicator);
            rpr_tv_indicator = (TextView) itemView.findViewById(R.id.rpr_tv_indicator);
            rpr_tv_name = (TextView) itemView.findViewById(R.id.rpr_tv_name);
            rpr_iv_run = (ImageView) itemView.findViewById(R.id.rpr_iv_run);
            rpr_tv_run = (TextView) itemView.findViewById(R.id.rpr_tv_run);
            rpr_iv_sleep = (ImageView) itemView.findViewById(R.id.rpr_iv_sleep);
            rpr_tv_sleep = (TextView) itemView.findViewById(R.id.rpr_tv_sleep);
            root = itemView.findViewById(R.id.root);

            rpr_iv_run.setColorFilter(Color.BLACK);
            rpr_iv_sleep.setColorFilter(Color.BLACK);

            root.setOnClickListener(this);

            this.callBack = callBack;
        }

        @Override
        public void onClick(View v) {
            if (callBack != null) {
                //le postDelay permet de laisser finir l'effet du material design
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callBack.playerPointAdapter_onClick(playerPointBean);
                    }
                }, 200);
            }
        }
    }

    /* ---------------------------------
    // getter / setter
    // -------------------------------- */

    public List<PlayerPointBean> getDaoList() {
        return daoList;
    }

    public void setFilterRole(Role filterRole) {
        this.filterRole = filterRole;
    }

    public void setFilterSexe(FilterSexe filterSexe) {
        this.filterSexe = filterSexe;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */

    public interface PlayerPointAdapterI {
        public void playerPointAdapter_onClick(PlayerPointBean playerPointBean);
    }

}
