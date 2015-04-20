package com.ultimatelinemanager.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ultimatelinemanager.R;
import com.ultimatelinemanager.bean.PlayerPointBean;
import com.ultimatelinemanager.bean.Role;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class PlayerPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<PlayerPointBean> daoList;
    protected PlayerPointAdapterI selectAdapterI;
    protected Context context;

    //config
    private int girlColor, boyColor, iceColor;

    /**
     * @param context
     * @param daoList Liste trier en fonction du role
     * @param selectAdapterI
     */
    public PlayerPointAdapter(Context context, PlayerPointAdapterI selectAdapterI) {
        this.context = context;
        this.daoList = new ArrayList<>();
        this.selectAdapterI = selectAdapterI;

        girlColor = context.getResources().getColor(R.color.girl_color);
        boyColor = context.getResources().getColor(R.color.boy_color);
        iceColor = context.getResources().getColor(R.color.ice_color);

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
        PlayerPointBean playerPointBean = daoList.get(position);
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
        holder.rpr_tv_name.setText(playerPointBean.getPlayerBean().getName());
        //playingTime
        holder.rpr_tv_run.setText("12m35");
        //sleep
        holder.rpr_tv_sleep.setText("2m35");
        //indicateur fatigue
        holder.rpr_iv_indicator.setImageResource(R.drawable.cold);
        holder.rpr_iv_indicator.setColorFilter(iceColor);
        holder.rpr_iv_indicator.setVisibility(View.VISIBLE);
        holder.rpr_tv_indicator.setText("110");
    }

    @Override
    public int getItemCount() {
        return daoList.size();
    }

    /* ---------------------------------
    // Public
    // -------------------------------- */

    /**
     * trie par ordre alphabetique
     * @param playerPointBean
     */
    public void addItem(PlayerPointBean playerPointBean) {
        int size = daoList.size();
        int value = 0;
        for (int i = 0; i < size; i++) {
            if (playerPointBean.getPlayerBean().getName().compareTo(daoList.get(i).getPlayerBean().getName()) < 0) {
                value = i;
                break;
            }
        }
        daoList.add(value, playerPointBean);
        notifyItemInserted(value);
    }

    public void removeItem(long playerId) {

        int size = daoList.size();
        for (int i = 0; i < size; i++) {
            if (daoList.get(i).getPlayerBean().getId().equals(playerId)) {
                daoList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
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

    public void setDaoList(List<PlayerPointBean> daoList) {
        this.daoList = daoList;
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */

    public interface PlayerPointAdapterI {
        public void playerPointAdapter_onClick(PlayerPointBean playerPointBean);
    }

}
