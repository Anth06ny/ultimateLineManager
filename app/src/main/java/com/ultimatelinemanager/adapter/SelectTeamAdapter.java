package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.formation.utils.DateUtils;
import com.ultimatelinemanager.R;

import java.util.List;

import greendao.TeamBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class SelectTeamAdapter extends RecyclerView.Adapter<SelectTeamAdapter.ViewHolder> {

    private List<TeamBean> teamDaoList;
    private String dateFormat;

    public SelectTeamAdapter(Context context, List<TeamBean> teamDaoList) {
        this.teamDaoList = teamDaoList;

        dateFormat = DateUtils.getFormat(context, DateUtils.DATE_FORMAT.ddMMyyyy);
    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_team, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TeamBean teamBean = teamDaoList.get(0);
        holder.st_row_tv1.setText(teamBean.getName());
        holder.st_row_tv2.setText(DateUtils.dateToString(teamBean.getCreation(), dateFormat));

    }

    @Override
    public int getItemCount() {
        return teamDaoList.size();
    }

    /* ---------------------------------
    // View Holder
    // -------------------------------- */

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView st_row_tv1;
        public final TextView st_row_tv2;

        public ViewHolder(View itemView) {
            super(itemView);
            st_row_tv1 = (TextView) itemView.findViewById(R.id.st_row_tv1);
            st_row_tv2 = (TextView) itemView.findViewById(R.id.st_row_tv2);
        }

    }

}
