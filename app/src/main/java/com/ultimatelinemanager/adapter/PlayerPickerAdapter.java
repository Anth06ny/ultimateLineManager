package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ultimatelinemanager.R;
import com.ultimatelinemanager.bean.Pair;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import greendao.PlayerBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class PlayerPickerAdapter extends RecyclerView.Adapter<PlayerPickerAdapter.ViewHolder> implements Filterable {

    private List<Pair<Boolean, PlayerBean>> daoList;
    private List<Pair<Boolean, PlayerBean>> daoListFiltered;
    private PlayerPickerCB playerPickerCB;
    private Context context;
    private boolean filterActive;
    private Filter filter;

    //config
    private static int girlColor, boyColor, selectedColor;

    public PlayerPickerAdapter(Context context, List<Pair<Boolean, PlayerBean>> listPlayer, PlayerPickerCB playerPickerCB) {
        this.context = context;
        this.playerPickerCB = playerPickerCB;
        daoListFiltered = new ArrayList<>();
        daoList = listPlayer;
        filterActive = false;

        selectedColor = context.getResources().getColor(R.color.aqua);
        girlColor = context.getResources().getColor(R.color.girl_color);
        boyColor = context.getResources().getColor(R.color.boy_color);
    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player, parent, false);

        return new ViewHolder(view, playerPickerCB);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bean = (Pair<Boolean, PlayerBean>) getItem(position);
        PlayerBean playerBean = holder.bean.second;
        String number = playerBean.getNumber() > 0 ? (playerBean.getNumber() + " - ") : "";
        holder.row_tv1.setText(number + playerBean.getName());
        holder.row_tv2.setText(playerBean.getRole());
        holder.row_iv_delete.setVisibility(View.VISIBLE);

        //Couleur de l'image
        if (playerBean.getSexe()) {
            holder.iv_main.setColorFilter(boyColor);
            holder.row_iv_delete.setColorFilter(boyColor);
        }
        else {
            holder.iv_main.setColorFilter(girlColor);
            holder.row_iv_delete.setColorFilter(boyColor);
        }

        if (holder.bean.first) {
            holder.cv.setCardBackgroundColor(selectedColor);
        }
        else {
            holder.cv.setCardBackgroundColor(Color.WHITE);
        }

        holder.row_iv_injured.setVisibility(playerBean.getInjured() ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        if (filterActive) {
            return daoListFiltered.size();
        }
        else {
            return daoList.size();
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PlayerFilter(this);
        }
        return filter;
    }

    private Object getItem(int position) {
        if (filterActive) {
            return daoListFiltered.get(position);
        }
        else {
            return daoList.get(position);
        }
    }

    /* ---------------------------------
    // View Holder
    // -------------------------------- */

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Composant graphique
        public CardView cv;
        public final TextView row_tv1;
        public final TextView row_tv2;
        public ImageView iv_main, row_iv_delete, row_iv_injured;
        public View root; //Pour le onclick
        //Data

        public Pair<Boolean, PlayerBean> bean;
        private PlayerPickerCB callBack;

        public ViewHolder(View itemView, PlayerPickerCB callBack) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            row_tv1 = (TextView) itemView.findViewById(R.id.row_tv1);
            row_tv2 = (TextView) itemView.findViewById(R.id.row_tv2);
            root = itemView.findViewById(R.id.root);

            iv_main = (ImageView) itemView.findViewById(R.id.iv_main);
            row_iv_delete = (ImageView) itemView.findViewById(R.id.row_iv_delete);
            row_iv_injured = (ImageView) itemView.findViewById(R.id.row_iv_injured);

            row_iv_delete.setOnClickListener(this);
            root.setOnClickListener(this);

            this.callBack = callBack;
        }

        @Override
        public void onClick(final View v) {
            if (callBack != null) {
                //le postDelay permet de laisser finir l'effet du material design
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (v == root) {
                            bean.first = !bean.first;
                            if (bean.first) {
                                cv.setCardBackgroundColor(selectedColor);
                            }
                            else {
                                cv.setCardBackgroundColor(Color.WHITE);
                            }
                            callBack.PlayerPickerCB_clickOnItem(bean);
                        }
                        else if (v == row_iv_delete) {
                            callBack.PlayerPickerCB_onDeleteClick(bean);
                        }
                    }
                }, 200);
            }
        }
    }

    /* ---------------------------------
    // Filter Class
    // -------------------------------- */

    private static class PlayerFilter extends Filter {

        private PlayerPickerAdapter adapter;
        private final List<Pair<Boolean, PlayerBean>> filteredList;

        private PlayerFilter(PlayerPickerAdapter adapter) {
            super();
            this.filteredList = new ArrayList();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();

            final FilterResults results = new FilterResults();
            if (StringUtils.isBlank(constraint)) {
                return null;
            }
            else {
                for (final Pair<Boolean, PlayerBean> playerBean : adapter.daoList) {
                    if (StringUtils.containsIgnoreCase(playerBean.second.getName(), constraint.toString())) {
                        filteredList.add(playerBean);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            adapter.daoListFiltered.clear();

            if (results == null) {
                adapter.filterActive = false;
            }
            else {
                adapter.filterActive = true;
                adapter.daoListFiltered.addAll((Collection<? extends Pair<Boolean, PlayerBean>>) results.values);
            }
            adapter.notifyDataSetChanged();
        }
    }

    /* ---------------------------------
    // Interface
    // -------------------------------- */

    public interface PlayerPickerCB {
        void PlayerPickerCB_onDeleteClick(Pair<Boolean, PlayerBean> bean);

        void PlayerPickerCB_clickOnItem(Pair<Boolean, PlayerBean> bean);
    }

}