package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ultimatelinemanager.R;

import java.util.List;

import greendao.PlayerBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    public enum TYPE {
        PLAYER, NOT_PLAYING, HANDLER, MIDDLE, BOTH
    }

    private List<PlayerBean> daoList;
    private String dateFormat;
    private SelectAdapterI selectAdapterI;
    private TYPE type;
    private Context context;
    private PlayerBean playerBean;

    //config
    private int girlColor, boyColor;

    public PlayerAdapter(Context context, List<PlayerBean> daoList, TYPE type, SelectAdapterI selectAdapterI) {
        this.context = context;
        this.daoList = daoList;
        this.selectAdapterI = selectAdapterI;
        this.type = type;

        girlColor = context.getResources().getColor(R.color.girl_color);
        boyColor = context.getResources().getColor(R.color.boy_color);

    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (type) {

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player, parent, false);
                break;
        }

        return new ViewHolder(view, type, selectAdapterI);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        playerBean = daoList.get(position);
        holder.playerBean = playerBean;

        switch (type) {
            case PLAYER:
                holder.row_tv1.setText(playerBean.getName());
                holder.row_tv2.setText(playerBean.getRole());

                //Couleur de l'image
                if (playerBean.getSexe()) {
                    holder.iv_main.setColorFilter(boyColor);
                }
                else {
                    holder.iv_main.setColorFilter(girlColor);
                }

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

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Composant graphique
        public final TextView row_tv1;
        public final TextView row_tv2;
        public TextView row_tv3;
        public ImageView iv_main;
        public View root; //Pour le onclick

        //Data
        public PlayerBean playerBean;
        private TYPE type;
        private SelectAdapterI callBack;

        public ViewHolder(View itemView, TYPE type, SelectAdapterI callBack) {
            super(itemView);
            row_tv1 = (TextView) itemView.findViewById(R.id.row_tv1);
            row_tv2 = (TextView) itemView.findViewById(R.id.row_tv2);
            root = itemView.findViewById(R.id.root);

            root.setOnClickListener(this);

            this.type = type;
            this.callBack = callBack;
        }

        @Override
        public void onClick(View v) {
            if (callBack != null) {
                //le postDelay permet de laisser finir l'effet du material design
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callBack.selectAdapter_onClick(playerBean, type);
                    }
                }, 200);
            }
        }
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */

    public interface SelectAdapterI {
        public void selectAdapter_onClick(PlayerBean playerBean, TYPE type);
    }

}
