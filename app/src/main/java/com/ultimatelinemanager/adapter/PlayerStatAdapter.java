package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ultimatelinemanager.R;
import com.ultimatelinemanager.bean.PlayerStatBean;

import java.util.List;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class PlayerStatAdapter extends RecyclerView.Adapter<PlayerStatAdapter.ViewHolder> {

    private List<PlayerStatBean> daoList;
    private Context context;


    public PlayerStatAdapter(Context context, List<PlayerStatBean> daoList) {
        this.daoList = daoList;
        this.context = context;
    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player_stat, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PlayerStatBean playerStatBean = daoList.get(position);

        //titre
        holder.rps_name.setText(playerStatBean.getPlayerBean().getName());

        //PlayingTime
        holder.stat_tv_playing_time_value.setText(context.getResources().getString(R.string.stat_playing_time_value
                , playerStatBean.getPlayingTime(), playerStatBean.getNumberPoint()));

        //Goal Attaque
        holder.stat_goal_attaque_value.setText(context.getResources().getString(R.string.stat_goal_attaque_value
                , playerStatBean.getGoalAttaqueSuccess(), playerStatBean.getGoalAttaque()));

        //Goal Defense
        holder.stat_goal_defense_value.setText(context.getResources().getString(R.string.stat_goal_defense_value
                , playerStatBean.getGoalDefenseSuccess(), playerStatBean.getGoalDefense()));

    }

    @Override
    public int getItemCount() {
        return daoList.size();
    }

    /* ---------------------------------
    // View Holder
    // -------------------------------- */

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //Composant graphique
        public final TextView rps_name;
        public final TextView stat_tv_playing_time_value;
        public final TextView stat_goal_attaque_value;
        public final TextView stat_goal_defense_value;


        public ViewHolder(View itemView) {
            super(itemView);
            rps_name = (TextView) itemView.findViewById(R.id.rps_name);
            stat_tv_playing_time_value = (TextView) itemView.findViewById(R.id.stat_tv_playing_time_value);
            stat_goal_attaque_value = (TextView) itemView.findViewById(R.id.stat_goal_attaque_value);
            stat_goal_defense_value = (TextView) itemView.findViewById(R.id.stat_goal_defense_value);
        }


    }


}