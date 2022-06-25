package com.bc.passcardpro.enums;

/**
 * @author Luckily_Baby
 * @date 2020/6/8 1:53
 */
public enum SqlCommand {
    //枚举
    CREATE_TABLE_PLAYER_DATA("create table if not exists `player_data` (`name` varchar(30) not null," +
            "`uuid` varchar(64) not null," +
            "`passcard_level` int not null default 0," +
            "`week_point` double not null default 0," +
            "`point` double not null default 0," +
            "`week_id` varchar(32) not null," +
            "`vip` boolean default false,"+
            "`gets` varchar(1000) ,"+
            "`vip_gets` varchar(1000),"+
            "`refresh_date` varchar(50),"+
            "primary key(`uuid`));"),
    CREATE_TABLE_SEASON_DATA("create table if not exists `season_data` (`season_id` varchar(32) not null," +
            "`name` varchar(30) not null," +
            "`start_date` date not null," +
            "`continue` int not null default 30," +
            "primary key(`season_id`));"),
    CREATE_TABLE_MISSION_DATA("create table if not exists `mission_data` (`mission_id` varchar(32) not null," +
            "`name` varchar(50) not null," +
            "`type` varchar(32) not null," +
            "`require` varchar(10) not null," +
            "`given_point` double not null default 1," +
            "`max_time` int default 1," +
            "`random` bool default false," +
            "`desc` varchar(500) ,"+
            "primary key(`mission_id`));"),
    CREATE_TABLE_PLAYER_MISSION_DATA("create table if not exists `player_mission_data` (`uuid` varchar(64) not null," +
            "`random_mission_id` varchar(300) ," +
            "`random_mission_state` varchar(300) ," +
            "`mission_1` varchar(100)," +
            "`mission_2` varchar(100)," +
            "`mission_3` varchar(100)," +
            "`mission_4` varchar(100)," +
            "`mission_5` varchar(100)," +
            "`mission_6` varchar(100)," +
            "primary key(`uuid`));"),
    CREATE_TABLE_WEEK_DATA("create table if not exists `week_data` (`week_id` varchar(32) not null," +
            "`start_date` date not null," +
            "`mission_1` varchar(32) not null," +
            "`mission_2` varchar(32) not null," +
            "`mission_3` varchar(32) not null," +
            "`mission_4` varchar(32) not null," +
            "`mission_5` varchar(32) not null," +
            "`mission_6` varchar(32) not null," +
            "primary key(`week_id`))"),
    SELECT_MISSION_BY_MISSION_ID("select * from mission_data where mission_id=?;"),
    SELECT_MISSION_BY_PLAYER("select * from player_mission_data where uuid=?"),
    SELECT_WEEK_MISSION("select * from week_data where week_id=?;"),
    SELECT_PLAYER_DATA("select * from player_data where uuid=?;"),
    UPDATE_PLAYER_MISSION_RATE("update player_mission_data set mission_#=? where uuid=?;"),
    UPDATE_PLAYER_POINT("update player_data set point=? where uuid=?;"),
    UPDATE_PLAYER_WEEK_POINT("update player_data set week_point=? where uuid=?;"),
    UPDATE_PLAYER_LEVEL("update player_data set passcard_level=? where uuid=?;"),
    CREATE_NEW_PLAYER_DATA("insert into player_data (name,uuid,passcard_level,week_point,point,week_id,gets,vip_gets,refresh_date) values (?,?,?,?,?,?,?,?,?);"),
    CREATE_NEW_PLAYER_MISSION_DATA("insert into player_mission_data " +
            "(uuid,random_mission_id,random_mission_state,mission_1,mission_2,mission_3,mission_4,mission_5,mission_6) " +
            "values (?,?,?,?,?,?,?,?,?);"),
    SELECT_RANDOM_MISSION_FROM_MISSION_DATA("select * from mission_data order by RAND() limit 1;"),
    CREATE_NEW_MISSION_DATA("insert into mission_data (mission_id,`name`,`type`,`require`,given_point,max_time,random,`desc`) " +
            "values (?,?,?,?,?,?,?,?);"),
    SELECT_TOP_PLAYER("select * from player_data order by passcard_level and `point` desc limit 10;"),
    CREATE_NEW_WEEK_MISSION("insert into week_data (week_id,start_date,mission_1,mission_2,mission_3,mission_4,mission_5,mission_6) values (?,?,?,?,?,?,?,?);"),
    DELETE_PLAYER_MISSION_DATA("delete from player_mission_data where uuid=?;"),
    UPDATE_PLAYER_RANDOM_MISSION_RATE("update player_mission_data set random_mission_id=? ,random_mission_state=? where uuid=?;"),
    UPDATE_PLAYER_GETS("update player_data set gets=?,vip_gets=? where uuid=?;"),
    UPDATE_MISSION_DESC("update mission_data set `desc`=? where mission_id=?;"),
    UPDATE_PLAYER_VIP("update player_data set vip=? where uuid=?;"),
    SELECT_NOW_SEASON_DATA("select * from season_data where season_id=?;"),
    DELETE_PLAYER_DATA_TABLE("truncate player_data;"),
    DELETE_PLAYER_MISSION_DATA_TABLE("truncate player_mission_data;"),
    DELETE_WEEK_MISSION_DATA_TABLE("truncate week_data;"),
    UPDATE_NEW_SEASON_DATA("update season_data set `name`=?,start_date=?,`continue`=? where season_id=?;"),
    UPDATE_SEASON_NAME("update season_data set name=? where season_id=?;"),
    UPDATE_SEASON_CONTINUE_DAY("update season_data set `continue`=? where season_id=?;"),
    CREATE_SEASON_DATA("insert into season_data (season_id,`name`,start_date,`continue`) values (?,?,?,?);"),
    SELECT_ALL_MISSION("select * from mission_data;"),
    DELETE_MISSION_DATA_BY_MISSION_ID("delete from mission_data where mission_id=?;"),
    UPDATE_VERSION_132("alter table player_data add column `refresh_date` varchar(50) default ?;"),
    UPDATE_PLAYER_REFRESH_DATE("update player_data set refresh_date=? where uuid=?;"),
    UPDATE_PLAYER_WEEK_ID("update player_data set week_id=? where uuid=?;");
    private String command;

    SqlCommand(String command) {
        this.command = command;
    }

    public String commandToString() {
        return command;
    }
}
