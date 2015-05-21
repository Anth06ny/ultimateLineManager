package com.ultimatelinemanager.bean;

import greendao.PlayerBean;

/**
 * Created by Anthony on 21/05/2015.
 */
public class PlayerStatBean {

    private PlayerBean playerBean;
    private long playingTime;
    private int numberPoint;
    private int goalAttaque, goalAttaqueSuccess;
    private int goalDefense, goalDefenseSuccess;


    public PlayerBean getPlayerBean() {
        return playerBean;
    }

    public void setPlayerBean(PlayerBean playerBean) {
        this.playerBean = playerBean;
    }

    public long getPlayingTime() {
        return playingTime;
    }

    public void setPlayingTime(long playingTime) {
        this.playingTime = playingTime;
    }

    public int getNumberPoint() {
        return numberPoint;
    }

    public void setNumberPoint(int numberPoint) {
        this.numberPoint = numberPoint;
    }

    public int getGoalAttaque() {
        return goalAttaque;
    }

    public void setGoalAttaque(int goalAttaque) {
        this.goalAttaque = goalAttaque;
    }

    public int getGoalAttaqueSuccess() {
        return goalAttaqueSuccess;
    }

    public void setGoalAttaqueSuccess(int goalAttaqueSuccess) {
        this.goalAttaqueSuccess = goalAttaqueSuccess;
    }

    public int getGoalDefense() {
        return goalDefense;
    }

    public void setGoalDefense(int goalDefense) {
        this.goalDefense = goalDefense;
    }

    public int getGoalDefenseSuccess() {
        return goalDefenseSuccess;
    }

    public void setGoalDefenseSuccess(int goalDefenseSuccess) {
        this.goalDefenseSuccess = goalDefenseSuccess;
    }
}
