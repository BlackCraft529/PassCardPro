package com.bc.passcardpro.api;

import com.bc.passcardpro.pojo.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 19:51
 */
public interface PassCardAPI {
    /**
     * 更新玩家每周数据
     * @param player 玩家
     */
    void updatePlayerWeeklyDateAndMission(Player player);

    /**
     * 更新玩家每日日期
     * @param player 玩家
     */
    void updatePlayerDailyDateAndMission(Player player);

    /**
     * 更新表数据结构 1.3.2
     */
    void updateSqlTableData();

    /**
     * 更新玩家数据文件 -1.3.2
     * @param player 玩家
     */
    void updatePlayerData(Player player);

    /**
     * 启动数据连接
     *
     * @return 是否成功
     */
    boolean enableDataBase();

    /**
     * 获取玩家的任务列表
     *
     * @param player 玩家
     * @return 任务列表
     */
    List<Mission> getPlayerMissionList(Player player);

    /**
     * 更新玩家的任务进度
     * 前进行判断：
     * 1 是否已经达到最大完成次数
     * 2 是否已经到达本周最大点数
     * 后进行更新：
     * 1 判断是否到达要求
     *   -是：更新点数、完成次数
     *
     * @param player 玩家
     * @param missionId 任务ID
     * @param rate 新进度
     */
    void updatePlayerMissionRate(Player player,String missionId,String rate);

    /**
     * 更新玩家随机任务进度
     *
     * @param player 玩家
     * @param missionId 任务ID
     * @param rate 进度
     */
    void updatePlayerRandomMissionRate(Player player,String missionId,String rate);

    /**
     * 通过任务ID获取任务内容
     *
     * @param missionId 任务ID
     * @return 任务
     */
    Mission getMissionById(String missionId);

    /**
     * 根据玩家和任务ID获取具体的任务进度
     *
     * @param player 玩家
     * @return 玩家任务进度
     */
    Map<String,String> getPlayerMissionByPlayer(Player player);

    /**
     * 获取本周任务列表
     *
     * @param weekId 本周ID
     * @return 任务列表
     */
    List<Mission> getWeekMissionList(String weekId);


    /**
     * 获取玩家当前数据
     * @param player 玩家
     * @return PassCardPlayer实体类
     */
    PassCardPlayer getPlayerData(Player player);

    /**
     * 通过uuid获取玩家数据
     *
     * @param UUID 玩家UUID
     * @return PassCardPlayer实体类
     */
    PassCardPlayer getPlayerData(String UUID);

    /**
     * 检查并更新玩家任务数据等
     *
     * @param player 玩家
     */
    void checkAndUpdatePlayerData(Player player);

    /**
     * 根据uuid获取TopPlayer
     *
     * @param uuid 玩家uuid
     * @return topPlayer
     */
    TopPlayer getTopPlayer(String uuid);

    /**
     * 改变玩家点点数
     *
     * @param passCardPlayer 玩家
     * @param points 增加的点数 可为负数
     * @param checkLevel 是否检查等级
     * @param notice 是否提示
     */
    void updatePlayerPoint(PassCardPlayer passCardPlayer,double points,boolean checkLevel,boolean notice);

    /**
     * 检查玩家等级并更新
     *
     * @param passCardPlayer 玩家
     * @param isAdd 是否为增加等级
     */
    void updatePlayerLevel(PassCardPlayer passCardPlayer,boolean isAdd);

    /**
     * 检查玩家当前获取的周点数是否到达了上限
     *
     * @param passCardPlayer 玩家
     * @return 是否到达上限
     */
    boolean playerWeekPointIsMax(PassCardPlayer passCardPlayer);

    /**
     * 更新玩家每周点数
     *
     * @param passCardPlayer 玩家
     * @param points 点数 可为负数
     */
    void updatePlayerWeekPoint(PassCardPlayer passCardPlayer,double points);

    /**
     * 新建玩家数据
     *
     * @param player 玩家
     */
    void createPlayerData(Player player);

    /**
     * 新建玩家任务数据
     *
     * @param player 玩家
     */
    void createPlayerMissionData(Player player);

    /**
     * 获取一条随机任务
     *
     * @param needRandom 是否需要random项为true
     * @return 随机任务
     */
    Mission getRandomMission(boolean needRandom);

    /**
     * 新建一条任务数据
     *
     * @param mission 任务
     * @return  是否成功
     */
    boolean createNewMissionData(Mission mission);

    /**
     * 获取玩家的任务进度
     *
     * @param player 玩家
     * @return 任务进度列表
     */
    MissionPlayer getPlayerMissionData(Player player);

    /**
     * 获取玩家到达下一个等级需要多少点数
     *
     * @param player 玩家
     * @return 点数
     */
    double getPlayerNextLevelNeedPoint(Player player);

    /**
     * 获取前10玩家排名
     *
     * @return 排行Map
     */
    ConcurrentHashMap<Integer,TopPlayer> getTop10Player();

    /**
     * 重置每周任务
     */
    void resetWeekMission();

    /**
     * 删除玩家数据
     *
     * @param player 玩家
     */
    void deletePlayerMissionData(Player player);

    /**
     * 获取玩家随机任务数据
     *
     * @param player 玩家
     * @return 随机任务数据集
     */
    List<Mission> getPlayerRandomMissionData(Player player);

    /**
     * 更新玩家数据库数据
     *
     * @param passCardPlayer 玩家
     * @param gets 本次获取的普通奖励个数
     * @param vipGets 本次获取的VIP奖励个数
     */
    void updatePassCardPlayerGets(PassCardPlayer passCardPlayer, int gets, int vipGets);

    /**
     * 更新任务描述
     *
     * @param missionId 任务ID
     * @param desc 描述
     * @param player 玩家
     */
    void updateMissionDesc(String missionId,String desc,Player player);

    /**
     * 设置玩家的VIP状态
     *
     * @param player 玩家
     * @param setVip 是否vip
     * @param sender 指令发送者
     */
    void updatePlayerVip(Player player,boolean setVip, CommandSender sender);

    /**
     * 获取当前赛季数据
     *
     * @return 当前赛季
     */
    Season getNowSeasonData();

    /**
     * 重置玩家赛季数据
     */
    void resetPlayerSeasonData();

    /**
     * 新建一个赛季
     *
     * @param season 赛季
     */
    void updateNewSeason(Season season);

    /**
     * 更新一个赛季名称
     *
     * @param seasonName 赛季名称
     * @param sender 执行者
     */
    void updateSeasonName(String seasonName , CommandSender sender);

    /**
     * 更新一个赛季时长
     *
     * @param continueDays 持续时间
     * @param commandSender 执行者
     */
    void updateSeasonContinueDays(int continueDays , CommandSender commandSender);

    /**
     * 新建一个赛季数据
     */
    void createSeasonData();

    /**
     * 获取所有的任务数据
     *
     * @return 任务数据集
     */
    List<Mission> getAllMissionData();

    /**
     * 根据任务ID删除任务
     *
     * @param missionId 任务ID
     * @param commandSender 执行者
     */
    void deleteMissionById(String missionId,CommandSender commandSender);

    /**
     * 关闭数据连接
     */
    void closeDataBase();
}
