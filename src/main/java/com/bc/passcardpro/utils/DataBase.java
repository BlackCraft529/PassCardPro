package com.bc.passcardpro.utils;

import com.bc.passcardpro.pojo.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luckily_Baby
 * @date 2020/7/6 22:10
 */
public class DataBase {

    /**
     * 打开界面的玩家
     *
     * key: 玩家  value：当前页数
     */
    public static ConcurrentHashMap<Player,Integer> viewer=new ConcurrentHashMap<>();

    /**
     * 前10玩家数据
     */
    public static ConcurrentHashMap<Integer , TopPlayer> top10PlayerMap=new ConcurrentHashMap<>();

    /**
     * 线程安全
     * 玩家临时数据-用于1.15+打开背包
     */
    public static ConcurrentHashMap<Player, PassCardPlayer> passCardPlayerMap =new ConcurrentHashMap<>();

    /**
     * 临时任务数据
     */
    public static ConcurrentHashMap<String,Mission> missionList=new ConcurrentHashMap<>();

    /**
     * 玩家临时任务数据
     */
    public static ConcurrentHashMap<Player, MissionPlayer> missionPlayerMap =new ConcurrentHashMap<>();

    /**
     * 玩家临时随机任务数据
     */
    public static ConcurrentHashMap<Player, List<Mission>> playerRandomMission=new ConcurrentHashMap<>();

    /**
     * 开启点击状态的玩家
     */
    public static List<Player> clicker =new ArrayList<>();

    /**
     * 赛季数据
     */
    public static ConcurrentHashMap<String, Season> seasonMap=new ConcurrentHashMap<>();

}
