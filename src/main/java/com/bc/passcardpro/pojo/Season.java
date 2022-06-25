package com.bc.passcardpro.pojo;

import java.util.Date;

/**
 * @author Luckily_Baby
 * @date 2020/7/11 12:12
 */
public class Season {
    private String seasonId;
    private String seasonName;
    private Date startDate;
    private int continueDays;
    private int leftDays;

    public Season(){}
    public Season(String seasonId,String seasonName,Date startDate,int continueDays,int leftDays){
        this.seasonId=seasonId;
        this.seasonName=seasonName;
        this.startDate=startDate;
        this.continueDays=continueDays;
        this.leftDays=leftDays;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getContinueDays() {
        return continueDays;
    }

    public void setContinueDays(int continueDays) {
        this.continueDays = continueDays;
    }

    public int getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(int leftDays) {
        this.leftDays = leftDays;
    }

}
