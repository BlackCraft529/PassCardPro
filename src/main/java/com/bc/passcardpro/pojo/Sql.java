package com.bc.passcardpro.pojo;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 11:04
 */
public class Sql {
    private String databaseName;
    private String password;
    private String userName;
    private String point;
    private String ip;

    public Sql(String databaseName,String password,String userName,String point,String ip){
        this.databaseName=databaseName;
        this.password=password;
        this.userName=userName;
        this.point=point;
        this.ip=ip;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
