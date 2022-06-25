package com.bc.passcardpro.pojo;

import java.util.Objects;

/**
 * @author Luckily_Baby
 * @date 2020/7/8 0:43
 */
public class TopPlayer {
    private String name;
    private double point;
    private int level;

    public TopPlayer(String name,double point,int level){
        this.level=level;
        this.name=name;
        this.point=point;
    }
    @Override
    public String toString() {
        return "TopPlayer{" +
                "Name='" + name + '\'' +
                ", point=" + point +
                ", level=" + level +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true; }
        if (!(o instanceof TopPlayer)){ return false; }
        TopPlayer topPlayer = (TopPlayer) o;
        return getName().equals(topPlayer.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
