package com.bc.passcardpro.api;

import com.bc.passcardpro.enums.SqlCommand;
import com.bc.passcardpro.loader.AwardLoader;
import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.loader.LangLoader;
import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.*;
import com.bc.passcardpro.getter.IdGetter;
import com.bc.passcardpro.task.RefreshPassCardPlayer;
import com.bc.passcardpro.task.RefreshPlayerMission;
import com.bc.passcardpro.task.RefreshSeasonData;
import com.bc.passcardpro.task.RefreshTopPlayer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 11:11
 */
public class SqlManager implements PassCardAPI{
    private HikariConfig hikariConfig = new HikariConfig();
    private HikariDataSource sqlConnectionPool;
    private Sql sqlDate;
    private DecimalFormat df = new DecimalFormat("#.0");

    /**
     * 启动数据库
     * @throws SQLException 数据库错误
     */
    private void enableSql() throws SQLException{
        sqlDate= CfgLoader.sql;
        Connection connection=null;
        try{
            connectMySql();
            connection=sqlConnectionPool.getConnection();
            // 创建表
            PreparedStatement createPlayerDataTable=connection.prepareStatement(SqlCommand.CREATE_TABLE_PLAYER_DATA.commandToString());
            PreparedStatement createSeasonDataTable=connection.prepareStatement(SqlCommand.CREATE_TABLE_SEASON_DATA.commandToString());
            PreparedStatement createMissionDataTable=connection.prepareStatement(SqlCommand.CREATE_TABLE_MISSION_DATA.commandToString());
            PreparedStatement createPlayerMissionDataTable=connection.prepareStatement(SqlCommand.CREATE_TABLE_PLAYER_MISSION_DATA.commandToString());
            PreparedStatement createWeekDataTable=connection.prepareStatement( SqlCommand.CREATE_TABLE_WEEK_DATA.commandToString());
            doCommand(createPlayerDataTable);
            doCommand(createSeasonDataTable);
            doCommand(createMissionDataTable);
            doCommand(createPlayerMissionDataTable);
            doCommand(createWeekDataTable);
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 连接数据库
     */
    private void connectMySql() throws SQLException {
        hikariConfig.setJdbcUrl("jdbc:mysql://" + sqlDate.getIp() + ":"
                + sqlDate.getPoint() + "/" + sqlDate.getDatabaseName() + "?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
        hikariConfig.setUsername(sqlDate.getUserName());
        hikariConfig.setPassword(sqlDate.getPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "500");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setMaximumPoolSize(500);
        hikariConfig.setIdleTimeout(60000);
        hikariConfig.setConnectionTimeout(600000);
        hikariConfig.setValidationTimeout(3000);
        hikariConfig.setMaxLifetime(60000);
        hikariConfig.setAutoCommit(true);

        sqlConnectionPool = new HikariDataSource(hikariConfig);
    }

    /**
     * 执行指令
     *
     * @param ps 指令
     */
    private void doCommand(PreparedStatement ps) {
        try {
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4执行指令失败,Error:");
            e.printStackTrace();
        }
    }

    /**
     * 获取玩家任务进度
     *
     * @param player 玩家
     * @return 任务进度
     */
    @Override
    public List<Mission> getPlayerMissionList(Player player){
        List<Mission> missionList=new ArrayList<>();
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_MISSION_BY_PLAYER.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                List<Mission> missions=getWeekMissionList(IdGetter.getWeekId());
                int missionNext=1;
                Map<String,String> missionRateMap=getPlayerMissionByPlayer(player);
                for(Mission mission:missions){
                    String missionRate=missionRateMap.get("mission_"+missionNext++);
                    mission.setRate(missionRate.split(",")[1]);
                    mission.setFinishTime(Integer.parseInt(missionRate.split(",")[0]));
                    missionList.add(mission);
                }
            }
            resultSet.close();
            preparedStatement.close();
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return missionList;
    }

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
     * @param missionId 非任务ID
     * @param rate 新进度
     */
    @Override
    public void updatePlayerMissionRate(Player player, String missionId, String rate){
        Mission mission=null;
        int missionNext=-1,i=1;
        for(Mission ms:getPlayerMissionList(player)){
            if(ms.getMissionId().equalsIgnoreCase(missionId)){
                mission=ms;
                missionNext=i;
            }
            i++;
        }
        if(mission==null){
            player.sendMessage(LangLoader.title+LangLoader.errorMissionNotExist);
            return;
        }
        if(mission.getFinishTime()>=mission.getMaxTime()){
            return;
        }
        PassCardPlayer passCardPlayer=getPlayerData(player);
        double maxWeekPoint=passCardPlayer.isVip()?CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
        if(maxWeekPoint<=passCardPlayer.getPoint()){
            return;
        }
        //进行任务进度更新
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=
                    connection.prepareStatement(SqlCommand.UPDATE_PLAYER_MISSION_RATE
                            .commandToString().replaceAll("#",""+missionNext));
            //任务数据格式：完成次数,当前进度,任务ID 注：当前进更新进度，不需要更新完成次数，完成次数在后方检查
            String newRateString=mission.getFinishTime()+","+rate+","+mission.getMissionId();
            preparedStatement.setString(1,newRateString);
            preparedStatement.setString(2,player.getUniqueId().toString());
            doCommand(preparedStatement);
            String rateDouble=df.format(Double.parseDouble(rate));
            String msg = LangLoader.noticeUpdateMission.replaceAll("<mission>", mission.getName())
                    .replaceAll("<missionRate>", rateDouble).replaceAll("<missionRequire>", df.format(Double.parseDouble(mission.getRequire())));
            if(CfgLoader.noticeMsg) {
                player.sendMessage(LangLoader.title + msg);
            }else if(CfgLoader.noticeTitle) {
                player.sendTitle("", msg, 20, 0, 20);
            }
            //检查进度
            checkAndUpdatePlayerData(player);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 更新玩家随机任务进度
     *
     * @param player    玩家
     * @param missionId 任务ID
     * @param rate      进度
     */
    @Override
    public void updatePlayerRandomMissionRate(Player player, String missionId, String rate) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PassCardPlayer passCardPlayer=getPlayerData(player);
            double maxWeekPoint=passCardPlayer.isVip()?CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
            if(maxWeekPoint<=passCardPlayer.getPoint()){
                return;
            }
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_MISSION_BY_PLAYER.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet=preparedStatement.executeQuery();
            String randomMissionId="",randomMissionRate="";
            while (resultSet.next()){
                randomMissionId=resultSet.getString("random_mission_id");
                randomMissionRate=resultSet.getString("random_mission_state");
                break;
            }
            resultSet.close();
            preparedStatement.close();
            if(randomMissionId.contains(missionId)){
                int index=0;
                for(String id:randomMissionId.split(",")){
                    if(id.equalsIgnoreCase(missionId)){
                        break;
                    }
                    index++;
                }
                Mission mission=getMissionById(missionId);
                String[] missionRate=randomMissionRate.split("-");
                if(Integer.parseInt(missionRate[index].split(",")[0])>=mission.getMaxTime()){
                    return;
                }else {
                    missionRate[index]=missionRate[index].split(",")[0]+","+rate;
                }
                String newRateString="";
                for(int i=0;i<missionRate.length;i++){
                    newRateString+=missionRate[i];
                    if(i+1<missionRate.length){
                        newRateString+="-";
                    }
                }
                PreparedStatement preparedStatement1=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_RANDOM_MISSION_RATE.commandToString());
                preparedStatement1.setString(1,randomMissionId);
                preparedStatement1.setString(2,newRateString);
                preparedStatement1.setString(3,player.getUniqueId().toString());
                doCommand(preparedStatement1);
                //检查任务是否完成 & Up Level
                checkAndUpdatePlayerData(player);
                String msg = LangLoader.noticeUpdateMission.replaceAll("<mission>", mission.getName())
                        .replaceAll("<missionRate>", df.format(Double.parseDouble(rate))).replaceAll("<missionRequire>", df.format(Double.parseDouble(mission.getRequire())));
                if(CfgLoader.noticeMsg) {
                    player.sendMessage(LangLoader.title + msg);
                }else if(CfgLoader.noticeTitle) {
                    player.sendTitle("", msg, 20, 0, 20);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 通过任务ID获取任务内容
     *
     * @param missionId 任务ID
     * @return 任务
     */
    @Override
    public Mission getMissionById(String missionId) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_MISSION_BY_MISSION_ID.commandToString());
            preparedStatement.setString(1,missionId);
            ResultSet resultSet=preparedStatement.executeQuery();
            Mission mission=null;
            while(resultSet.next()){
                mission=new Mission(missionId,resultSet.getString("type"),resultSet.getString("name")
                        , resultSet.getString("require"),resultSet.getDouble("given_point")
                        , resultSet.getInt("max_time"),resultSet.getBoolean("random"));
                mission.setDesc(resultSet.getString("desc"));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return mission;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return null;
    }

    /**
     * 根据玩家和任务ID获取具体的任务进度
     *
     * @param player 玩家
     * @return 玩家任务进度
     */
    @Override
    public Map<String,String> getPlayerMissionByPlayer(Player player) {
        Connection connection=null;
        Map<String,String> resultMap=new HashMap<>();
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_MISSION_BY_PLAYER.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                //完成次数,当前进度,任务 ID
                resultMap.put("mission_1",resultSet.getString("mission_1"));
                resultMap.put("mission_2",resultSet.getString("mission_2"));
                resultMap.put("mission_3",resultSet.getString("mission_3"));
                resultMap.put("mission_4",resultSet.getString("mission_4"));
                resultMap.put("mission_5",resultSet.getString("mission_5"));
                resultMap.put("mission_6",resultSet.getString("mission_6"));
            }
            resultSet.close();
            preparedStatement.close();
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return resultMap;
    }

    /**
     * 获取本周任务列表
     *
     * @return 任务列表
     */
    @Override
    public List<Mission> getWeekMissionList(String weekId) {
        List<Mission> weekMissions=new ArrayList<>();
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_WEEK_MISSION.commandToString());
            preparedStatement.setString(1,weekId);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                for(int i=1;i<=6;i++) {
                    weekMissions.add(getMissionById(resultSet.getString("mission_" + i)));
                }
            }
            resultSet.close();
            preparedStatement.close();
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return weekMissions;
    }

    /**
     * 获取玩家当前数据
     *
     * @param player 玩家
     * @return PassCardPlayer实体类
     */
    @Override
    public PassCardPlayer getPlayerData(Player player) {
        Connection connection=null;
        PassCardPlayer passCardPlayer=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_PLAYER_DATA.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()) {
                passCardPlayer = new PassCardPlayer(player, resultSet.getString("week_id")
                        , resultSet.getInt("passcard_level"), resultSet.getDouble("week_point")
                        , resultSet.getDouble("point"));
                passCardPlayer.setGets(resultSet.getString("gets"));
                passCardPlayer.setVipGets(resultSet.getString("vip_gets"));
                passCardPlayer.setVip(resultSet.getBoolean("vip"));
                passCardPlayer.setDate(resultSet.getString("refresh_date"));
                break;
            }
            resultSet.close();
            preparedStatement.close();
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return passCardPlayer;
    }

    /**
     * 通过uuid获取玩家数据
     *
     * @param UUID 玩家UUID
     * @return PassCardPlayer实体类
     * @deprecated 过时
     */
    @Override
    @Deprecated
    public PassCardPlayer getPlayerData(String UUID) {
        Connection connection=null;
        PassCardPlayer passCardPlayer=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_PLAYER_DATA.commandToString());
            preparedStatement.setString(1,UUID);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()) {
                passCardPlayer = new PassCardPlayer(Bukkit.getPlayer(UUID), resultSet.getString("week_id")
                        , resultSet.getInt("passcard_level"), resultSet.getDouble("week_point")
                        , resultSet.getDouble("point"));
                passCardPlayer.setGets(resultSet.getString("gets"));
                passCardPlayer.setVipGets(resultSet.getString("vip_gets"));
                passCardPlayer.setVip(resultSet.getBoolean("vip"));
                break;
            }
            resultSet.close();
            preparedStatement.close();
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return passCardPlayer;
    }

    /**
     * 检查并更新玩家任务数据等
     *
     * @param player 玩家
     */
    @Override
    public void checkAndUpdatePlayerData(Player player) {
        List<Mission> missions=getPlayerMissionList(player);
        List<Mission> randomMission=getPlayerRandomMissionData(player);
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            for (int i = 0; i < missions.size(); i++) {
                if (Double.parseDouble(missions.get(i).getRate()) >= Double.parseDouble(missions.get(i).getRequire())) {
                    PreparedStatement preparedStatement=
                            connection.prepareStatement(SqlCommand.UPDATE_PLAYER_MISSION_RATE
                                    .commandToString().replaceAll("#",(i+1)+""));
                    //更新完成次数+1，进行玩家提示
                    preparedStatement.setString(1,(missions.get(i).getFinishTime()+1)+",0,"+missions.get(i).getMissionId());
                    preparedStatement.setString(2,player.getUniqueId().toString());
                    doCommand(preparedStatement);
                    //增加点数等
                    double addPoint=missions.get(i).getGivenPoint();
                    updatePlayerPoint(getPlayerData(player),addPoint,true,true);
                    player.sendMessage(LangLoader.title+LangLoader.noticeMissionComplete.replaceAll("<mission>",missions.get(i).getName()));
                }
            }
            //随机任务 完成更新
            for(int i = 0; i < randomMission.size(); i++){
                if(Double.parseDouble(randomMission.get(i).getRate()) >= Double.parseDouble(randomMission.get(i).getRequire())){
                    String randomMissionState="",randomMissionId="";
                    PreparedStatement preparedStatementGet=connection.prepareStatement(SqlCommand.SELECT_MISSION_BY_PLAYER.commandToString());
                    preparedStatementGet.setString(1,player.getUniqueId().toString());
                    ResultSet resultSet=preparedStatementGet.executeQuery();
                    while (resultSet.next()){
                        randomMissionState=resultSet.getString("random_mission_state");
                        randomMissionId=resultSet.getString("random_mission_id");
                        break;
                    }
                    resultSet.close();
                    preparedStatementGet.close();
                    String[] randomMissionStateArray=randomMissionState.split("-");
                    for(int s=0;s<randomMissionStateArray.length;s++){
                        if(s==i){
                            int finishTime=Integer.parseInt(randomMissionStateArray[s].split(",")[0])+1;
                            randomMissionStateArray[s]=finishTime+",0";
                        }
                    }
                    randomMissionState=randomMissionStateArray[0]+"-"+randomMissionStateArray[1]+"-"+randomMissionStateArray[2];
                    PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_RANDOM_MISSION_RATE.commandToString());
                    preparedStatement.setString(1,randomMissionId);
                    preparedStatement.setString(2,randomMissionState);
                    preparedStatement.setString(3,player.getUniqueId().toString());
                    doCommand(preparedStatement);
                    double addPoint=randomMission.get(i).getGivenPoint();
                    updatePlayerPoint(getPlayerData(player),addPoint,true,true);
                    player.sendMessage(LangLoader.title+LangLoader.noticeMissionComplete.replaceAll("<mission>",randomMission.get(i).getName()));
                }
            }
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 根据uuid获取TopPlayer
     *
     * @param uuid 玩家uuid
     * @return topPlayer
     */
    @Override
    public TopPlayer getTopPlayer(String uuid) {
        Connection connection=null;
        TopPlayer topPlayer=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_PLAYER_DATA.commandToString());
            preparedStatement.setString(1, uuid);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()) {
                topPlayer = new TopPlayer(resultSet.getString("name")
                        , resultSet.getDouble("point"), resultSet.getInt("passcard_level"));
                break;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return topPlayer;
    }

    /**
     * 增加玩家点点数
     *
     * @param passCardPlayer 玩家
     * @param points 增加的点数 可为负数
     * @param checkLevel 是否检查等级
     */
    @Override
    public void updatePlayerPoint(PassCardPlayer passCardPlayer, double points,boolean checkLevel,boolean notice) {
        Connection connection=null;
        try {
            Player player=passCardPlayer.getPlayer();
            double playerWeekPoint=passCardPlayer.getWeekPoint();
            if(playerWeekPointIsMax(passCardPlayer)){
                connection=sqlConnectionPool.getConnection();
                PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_POINT.commandToString());
                preparedStatement.setDouble(1,points);
                preparedStatement.setString(2,player.getUniqueId().toString());
                doCommand(preparedStatement);
                if(checkLevel) {
                    updatePlayerWeekPoint(passCardPlayer, points);
                }
                String changeType=points>0?"+":"-";
                if(notice) {
                    player.sendMessage(LangLoader.title + LangLoader.noticeUpdatePoint.replaceAll("<changeType>", changeType)
                            .replaceAll("<point>", points + ""));
                }
                //自动更新等级
                if(checkLevel) {
                    updatePlayerLevel(getPlayerData(player), points > 0);
                }
            }else{
                player.sendMessage(LangLoader.title+LangLoader.errorPointIsMax.replaceAll("<weekPoint>",playerWeekPoint+"")
                        .replaceAll("<maxWeekPoint>",passCardPlayer.isVip()?CfgLoader.vipWeekMaxPoint+"":CfgLoader.weekMaxPoint+""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 检查玩家等级并更新
     *
     * @param passCardPlayer 玩家
     * @param isAdd 是否是增加的
     *
     */
    @Override
    public void updatePlayerLevel(PassCardPlayer passCardPlayer,boolean isAdd) {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            Player player=passCardPlayer.getPlayer();
            int playerLevel=isAdd?passCardPlayer.getPassCardLevel():0,addLevel=1;
            double playerNowPoint=passCardPlayer.getPoint();
            while (AwardLoader.award.get((playerLevel + addLevel) + "") != null && playerNowPoint>0) {
                if (playerNowPoint >= AwardLoader.award.getDouble((playerLevel + addLevel) + ".Point")) {
                    PreparedStatement preparedStatement = connection.prepareStatement(SqlCommand.UPDATE_PLAYER_LEVEL.commandToString());
                    //增加玩家等级
                    int nowLevel=(playerLevel + addLevel);
                    preparedStatement.setInt(1,nowLevel);
                    preparedStatement.setString(2,player.getUniqueId().toString());
                    doCommand(preparedStatement);
                    player.sendMessage(LangLoader.title+LangLoader.noticeLevelUp
                            .replaceAll("<oldLevel>",passCardPlayer.getPassCardLevel()+"")
                            .replaceAll("<newLevel>",getPlayerData(player).getPassCardLevel()+""));
                    //更新玩家点数数据.
                    playerNowPoint-=AwardLoader.award.getDouble((playerLevel + addLevel) + ".Point");
                    updatePlayerPoint(getPlayerData(player), playerNowPoint,false,false);
                }else{
                    break;
                }
                addLevel++;
            }
            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 检查玩家当前获取的周点数是否到达了上限
     *
     * @param passCardPlayer 玩家
     * @return 是否到达上限
     */
    @Override
    public boolean playerWeekPointIsMax(PassCardPlayer passCardPlayer) {
        Connection connection=null;
        try{
            double playerWeenPoint=passCardPlayer.getWeekPoint();
            double maxWeekPoint=passCardPlayer.isVip()?CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
            return !(playerWeenPoint >= maxWeekPoint);
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 更新玩家每周点数
     *
     * @param passCardPlayer 玩家
     * @param points 点数 可为负数
     */
    @Override
    public void updatePlayerWeekPoint(PassCardPlayer passCardPlayer, double points) {
        if(points<0){
            return;
        }
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_WEEK_POINT.commandToString());
            preparedStatement.setDouble(1,passCardPlayer.getWeekPoint()+points);
            preparedStatement.setString(2,passCardPlayer.getPlayer().getUniqueId().toString());
            doCommand(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 新建玩家数据
     *
     * @param player 玩家
     */
    @Override
    public void createPlayerData(Player player) {
        Connection connection=null;
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.CREATE_NEW_PLAYER_DATA.commandToString());
            //insert into player_data set (name,uuid,passcard_level,week_point,point,week_id,gets,vip_gets) values (?,?,?,?,?,?,?,?);
            preparedStatement.setString(1,player.getName());
            preparedStatement.setString(2,player.getUniqueId().toString());
            preparedStatement.setInt(3,0);
            preparedStatement.setDouble(4,0D);
            preparedStatement.setDouble(5,0D);
            preparedStatement.setString(6,IdGetter.getWeekId());
            preparedStatement.setString(7,"0,");
            preparedStatement.setString(8,"0,");
            preparedStatement.setString(9,dateFormat.format(new Date()));
            doCommand(preparedStatement);
            //创建任务数据
            createPlayerMissionData(player);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a成功为 §e"+player.getName()+" §a[§b"+player.getUniqueId().toString()+"§a] 创建数据!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 新建玩家任务数据
     *
     * @param player 玩家
     */
    @Override
    public void createPlayerMissionData(Player player) {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            List<Mission> weekMissions=getWeekMissionList(IdGetter.getWeekId());
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.CREATE_NEW_PLAYER_MISSION_DATA.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            //random mission data
            StringBuilder randomMissionId= new StringBuilder();
            for(int i=1;i<=3;i++){
                Mission mission=getRandomMission(true);
                while(randomMissionId.toString().contains(mission.getMissionId())){
                    mission=getRandomMission(true);
                }
                randomMissionId.append(mission.getMissionId());
                if(i<3){
                    randomMissionId.append(",");
                }
            }
            preparedStatement.setString(2,randomMissionId.toString());
            preparedStatement.setString(3,"0,0-0,0-0,0");
            for(int i=0;i<weekMissions.size();i++){
                String missionData="0,0,"+weekMissions.get(i).getMissionId();
                preparedStatement.setString(4+i,missionData);
            }
            doCommand(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 获取一条随机任务
     *
     * @return 随机任务
     */
    @Override
    public Mission getRandomMission(boolean needRandom) {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_RANDOM_MISSION_FROM_MISSION_DATA.commandToString());
            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();
            while(true) {
                if(resultSet.getBoolean("random")||!needRandom) {
                    //String missionId,String type,String name,String require,double givenPoint,int maxTime,boolean random
                    Mission mission= new Mission(resultSet.getString("mission_id"),resultSet.getString("type")
                            ,resultSet.getString("name"),resultSet.getString("require")
                    ,resultSet.getDouble("given_point"),resultSet.getInt("max_time"),resultSet.getBoolean("random"));
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                    return mission;
                }else{
                    resultSet=preparedStatement.executeQuery();
                    resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return null;
    }

    /**
     * 新建一条任务数据
     *
     * @param mission 任务
     * @return 是否成功
     */
    @Override
    public boolean createNewMissionData(Mission mission) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.CREATE_NEW_MISSION_DATA.commandToString());
            preparedStatement.setString(1,mission.getMissionId());
            preparedStatement.setString(2,mission.getName());
            preparedStatement.setString(3,mission.getType());
            preparedStatement.setString(4,mission.getRequire());
            preparedStatement.setDouble(5,mission.getGivenPoint());
            preparedStatement.setInt(6,mission.getMaxTime());
            preparedStatement.setBoolean(7,mission.isRandom());
            preparedStatement.setString(8,mission.getDesc()==null?"":mission.getDesc());
            doCommand(preparedStatement);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 获取玩家的任务进度
     *
     * @param player 玩家
     * @return 任务进度列表
     */
    @Override
    public MissionPlayer getPlayerMissionData(Player player) {
        Connection connection=null;
        ConcurrentHashMap<String,Mission> missionMap=new ConcurrentHashMap<>();
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_MISSION_BY_PLAYER.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                //String missionId,String type,String name,String require,double givenPoint,int maxTime,boolean random
                for(int missionSlot=1;missionSlot<7;missionSlot++) {
                    String missionId=resultSet.getString("mission_"+missionSlot).split(",")[2];
                    int finishTime=Integer.parseInt(resultSet.getString("mission_"+missionSlot).split(",")[0]);
                    double rate=Double.parseDouble(resultSet.getString("mission_"+missionSlot).split(",")[1]);
                    Mission mission=getMissionById(missionId);
                    mission.setRate(rate+"");
                    mission.setFinishTime(finishTime);
                    missionMap.put("mission_"+missionSlot,mission);
                }
                return new MissionPlayer(player,getPlayerData(player),missionMap);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return null;
    }

    /**
     * 获取玩家到达下一个等级需要多少点数
     *
     * @param player 玩家
     * @return 点数
     */
    @Override
    public double getPlayerNextLevelNeedPoint(Player player) {
        Connection connection=null;
        try{
            PassCardPlayer passCardPlayer=getPlayerData(player);
            if(passCardPlayer==null){
                return 0;
            }
            int level=passCardPlayer.getPassCardLevel();
            if(AwardLoader.award.get((level+1)+"")!=null){
                return AwardLoader.award.getDouble((level+1)+".Point")-passCardPlayer.getPoint();
            }else{
                return 0;
            }
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 获取前10玩家排名
     *
     * @return 排行Map
     */
    @Override
    public ConcurrentHashMap<Integer, TopPlayer> getTop10Player() {
        ConcurrentHashMap<Integer , TopPlayer> top10Player=new ConcurrentHashMap<>();
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_TOP_PLAYER.commandToString());
            ResultSet resultSet=preparedStatement.executeQuery();
            int top=1;
            while (resultSet.next()){
                top10Player.put(top++, getTopPlayer(Objects.requireNonNull(resultSet.getString("uuid"))));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return top10Player;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return top10Player;
    }

    /**
     * 重置每周任务
     */
    @Override
    public void resetWeekMission() {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.CREATE_NEW_WEEK_MISSION.commandToString());
            List<Mission> randomMissionList=new ArrayList<>();
            for(int i=0;i<6;i++){
                Mission mission=getRandomMission(false);
                for (int s=0; s < randomMissionList.size(); s++) {
                    if (randomMissionList.get(s).getMissionId().equalsIgnoreCase(mission.getMissionId())) {
                        mission = getRandomMission(false);
                        s=0;
                    }
                }
                randomMissionList.add(mission);
            }
            preparedStatement.setString(1,IdGetter.getWeekId());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            preparedStatement.setString(2,df.format(new Date()));
            for(int i=1;i<=6;i++){
                preparedStatement.setString(i+2,randomMissionList.get((i-1)).getMissionId());
            }
            doCommand(preparedStatement);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4每周任务内容重置: "+IdGetter.getWeekId()+" !");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 删除玩家数据
     */
    @Override
    public void deletePlayerMissionData(Player player) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.DELETE_PLAYER_MISSION_DATA.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            doCommand(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 获取玩家随机任务数据
     *
     * @param player 玩家
     * @return 随机任务数据集
     */
    @Override
    public List<Mission> getPlayerRandomMissionData(Player player) {
        Connection connection=null;
        List<Mission> missionList=new ArrayList<>();
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_MISSION_BY_PLAYER.commandToString());
            preparedStatement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                String randomMissionIdString = resultSet.getString("random_mission_id");
                String randomMissionStateString = resultSet.getString("random_mission_state");
                for (int i=0;i<3;i++) {
                    String thisMissionState=randomMissionStateString.split("-")[i];
                    Mission mission=getMissionById(randomMissionIdString.split(",")[i]);
                    mission.setRate(thisMissionState.split(",")[1]);
                    mission.setFinishTime(Integer.parseInt(thisMissionState.split(",")[0]));
                    missionList.add(mission);
                }
                break;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return missionList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return missionList;
    }

    /**
     * 更新玩家数据库数据
     *
     * @param passCardPlayer 玩家
     */
    @Override
    public void updatePassCardPlayerGets(PassCardPlayer passCardPlayer, int gets, int vipGets) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_GETS.commandToString());
            preparedStatement.setString(1,passCardPlayer.getGets());
            preparedStatement.setString(2,passCardPlayer.getVipGets());
            preparedStatement.setString(3,passCardPlayer.getPlayer().getUniqueId().toString());
            doCommand(preparedStatement);
            passCardPlayer.getPlayer().sendMessage(LangLoader.title+
                    LangLoader.noticeGetAward
                        .replaceAll("<countGets>",(gets+vipGets)+"")
                        .replaceAll("<gets>",gets+"")
                            .replaceAll("<vipGets>",vipGets+""));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 更新任务描述
     *
     * @param missionId 任务ID
     * @param desc      描述
     * @param player    玩家
     */
    @Override
    public void updateMissionDesc(String missionId, String desc,Player player) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            Mission mission=getMissionById(missionId);
            if(mission==null){
                player.sendMessage(LangLoader.title+LangLoader.errorMissionNotExist);
                return;
            }
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_MISSION_DESC.commandToString());
            preparedStatement.setString(1,desc);
            preparedStatement.setString(2,missionId);
            doCommand(preparedStatement);
            player.sendMessage(LangLoader.title+LangLoader.noticeUpdateDesc
                    .replaceAll("<mission>",mission.getName()).replaceAll("<desc>",desc));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 设置玩家的VIP状态
     *
     * @param player 玩家
     * @param setVip 是否vip
     * @param sender 指令发送者
     */
    @Override
    public void updatePlayerVip(Player player, boolean setVip, CommandSender sender) {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_VIP.commandToString());
            preparedStatement.setBoolean(1,setVip);
            preparedStatement.setString(2,player.getUniqueId().toString());
            doCommand(preparedStatement);
            sender.sendMessage(LangLoader.title+LangLoader.noticeUpdateVip
                    .replaceAll("<player>",player.getName()).replaceAll("<vip>",setVip?CfgLoader.vipTitle:CfgLoader.defaultTitle));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 获取当前赛季数据
     *
     * @return 当前赛季
     */
    @Override
    public Season getNowSeasonData() {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            Season season=null;
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_NOW_SEASON_DATA.commandToString());
            preparedStatement.setString(1,"season");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                int leftDays=resultSet.getInt("continue")
                        -com.bc.passcardpro.utils.Time.daysBetween(df.format(resultSet.getDate("start_date")),df.format(new Date()));
                season = new Season("season",resultSet.getString("name")
                        ,resultSet.getDate("start_date"),resultSet.getInt("continue"),leftDays);
                break;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return season;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return null;
    }

    /**
     * 重置赛季数据
     */
    @Override
    public void resetPlayerSeasonData() {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.DELETE_PLAYER_DATA_TABLE.commandToString());
            doCommand(preparedStatement);
            preparedStatement=connection.prepareStatement(SqlCommand.DELETE_PLAYER_MISSION_DATA_TABLE.commandToString());
            doCommand(preparedStatement);
            preparedStatement=connection.prepareStatement(SqlCommand.DELETE_WEEK_MISSION_DATA_TABLE.commandToString());
            doCommand(preparedStatement);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4用户数据已全部重置...");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 新建一个赛季
     *
     * @param season 赛季
     */
    @Override
    public void updateNewSeason(Season season) {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_NEW_SEASON_DATA.commandToString());
            //update season_data set name=?,start_date=?,continue=? where season_id=?;
            preparedStatement.setString(1,season.getSeasonName().replaceAll("&","§"));
            preparedStatement.setDate(2, new java.sql.Date(season.getStartDate().getTime()));
            preparedStatement.setInt(3,season.getContinueDays());
            preparedStatement.setString(4,"season");
            doCommand(preparedStatement);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4赛季数据已全部重置...");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 更新一个赛季名称
     *
     * @param seasonName 赛季名称
     * @param sender 执行者
     */
    @Override
    public void updateSeasonName(String seasonName,CommandSender sender) {
        Connection connection=null;
        seasonName=seasonName.replaceAll("&","§");
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_SEASON_NAME.commandToString());
            preparedStatement.setString(1,seasonName);
            preparedStatement.setString(2,"season");
            doCommand(preparedStatement);
            sender.sendMessage(LangLoader.title+LangLoader.noticeUpdateSeasonName.replaceAll("<seasonName>",seasonName));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 更新一个赛季时长
     *
     * @param continueDays  持续时间
     * @param commandSender 执行者
     */
    @Override
    public void updateSeasonContinueDays(int continueDays, CommandSender commandSender) {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_SEASON_CONTINUE_DAY.commandToString());
            preparedStatement.setInt(1,continueDays);
            preparedStatement.setString(2,"season");
            doCommand(preparedStatement);
            commandSender.sendMessage(LangLoader.title+LangLoader.noticeUpdateSeasonDays.replaceAll("<continueDays>",continueDays+""));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 新建一个赛季数据
     */
    @Override
    public void createSeasonData() {
        Connection connection=null;
        try {
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.CREATE_SEASON_DATA.commandToString());
            //insert into season_data (season_id,name,start_date,continue) values (?,?,?,?);
            preparedStatement.setString(1,"season");
            preparedStatement.setString(2,"§bUNKNOWN");
            preparedStatement.setDate(3,new java.sql.Date(System.currentTimeMillis()));
            preparedStatement.setInt(4,30);
            doCommand(preparedStatement);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§c已新建赛季数据...");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 获取所有的任务数据
     *
     * @return 任务数据集
     */
    @Override
    public List<Mission> getAllMissionData() {
        Connection connection=null;
        List<Mission> allMissionList=new ArrayList<>();
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.SELECT_ALL_MISSION.commandToString());
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                allMissionList.add(getMissionById(resultSet.getString("mission_id")));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return allMissionList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return allMissionList;
    }

    /**
     * 根据任务ID删除任务
     *
     * @param missionId     任务ID
     * @param commandSender 执行者
     */
    @Override
    public void deleteMissionById(String missionId, CommandSender commandSender) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.DELETE_MISSION_DATA_BY_MISSION_ID.commandToString());
            preparedStatement.setString(1,missionId);
            doCommand(preparedStatement);
            commandSender.sendMessage(LangLoader.title+"§a删除任务序号: "+missionId+" !");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 关闭数据连接
     */
    @Override
    public void closeDataBase() {
        try {
            RefreshPassCardPlayer.setKeepRunning(false);
            RefreshPlayerMission.setKeepRunning(false);
            RefreshSeasonData.setKeepRunning(false);
            RefreshTopPlayer.setKeepRunning(false);
            Thread.sleep(10);
            sqlConnectionPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新玩家每周数据
     *
     * @param player 玩家
     */
    @Override
    public void updatePlayerWeeklyDateAndMission(Player player) {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_WEEK_ID.commandToString());
            preparedStatement.setString(1,IdGetter.getWeekId());
            preparedStatement.setString(2,player.getUniqueId().toString());
            doCommand(preparedStatement);
            deletePlayerMissionData(player);
            createPlayerMissionData(player);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§e玩家 §a"+player.getName()+" §e每周数据已刷新!");
            player.sendMessage(LangLoader.title+LangLoader.noticeRefreshMissionData);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }

    }

    /**
     * 更新玩家每日日期
     *
     * @param player 玩家
     */
    @Override
    public void updatePlayerDailyDateAndMission(Player player) {
        Connection connection=null;
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try {
            connection = sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_REFRESH_DATE.commandToString());
            preparedStatement.setString(1,dateFormat.format(new Date()));
            preparedStatement.setString(2,player.getUniqueId().toString());
            doCommand(preparedStatement);
            deletePlayerMissionData(player);
            createPlayerMissionData(player);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§e玩家 §a"+player.getName()+" §e每日任务已刷新!");
            player.sendMessage(LangLoader.title+LangLoader.noticeRefreshMissionData);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
    }

    /**
     * 更新表数据结构 1.3.2
     */
    @Override
    public void updateSqlTableData() {
        Connection connection=null;
        try{
            connection=sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_VERSION_132.commandToString());
            preparedStatement.setString(1,"NewPlayer");
            try {
                preparedStatement.executeUpdate();
                preparedStatement.close();
                PassCard.logger.sendMessage(PassCard.pluginTitle + "§a数据库已更新...");
            }catch (Exception ignored){ }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(connection);
        }
    }

    /**
     * 更新玩家数据文件 -1.3.2
     *
     * @param player 玩家
     */
    @Override
    public void updatePlayerData(Player player) {
        /*Connection connection=null;
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try {
            connection = sqlConnectionPool.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement(SqlCommand.UPDATE_PLAYER_REFRESH_DATE.commandToString());
            preparedStatement.setString(1,dateFormat.format(new Date()));
            preparedStatement.setString(2,player.getUniqueId().toString());
            doCommand(preparedStatement);
            PassCard.logger.sendMessage(PassCard.pluginTitle + "§e玩家 §a" + player.getName() + " §e数据已更新至1.3.2版本!");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(connection);
        }*/
    }

    /**
     * 启动数据连接
     *
     * @return 是否成功
     */
    @Override
    public boolean enableDataBase() {
        try {
            enableSql();
            return true;
        }catch (Exception ex) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4数据错误:");
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭连接
     * @param connection 连接对象
     */
    private void closeConnection(Connection connection){
        try {
            if (connection != null) {
                connection.close();
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
