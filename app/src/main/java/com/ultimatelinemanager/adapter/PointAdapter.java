package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.formation.utils.Utils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import greendao.PlayerPoint;
import greendao.PointBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class PointAdapter extends RecyclerView.Adapter<PointAdapter.ViewHolder> {

    private List<PointBean> daoList;
    private List<Long> showPlayerListId; //les point dont on affiche l'id
    private Context context;
    private PointAdapterCB pointAdapterCB;

    //config
    private int girlColor, boyColor, winColor;
    private int notStartHexaColor, inProgressHexaColor, finishedHexaColor;
    private int paddingLeftPlayer; //padding a appliquer au textview des joueurs pour qu'ils soient décalés.

    public PointAdapter(Context context, List<PointBean> daoList, PointAdapterCB pointAdapterCB) {
        this.context = context;
        this.daoList = daoList;
        this.pointAdapterCB = pointAdapterCB;
        showPlayerListId = new ArrayList<>();

        girlColor = context.getResources().getColor(R.color.girl_color);
        boyColor = context.getResources().getColor(R.color.boy_color);
        winColor = context.getResources().getColor(R.color.win_color);
        inProgressHexaColor = context.getResources().getColor(R.color.in_progress_color);
        finishedHexaColor = context.getResources().getColor(R.color.finish_color);
        notStartHexaColor = context.getResources().getColor(R.color.not_start_color);
        paddingLeftPlayer = context.getResources().getDimensionPixelSize(R.dimen.margin_20);

        notifyDataSetChanged();

    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_point, parent, false);

        return new ViewHolder(view, boyColor, winColor, showPlayerListId, this, daoList, pointAdapterCB);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PointBean pointBean = daoList.get(position);
        //titre
        holder.rp_title.setText(context.getString(R.string.lp_bloc_tittle, daoList.size() - position));
        //statut point non commencé
        if (pointBean.getStart() == null) {
            //offense / defense
            holder.rp_iv_offense.setVisibility(View.INVISIBLE);

            //Statut
            holder.ma_tv_statut.setTextColor(notStartHexaColor);
            holder.ma_tv_statut.setText(context.getString(R.string.tm_list_statut_not_start));

            //playingTime
            holder.ma_tv_playing_time.setText(Constante.EMPTY);
        }
        //point en cours
        else {
            //offense / defense
            holder.rp_iv_offense.setVisibility(View.VISIBLE);
            holder.rp_iv_offense.setImageResource(pointBean.getTeamOffense() != null && pointBean.getTeamOffense() ? R.drawable.offense
                    : R.drawable.defense);

            //Statut
            if (pointBean.getTeamGoal() == null) {
                //Point en cours
                holder.ma_tv_statut.setTextColor(inProgressHexaColor);
                holder.ma_tv_statut.setText(context.getString(R.string.tm_list_statut_in_progress));
            }
            else {
                //point terminé
                holder.ma_tv_statut.setTextColor(finishedHexaColor);
                holder.ma_tv_statut.setText(context.getString(R.string.tm_list_statut_finish));
            }

            //playing time
            //s'il y a deja du temps on met le temps sinon on met le temps écoulé deplus le début du point
            long playingTime = pointBean.getLength() != 0 ? pointBean.getLength() : (new Date().getTime() - pointBean.getStart().getTime());
            holder.ma_tv_playing_time.setText(Utils.timeToHHMM(playingTime));
        }

        //win
        holder.rp_iv_result.setVisibility(pointBean.getTeamGoal() != null && pointBean.getTeamGoal() ? View.VISIBLE : View.INVISIBLE);

        //On affiche les Joueurs
        if (showPlayerListId.contains(pointBean.getId())) {
            holder.rp_ll_players.setVisibility(View.VISIBLE);
            //on retire tous pour tous remettre sauf la 1er
            View view = holder.rp_ll_players.findViewById(R.id.tp_tv_players_title);
            holder.rp_ll_players.removeAllViews();
            holder.rp_ll_players.addView(view);
            for (PlayerPoint playerPoint : pointBean.getPlayerPointList()) {
                TextView textView = new TextView(context);
                textView.setText("-" + playerPoint.getPlayerBean().getName());
                textView.setTextColor(playerPoint.getPlayerBean().getSexe() ? boyColor : girlColor);
                textView.setPadding(paddingLeftPlayer, 0, 0, 0);
                holder.rp_ll_players.addView(textView);
            }

            //on affiche masquer les joueur
            holder.tv_show_players.setText(context.getString(R.string.lp_hide_players));
            //on affiche le bouton de suppresion
            holder.tv_delete_point.setVisibility(View.VISIBLE);
        }
        //on les masque
        else {
            holder.rp_ll_players.setVisibility(View.GONE);
            //on affiche afficher les joueur
            holder.tv_show_players.setText(context.getString(R.string.lp_show_players));
            holder.tv_delete_point.setVisibility(View.INVISIBLE);
        }

        holder.pointBean = pointBean;

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
        public ImageView rp_iv_result;
        public ImageView rp_iv_offense;
        public TextView rp_title;
        public TableLayout rp_tl;
        public TextView ma_tv_statut;
        public TextView ma_tv_playing_time;
        public LinearLayout rp_ll_players;
        public TextView tv_show_players;
        public TextView tv_delete_point;
        public View root;

        //Data
        public PointBean pointBean;
        public List<PointBean> pointBeanList;
        private PointAdapterCB pointAdapterCB;
        private List<Long> showPlayerListId;
        private PointAdapter adapter;

        public ViewHolder(View itemView, int offenseColor, int winColor, List<Long> showPlayerListId, PointAdapter adapter,
                List<PointBean> pointBeanList, PointAdapterCB pointAdapterCB) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            rp_iv_result = (ImageView) itemView.findViewById(R.id.rp_iv_result);
            rp_iv_offense = (ImageView) itemView.findViewById(R.id.rp_iv_offense);
            rp_title = (TextView) itemView.findViewById(R.id.rp_title);
            rp_tl = (TableLayout) itemView.findViewById(R.id.rp_tl);
            ma_tv_statut = (TextView) itemView.findViewById(R.id.ma_tv_statut);
            ma_tv_playing_time = (TextView) itemView.findViewById(R.id.ma_tv_playing_time);
            rp_ll_players = (LinearLayout) itemView.findViewById(R.id.rp_ll_players);
            tv_show_players = (TextView) itemView.findViewById(R.id.tv_show_players);
            tv_delete_point = (TextView) itemView.findViewById(R.id.tv_delete_point);

            root.setOnClickListener(this);
            tv_show_players.setOnClickListener(this);
            tv_delete_point.setOnClickListener(this);

            rp_iv_offense.setColorFilter(offenseColor, PorterDuff.Mode.SRC_IN);
            rp_iv_result.setColorFilter(winColor, PorterDuff.Mode.SRC_IN);

            this.pointAdapterCB = pointAdapterCB;
            this.showPlayerListId = showPlayerListId;
            this.adapter = adapter;
            this.pointBeanList = pointBeanList;
        }

        @Override
        public void onClick(View v) {

            if (v == tv_delete_point) {

                if (pointAdapterCB != null) {
                    pointAdapterCB.pointAdapter_deletePoint(pointBean);
                }
            }
            else if (v == tv_show_players) {

                if (showPlayerListId.contains(pointBean.getId())) {
                    showPlayerListId.remove(pointBean.getId());
                }
                else {
                    showPlayerListId.add(pointBean.getId());
                }

                //On previent que l'element à changé
                int position = 0;
                for (PointBean pointBean1 : pointBeanList) {
                    if (pointBean1.getId().equals(pointBean.getId())) {
                        adapter.notifyItemChanged(position);
                        return;
                    }
                    else {
                        position++;
                    }
                }
            }
            else if (v == root) {
                if (pointAdapterCB != null) {
                    pointAdapterCB.pointAdapter_click(pointBean);
                }
            }
        }
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */

    public interface PointAdapterCB {
        void pointAdapter_click(PointBean bean);

        void pointAdapter_deletePoint(PointBean bean);
    }
}