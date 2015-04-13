package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formation.utils.DateUtils;
import com.formation.utils.Utils;
import com.ultimatelinemanager.R;

import java.util.List;

import greendao.MatchBean;
import greendao.PlayerBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {

    public enum TYPE {
        TEAM, PLAYER, MATCH
    }

    private List<?> daoList;
    private String dateFormat;
    private SelectAdapterI selectAdapterI;
    private TYPE type;
    private Context context;

    //config
    private int girlColor, boyColor;
    private String notStartHexaColor, inProgressHexaColor, finishedHexaColor;

    public SelectAdapter(Context context, List<?> daoList, TYPE type, SelectAdapterI selectAdapterI) {
        this.context = context;
        this.daoList = daoList;
        this.selectAdapterI = selectAdapterI;
        this.type = type;

        switch (type) {
            case TEAM:
                dateFormat = DateUtils.getFormat(context, DateUtils.DATE_FORMAT.ddMMyyyy);
                break;
            case PLAYER:
                girlColor = context.getResources().getColor(R.color.girl_color);
                boyColor = context.getResources().getColor(R.color.boy_color);
                break;
            case MATCH:
                dateFormat = DateUtils.getFormat(context, DateUtils.DATE_FORMAT.ddMMyyyy_HHmm);
                inProgressHexaColor = String.format("#%06X", (0xFFFFFF & Utils.getColorFromTheme(context, R.attr.color_text_main)));
                ;

                finishedHexaColor = String.format("#%06X", (0xFFFFFF & context.getResources().getColor(R.color.vivid_green)));
                ;
                notStartHexaColor = String.format("#%06X", (0xFFFFFF & context.getResources().getColor(R.color.red)));
                ;
                break;
        }

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
            case MATCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_match, parent, false);
                break;
        }

        return new ViewHolder(view, type, selectAdapterI);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (type) {

            case TEAM:
                TeamBean teamBean = (TeamBean) daoList.get(position);
                holder.bean = teamBean;
                holder.row_tv1.setText(teamBean.getName());
                holder.row_tv2.setText(DateUtils.dateToString(teamBean.getCreation(), dateFormat));
                break;

            case PLAYER:
                PlayerBean playerBean = (PlayerBean) daoList.get(position);
                holder.bean = playerBean;
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

            case MATCH:
                MatchBean matchBean = (MatchBean) daoList.get(position);
                holder.bean = matchBean;
                holder.row_tv1.setText(matchBean.getName());

                String statut;
                String hexColor;
                //Match commencé
                if (matchBean.getStart() != null) {
                    holder.row_tv2.setText(DateUtils.dateToString(matchBean.getStart(), dateFormat));
                    holder.row_tv2.setVisibility(View.VISIBLE);

                    //match non terminée
                    if (matchBean.getEnd() == null) {
                        hexColor = inProgressHexaColor;
                        statut = context.getString(R.string.ma_list_statut_in_progress);
                    }
                    else {
                        hexColor = finishedHexaColor;
                        statut = context.getString(R.string.ma_list_statut_finish);
                    }

                }
                //Match non commencé
                else {
                    holder.row_tv2.setVisibility(View.GONE);
                    hexColor = notStartHexaColor;
                    statut = context.getString(R.string.ma_list_statut_not_start);
                }

                holder.row_tv3.setText(Html.fromHtml(context.getString(R.string.ma_list_statut, hexColor, statut)));

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
        public TextView row_tv3;
        public ImageView iv_main;
        public View root; //Pour le onclick
        //Data
        public T bean;
        private SelectAdapterI callBack;

        public ViewHolder(View itemView, TYPE type1, SelectAdapterI callBack) {
            super(itemView);
            row_tv1 = (TextView) itemView.findViewById(R.id.row_tv1);
            row_tv2 = (TextView) itemView.findViewById(R.id.row_tv2);
            root = itemView.findViewById(R.id.root);

            switch (type1) {
                case TEAM:
                    break;
                case PLAYER:
                    iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
                    break;
                case MATCH:
                    row_tv3 = (TextView) itemView.findViewById(R.id.row_tv3);
                    break;
            }

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
