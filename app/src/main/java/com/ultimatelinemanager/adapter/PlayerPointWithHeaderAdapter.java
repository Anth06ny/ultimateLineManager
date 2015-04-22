package com.ultimatelinemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ultimatelinemanager.R;
import com.ultimatelinemanager.bean.PlayerPointBean;
import com.ultimatelinemanager.bean.Role;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by amonteiro on 20/04/2015.
 */
public class PlayerPointWithHeaderAdapter extends PlayerPointAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private int nbrHandler, nbrMiddle, nbrBoth;

    /**
     * @param context
     * @param selectAdapterI
     */
    public PlayerPointWithHeaderAdapter(Context context, PlayerPointAdapterI selectAdapterI) {
        super(context, selectAdapterI);

        nbrHandler = 0;
        nbrMiddle = 0;
        nbrBoth = 0;

    }

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_player_point_header, parent, false);
            return new ViewHolderHeader(view);
        }
        else if (viewType == TYPE_ITEM) {
            return super.onCreateViewHolder(parent, viewType);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {

        if (h instanceof ViewHolderHeader) {
            //cast holder to VHHeader and set data for header.
            onBindViewHolderHeader((ViewHolderHeader) h, position);
        }
        else if (h instanceof ViewHolderItem) {
            //on retire les 3 titre
            if (position < nbrHandler + 1) { //1 titre
                onBindViewHolderItem((ViewHolderItem) h, position - 1);
            }
            else if (position < (nbrHandler + nbrMiddle + 2)) { //2 titres
                onBindViewHolderItem((ViewHolderItem) h, position - 2);
            }
            else {
                //3 titres
                onBindViewHolderItem((ViewHolderItem) h, position - 3);
            }

        }
    }

    public void onBindViewHolderHeader(ViewHolderHeader header, int position) {

        if (position == 0) {
            header.rpp_tv_title.setText(context.getString(R.string.pa_handler));
            header.rpp_tv_value.setText(nbrHandler + "");
        }
        else if (position == nbrHandler + 1) {
            header.rpp_tv_title.setText(context.getString(R.string.pa_middle));
            header.rpp_tv_value.setText(nbrMiddle + "");
        }
        else {
            header.rpp_tv_title.setText(context.getString(R.string.pa_both));
            header.rpp_tv_value.setText(nbrBoth + "");
        }
    }

    /**
     * On utilise pas de filtre pour cette liste
     * @param position
     * @return
     */
    public PlayerPointBean getItem(int position) {
        return daoList.get(position);
    }

    @Override
    public int getItemCount() {
        return daoList.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == nbrHandler + 1 || position == nbrHandler + nbrMiddle + 2) {
            return TYPE_HEADER;
        }
        else {
            return TYPE_ITEM;
        }
    }

    /* ---------------------------------
    // Public
    // -------------------------------- */

    /**
     * Met à, jour la position des titres
     */
    public void refreshList() {
        //Pour la liste  trier par role
        nbrHandler = 0;
        nbrMiddle = 0;
        nbrBoth = 0;

        //On parcourt la liste pour identifier le nombre de handler / middle et both
        for (PlayerPointBean list : daoList) {
            if (list.getRoleInPoint() == Role.Handler) {
                nbrHandler++;
            }
            else if (list.getRoleInPoint() == Role.Middle) {
                nbrMiddle++;
            }
            else {
                nbrBoth++;
            }
        }
    }

    /**
     * Ajoute le joueur dans la bonne section
     * @param playerPointBean
     * @param role
     */
    public void addItem(PlayerPointBean playerPointBean, Role role) {
        playerPointBean.setRoleInPoint(role);
        switch (role) {
            case Handler:
                daoList.add(nbrHandler, playerPointBean); //pas de titre dans la liste
                notifyItemInserted(nbrHandler + 1); //
                nbrHandler++;
                //On met à jour le titre
                notifyItemChanged(0);
                break;
            case Middle:
                daoList.add(nbrHandler + nbrMiddle, playerPointBean);//pas de titre dans la liste
                notifyItemInserted(nbrHandler + nbrMiddle + 2);
                nbrMiddle++;
                notifyItemChanged(nbrHandler + 1);
                break;
            default:
                //On l'ajoute à la fin
                daoList.add(playerPointBean);//pas de titre dans la liste
                notifyItemInserted(daoList.size() + 2); //3 titre
                nbrBoth++;
                notifyItemChanged(nbrHandler + nbrMiddle + 2);
                break;
        }

    }

    public void removeItem(long playerId) {
        //on parcourt la liste pour le trouver
        int i = 0;
        for (PlayerPointBean temp : daoList) {
            if (temp.getPlayerBean().getId().equals(playerId)) {
                daoList.remove(i);
                switch (temp.getRoleInPoint()) {
                    case Handler:
                        notifyItemRemoved(i + 1); //1 titre
                        nbrHandler--;
                        //on met à jour le titre correspondant
                        notifyItemChanged(0);
                        break;
                    case Middle:
                        notifyItemRemoved(i + 2); //1 titre
                        nbrMiddle--;
                        notifyItemChanged(nbrHandler + 1);
                        break;
                    default:
                        notifyItemRemoved(i + 3); //1 titre
                        nbrBoth--;
                        notifyItemChanged(nbrHandler + nbrMiddle + 2);
                        break;
                }

                temp.setRoleInPoint(null);
                return;
            }
            i++;
        }
    }

    public void moveItem(PlayerPointBean playerPointBean, Role role) {
        //on ne fait rien si c'est le même role
        if (playerPointBean.getRoleInPoint() != role) {
            //on le retire
            removeItem(playerPointBean.getPlayerBean().getId());
            //et on l'ajoute
            addItem(playerPointBean, role);
        }

    }

    /**
     * Tri la liste dans l'ordre Handler / Middle /Both / Autre
     */
    public void sortList() {
        Collections.sort(daoList, new Comparator<PlayerPointBean>() {
            @Override
            public int compare(PlayerPointBean lhs, PlayerPointBean rhs) {
                if (lhs.getRoleInPoint() == rhs.getRoleInPoint()) {
                    return 0;
                }
                else if (lhs.getRoleInPoint() == null) {
                    return -1;
                }
                else if (rhs.getRoleInPoint() == null) {
                    return 0;
                }
                else if (lhs.getRoleInPoint() == Role.Handler) {
                    //L'autre est forcement middle ou both sinon on serait passé dans le if
                    return 1;
                }
                else if (rhs.getRoleInPoint() == Role.Handler) {
                    return -1;

                }
                else if (lhs.getRoleInPoint() == Role.Middle) {
                    return 1;
                }
                else if (rhs.getRoleInPoint() == Role.Middle) {
                    return -1;
                }
                else {
                    //Il ne reste que le cas ou les 2 sont both
                    return 0;
                }
            }
        });
    }

    /* ---------------------------------
        // View Holder
        // -------------------------------- */

    protected static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView rpp_tv_title, rpp_tv_value;

        public ViewHolderHeader(View itemView) {
            super(itemView);

            rpp_tv_title = (TextView) itemView.findViewById(R.id.rpp_tv_title);
            rpp_tv_value = (TextView) itemView.findViewById(R.id.rpp_tv_value);

        }
    }

}
