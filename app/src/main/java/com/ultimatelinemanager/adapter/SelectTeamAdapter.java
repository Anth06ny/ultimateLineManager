package com.ultimatelinemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ultimatelinemanager.R;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class SelectTeamAdapter extends RecyclerView.Adapter<SelectTeamAdapter.ViewHolder> {

    public SelectTeamAdapter() {

    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_select_team, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
