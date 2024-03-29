package com.ultimatelinemanager.bean;

import greendao.PlayerBean;

/**
 * Created by amonteiro on 20/04/2015.
 */
public class PlayerPointBean {

    private PlayerBean playerBean;
    private Role roleInPoint;
    private Long playingTime; //En milliseconde
    private Long restTime;//En milliseconde
    private int nbrPoint;

    public PlayerPointBean(PlayerBean playerBean) {
        this.playerBean = playerBean;
        roleInPoint = null;
        playingTime = restTime = (long) 0;
        nbrPoint = 0;
    }

    /* ---------------------------------
    // getter / setter
    // -------------------------------- */


    public PlayerBean getPlayerBean() {
        return playerBean;
    }

    public void setPlayerBean(PlayerBean playerBean) {
        this.playerBean = playerBean;
    }

    public Role getRoleInPoint() {
        return roleInPoint;
    }

    public void setRoleInPoint(Role roleInPoint) {
        this.roleInPoint = roleInPoint;
    }

    public Long getPlayingTime() {
        return playingTime;
    }

    public void setPlayingTime(Long playingTime) {
        this.playingTime = playingTime;
    }

    public Long getRestTime() {
        return restTime;
    }

    public void setRestTime(Long restTime) {
        this.restTime = restTime;
    }

    public int getNbrPoint() {
        return nbrPoint;
    }

    public void setNbrPoint(int nbrPoint) {
        this.nbrPoint = nbrPoint;
    }
}
