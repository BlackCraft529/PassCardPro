package com.bc.passcardpro.pojo;

import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 21:15
 */
public class PassCardPlayer {
    private Player player;
    private String weekId;
    private String date;
    private int passCardLevel;
    private double weekPoint;
    private double point;
    private String gets;
    private String vipGets;
    private boolean vip;
    private double nextLevelNeedPoint;

    @Override
    public String toString() {
        return "PassCardPlayer{" +
                "player=" + player +
                ", weekId='" + weekId + '\'' +
                ", passCardLevel=" + passCardLevel +
                ", weekPoint=" + weekPoint +
                ", point=" + point +
                ", gets='" + gets + '\'' +
                ", vipGets='" + vipGets + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true;}
        if (!(o instanceof PassCardPlayer)){ return false;}
        PassCardPlayer that = (PassCardPlayer) o;
        return getPlayer().equals(that.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayer());
    }

    public PassCardPlayer(Player player, String weekId, int passCardLevel, double weekPoint, double point){
        this.player=player;
        this.passCardLevel=passCardLevel;
        this.weekId=weekId;
        this.weekPoint=weekPoint;
        this.point=point;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getNextLevelNeedPoint() {
        return nextLevelNeedPoint;
    }

    public void setNextLevelNeedPoint(double nextLevelNeedPoint) {
        this.nextLevelNeedPoint = nextLevelNeedPoint;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getGets() {
        return gets;
    }

    public void setGets(String gets) {
        this.gets = gets;
    }

    public String getVipGets() {
        return vipGets;
    }

    public void setVipGets(String vipGets) {
        this.vipGets = vipGets;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getWeekId() {
        return weekId;
    }

    public void setWeekId(String weekId) {
        this.weekId = weekId;
    }

    public int getPassCardLevel() {
        return passCardLevel;
    }

    public void setPassCardLevel(int passCardLevel) {
        this.passCardLevel = passCardLevel;
    }

    public double getWeekPoint() {
        return weekPoint;
    }

    public void setWeekPoint(double weekPoint) {
        this.weekPoint = weekPoint;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }
}
