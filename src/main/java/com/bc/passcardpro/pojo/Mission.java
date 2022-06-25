package com.bc.passcardpro.pojo;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 16:00
 */
public class Mission {
    private String missionId;
    private String type;
    private String name;
    private String require;
    private String rate;
    private int finishTime;
    private double givenPoint;
    private int maxTime;
    private boolean random;
    private String desc;

    public Mission(String missionId,String type,String name,String require,double givenPoint,int maxTime,boolean random){
        this.missionId=missionId;
        this.type=type;
        this.name=name;
        this.require=require;
        this.givenPoint=givenPoint;
        this.maxTime=maxTime;
        this.random=random;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "missionId='" + missionId + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", require='" + require + '\'' +
                ", rate='" + rate + '\'' +
                ", finishTime=" + finishTime +
                ", givenPoint=" + givenPoint +
                ", maxTime=" + maxTime +
                ", random=" + random +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRate() {
        return rate;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public double getGivenPoint() {
        return givenPoint;
    }

    public void setGivenPoint(double givenPoint) {
        this.givenPoint = givenPoint;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }
}
