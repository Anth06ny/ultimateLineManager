package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formation.utils.DateUtils;
import com.ultimatelinemanager.R;

import java.util.List;

import greendao.PlayerBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {

    public enum TYPE {
        TEAM, PLAYER
    }

    private List<?> daoList;
    private String dateFormat;
    private SelectAdapterI selectAdapterI;
    private TYPE type;

    public SelectAdapter(Context context, List<?> daoList, TYPE type, SelectAdapterI selectAdapterI) {
        this.daoList = daoList;
        this.selectAdapterI = selectAdapterI;
        this.type = type;

        dateFormat = DateUtils.getFormat(context, DateUtils.DATE_FORMAT.ddMMyyyy);
    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        switch (type) {

            case TEAM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_team, parent, false);
                break;
            case PLAYER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player, parent, false);
                break;
        }

        return new ViewHolder(view, selectAdapterI);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (type) {

            case TEAM:
                TeamBean teamBean = (TeamBean) daoList.get(position);
                holder.row_tv1.setText(teamBean.getName());
                holder.row_tv2.setText(DateUtils.dateToString(teamBean.getCreation(), dateFormat));
                holder.bean = teamBean;
                break;
            case PLAYER:
                PlayerBean playerBean = (PlayerBean) daoList.get(position);
                holder.row_tv1.setText(playerBean.getName());
                holder.row_tv2.setText(playerBean.getRole());
                holder.bean = playerBean;
                break;
        }

    }

    @Override
    public int getItemCount() {
        return daoList.size();
    }

    /* ---------------------------------
    // View Holder
    // -------------------------------- */

    protected static class ViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Composant graphique
        public final TextView row_tv1;
        public final TextView row_tv2;
        public final ImageView iv_main;
        public View root; //Pour le onclick
        //Data
        public T bean;
        private SelectAdapterI callBack;

        public ViewHolder(View itemView, SelectAdapterI callBack) {
            super(itemView);
            row_tv1 = (TextView) itemView.findViewById(R.id.row_tv1);
            row_tv2 = (TextView) itemView.findViewById(R.id.row_tv2);
            iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            root = itemView.findViewById(R.id.root);

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
                        callBack.selectAdapter_onClick(bean);
                    }
                }, 200);
            }
        }
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */

    public interface SelectAdapterI<T> {
        public void selectAdapter_onClick(T bean);
    }

}
