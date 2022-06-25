package com.bc.passcardpro.api;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.getter.IdGetter;
import com.bc.passcardpro.loader.AwardLoader;
import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.loader.LangLoader;
import com.bc.passcardpro.loader.yumLoader.MissionDataLoader;
import com.bc.passcardpro.loader.yumLoader.PlayerDataLoader;
import com.bc.passcardpro.loader.yumLoader.SeasonDataLoader;
import com.bc.passcardpro.loader.yumLoader.WeekDataLoader;
import com.bc.passcardpro.pojo.*;
import com.bc.passcardpro.utils.Math;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 15:09
 */
public class YmlManager implements PassCardAPI{
    private DecimalFormat df = new DecimalFormat("#.0");
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 更新玩家每周数据
     *
     * @param player 玩家
     */
    @Override
    public void updatePlayerWeeklyDateAndMission(Player player) {
        FileConfiguration playerData=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerData!=null) {
            try {
                playerData.set("Data.WeekId", IdGetter.getWeekId());
                playerData.save(PlayerDataLoader.getPlayerFile(player));
                deletePlayerMissionData(player);
                createPlayerMissionData(player);
                PassCard.logger.sendMessage(PassCard.pluginTitle + "§e玩家 §a" + player.getName() + " §e每周数据已刷新!");
                player.sendMessage(LangLoader.title + LangLoader.noticeRefreshMissionData);
            }catch (Exception ex){
                PassCard.logger.sendMessage(PassCard.pluginTitle+"§4玩家数据更新错误[11132]!");
                ex.printStackTrace();
            }
        }
    }

    /**
     * 更新玩家每日日期与任务数据
     *
     * @param player 玩家
     */
    @Override
    public void updatePlayerDailyDateAndMission(Player player) {
        FileConfiguration playerData= PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerData!=null){
            if(playerData.get("Data.Date")!=null) {
                try {
                    playerData.set("Data.Date", dateFormat.format(new Date()));
                    playerData.save(PlayerDataLoader.getPlayerFile(player));
                    deletePlayerMissionData(player);
                    createPlayerMissionData(player);
                    PassCard.logger.sendMessage(PassCard.pluginTitle+"§e玩家 §a"+player.getName()+" §e每日任务已刷新!");
                    player.sendMessage(LangLoader.title+LangLoader.noticeRefreshMissionData);
                }catch (Exception e){
                    PassCard.logger.sendMessage(PassCard.pluginTitle+"§4玩家数据更新错误[10132]!");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新表数据结构 1.3.2
     * @deprecated
     */
    @Override
    public void updateSqlTableData() { }

    /**
     * 更新玩家数据文件 -1.3.2
     *
     * @param player 玩家
     */
    @Override
    public void updatePlayerData(Player player) {
        FileConfiguration playerData= PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerData!=null){
            if(playerData.get("Data.Date")==null) {
                try {
                    playerData.set("Data.Date", dateFormat.format(new Date()));
                    playerData.save(PlayerDataLoader.getPlayerFile(player));
                    PassCard.logger.sendMessage(PassCard.pluginTitle + "§e玩家 §a" + player.getName() + " §e数据已更新至1.3.2版本!");
                }catch (Exception e){
                    PassCard.logger.sendMessage(PassCard.pluginTitle+"§4玩家数据更新错误：V1.3.2");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 启动数据连接
     *
     * @return 是否成功
     */
    @Override
    public boolean enableDataBase() {
        MissionDataLoader.loadMission();
        WeekDataLoader.loadWeek();
        SeasonDataLoader.loadSeason();
        File playerFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"players", "player.yml");
        if(!playerFile.exists()) {
            File fileParents = playerFile.getParentFile();
            //新建文件夹
            if (!fileParents.exists()) {
                if(fileParents.mkdirs()){
                    PassCard.logger.sendMessage(PassCard.pluginTitle+"§a新建玩家数据文档完成!");
                }
            }
        }
        return true;
    }

    /**
     * 获取玩家的任务列表
     *
     * @param player 玩家
     * @return 任务列表
     */
    @Override
    public List<Mission> getPlayerMissionList(Player player) {
        List<Mission> playerMissionList=new ArrayList<>();
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return playerMissionList;
        }
        for(int i=1;i<=6;i++){
            Mission mission=getMissionById(playerFile.getString("Mission."+i+".Id"));
            mission.setRate(playerFile.getString("Mission."+i+".Rate"));
            mission.setFinishTime(playerFile.getInt("Mission."+i+".Complete"));
            playerMissionList.add(mission);
        }
        return playerMissionList;
    }

    /**
     * 更新玩家的任务进度
     * 前进行判断：
     * 1 是否已经达到最大完成次数
     * 2 是否已经到达本周最大点数
     * 后进行更新：
     * 1 判断是否到达要求
     * -是：更新点数、完成次数
     *
     * @param player    玩家
     * @param missionId 任务ID
     * @param rate      新进度
     */
    @Override
    public void updatePlayerMissionRate(Player player, String missionId, String rate) {
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return ;
        }
        PassCardPlayer passCardPlayer=getPlayerData(player);
        double maxWeekPoint=passCardPlayer.isVip()? CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
        if(playerFile.getDouble("Data.WeekPoint")>=maxWeekPoint){
            return;
        }
        Mission mission=getMissionById(missionId);
        List<Mission> missionList=getPlayerMissionList(player);
        for (int i=0;i<missionList.size();i++) {
            if (missionList.get(i).getMissionId().equalsIgnoreCase(missionId)) {
                if (missionList.get(i).getFinishTime() >= mission.getMaxTime()) {
                    return;
                }
                playerFile.set("Mission."+(i+1)+".Rate",Double.parseDouble(rate));
                try {
                    playerFile.save(PlayerDataLoader.getPlayerFile(player));
                    String rateDouble=df.format(Double.parseDouble(rate));
                    DecimalFormat dft = new DecimalFormat("#.0");
                    String msg = LangLoader.noticeUpdateMission.replaceAll("<mission>", mission.getName())
                            .replaceAll("<missionRate>", rateDouble).replaceAll("<missionRequire>", dft.format(Double.parseDouble(mission.getRequire())));
                    if(CfgLoader.noticeMsg) {
                        player.sendMessage(LangLoader.title + msg);
                    }else if(CfgLoader.noticeTitle) {
                        player.sendTitle("", msg, 20, 0, 20);
                    }
                }catch (IOException ex){
                    PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
                    ex.printStackTrace();
                }
                //检查并更新玩家的数据状态
                checkAndUpdatePlayerData(player);
                break;
            }
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
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return ;
        }
        PassCardPlayer passCardPlayer=getPlayerData(player);
        double maxWeekPoint=passCardPlayer.isVip()? CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
        if(playerFile.getDouble("Data.WeekPoint")>=maxWeekPoint){
            return;
        }
        Mission mission=getMissionById(missionId);
        List<Mission> missionList=getPlayerRandomMissionData(player);
        for (int i=0;i<missionList.size();i++) {
            if (missionList.get(i).getMissionId().equalsIgnoreCase(missionId)) {
                if (missionList.get(i).getFinishTime() >= mission.getMaxTime()) {
                    return;
                }
                playerFile.set("RandomMission.Mission"+(i+1)+".Rate",Double.parseDouble(rate));
                try {
                    playerFile.save(PlayerDataLoader.getPlayerFile(player));
                    String rateDouble=df.format(Double.parseDouble(rate));
                    DecimalFormat dft = new DecimalFormat("#.0");
                    String msg = LangLoader.noticeUpdateMission.replaceAll("<mission>", mission.getName())
                            .replaceAll("<missionRate>", rateDouble).replaceAll("<missionRequire>", dft.format(Double.parseDouble(mission.getRequire())));
                    if(CfgLoader.noticeMsg) {
                        player.sendMessage(LangLoader.title + msg);
                    }else if(CfgLoader.noticeTitle) {
                        player.sendTitle("", msg, 20, 0, 20);
                    }
                }catch (IOException ex){
                    PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
                    ex.printStackTrace();
                }
                //检查并更新玩家的数据状态
                checkAndUpdatePlayerData(player);
                break;
            }
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
        Mission mission=null;
        if(MissionDataLoader.missionData.get(missionId)!=null){
            //String missionId,String type,String name,String require,double givenPoint,int maxTime,boolean random
            String type=MissionDataLoader.missionData.getString(missionId+".Type");
            String name= Objects.requireNonNull(MissionDataLoader.missionData.getString(missionId + ".Name")).replaceAll("&","§");
            String require=MissionDataLoader.missionData.getString(missionId+".Require");
            double givenPoint=MissionDataLoader.missionData.getDouble(missionId+".GivenPoint");
            int maxTime=MissionDataLoader.missionData.getInt(missionId+".MaxTime");
            String desc= Objects.requireNonNull(MissionDataLoader.missionData.getString(missionId + ".Desc")).replaceAll("&","§");
            mission=new Mission(missionId,type,name,require,givenPoint,maxTime,true);
            mission.setDesc(desc);
        }
        return mission;
    }

    /**
     * 根据玩家和任务ID获取具体的任务进度
     *
     * @param player 玩家
     * @return 玩家任务进度
     */
    @Override
    public Map<String, String> getPlayerMissionByPlayer(Player player) {
        //结构：  完成次数,当前进度,任务 ID
        Map<String,String> resultMap=new HashMap<>();
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return null;
        }
        for(int i=1;i<=6;i++){
            String data=playerFile.getInt("Mission."+i+".Complete")+","
                    +playerFile.getDouble("Mission."+i+".Rate")+","
                    +playerFile.getInt("Mission."+i+".Id");
            resultMap.put("mission_"+i,data);
        }
        return resultMap;
    }

    /**
     * 获取本周任务列表
     *
     * @param weekId 本周ID
     * @return 任务列表
     */
    @Override
    public List<Mission> getWeekMissionList(String weekId) {
        List<Mission> missionList=new ArrayList<>();
        if(WeekDataLoader.weekData.get(weekId)==null){
            return missionList;
        }
        for(int i=1;i<=6;i++){
            String missionId=WeekDataLoader.weekData.getString(weekId+".Mission."+i);
            missionList.add(getMissionById(missionId));
        }
        return missionList;
    }


    /**
     * 获取玩家当前数据
     *
     * @param player 玩家
     * @return PassCardPlayer实体类
     */
    @Override
    public synchronized PassCardPlayer getPlayerData(Player player) {
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return null;
        }
        //Player player, String weekId, int passCardLevel, double weekPoint, double point
        int passCardLevel=playerFile.getInt("Data.Level");
        double weekPoint=playerFile.getDouble("Data.WeekPoint");
        double point=playerFile.getDouble("Data.Point");
        boolean isVip=playerFile.getBoolean("Data.Vip");
        PassCardPlayer passCardPlayer=new PassCardPlayer(player,playerFile.getString("Data.WeekId"),passCardLevel,weekPoint,point);
        passCardPlayer.setVip(isVip);
        passCardPlayer.setGets(playerFile.getString("Data.Gets.Default"));
        passCardPlayer.setVipGets(playerFile.getString("Data.Gets.Vip"));
        passCardPlayer.setDate(playerFile.getString("Data.Date"));
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
        Player player=null;
        if(Bukkit.getOfflinePlayer(UUID)!= null) {
            player=Bukkit.getOfflinePlayer(UUID).getPlayer();
        }else if(Bukkit.getPlayer(UUID)!=null){
            player=Bukkit.getPlayer(UUID);
        }
        return getPlayerData(player);
    }

    /**
     * 检查并更新玩家任务数据等
     *
     * @param player 玩家
     */
    @Override
    public void checkAndUpdatePlayerData(Player player) {
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return;
        }
        List<Mission> missions=getPlayerMissionList(player);
        List<Mission> randomMission=getPlayerRandomMissionData(player);
        for (int i = 0; i < missions.size(); i++) {
            if (Double.parseDouble(missions.get(i).getRate()) >= Double.parseDouble(missions.get(i).getRequire())) {
                playerFile.set("Mission."+(i+1)+".Complete",playerFile.getInt("Mission."+(i+1)+".Complete")+1);
                playerFile.set("Mission."+(i+1)+".Rate",0);
                try {
                    playerFile.save(PlayerDataLoader.getPlayerFile(player));
                    double addPoint=missions.get(i).getGivenPoint();
                    updatePlayerPoint(getPlayerData(player),addPoint,true,true);
                    player.sendMessage(LangLoader.title+LangLoader.noticeMissionComplete.replaceAll("<mission>",missions.get(i).getName()));
                }catch (IOException ex){
                    PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
                    ex.printStackTrace();
                }
            }
        }
        for (int i = 0; i < randomMission.size(); i++) {
            if (Double.parseDouble(randomMission.get(i).getRate()) >= Double.parseDouble(randomMission.get(i).getRequire())) {
                playerFile.set("RandomMission.Mission"+(i+1)+".Complete",playerFile.getInt("RandomMission.Mission"+(i+1)+".Complete")+1);
                playerFile.set("RandomMission.Mission"+(i+1)+".Rate",0);
                try {
                    playerFile.save(PlayerDataLoader.getPlayerFile(player));
                    double addPoint=randomMission.get(i).getGivenPoint();
                    updatePlayerPoint(getPlayerData(player),addPoint,true,true);
                    player.sendMessage(LangLoader.title+LangLoader.noticeMissionComplete.replaceAll("<mission>",randomMission.get(i).getName()));
                }catch (IOException ex){
                    PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
                    ex.printStackTrace();
                }
            }
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
        TopPlayer topPlayer=null;
        File file=new File(PassCard.getPlugin().getDataFolder()+File.separator+"players");
        File[] files=file.listFiles();
        assert files != null;
        for(File playerDataFile:files){
            FileConfiguration playerData= YamlConfiguration.loadConfiguration(playerDataFile);
            if(!(Objects.requireNonNull(playerData.getString("UUID")).equalsIgnoreCase(uuid))){
                continue;
            }
            //String name,double point,int level
            topPlayer=new TopPlayer(playerData.getString("Name"),
                    playerData.getDouble("Data.Point"),
                    playerData.getInt("Data.Level"));
            break;
        }
        return topPlayer;
    }

    /**
     * 改变玩家点点数
     *
     * @param passCardPlayer 玩家
     * @param points         增加的点数 可为负数
     * @param checkLevel     是否检查等级
     * @param notice         是否提示
     */
    @Override
    public void updatePlayerPoint(PassCardPlayer passCardPlayer, double points, boolean checkLevel, boolean notice) {
        Player player=passCardPlayer.getPlayer();
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return;
        }
        if(playerWeekPointIsMax(passCardPlayer)){
            double playerWeekPoint=playerFile.getDouble("Data.WeekPoint");
            player.sendMessage(LangLoader.title+LangLoader.errorPointIsMax.replaceAll("<weekPoint>",playerWeekPoint+"")
                    .replaceAll("<maxWeekPoint>",passCardPlayer.isVip()?CfgLoader.vipWeekMaxPoint+"":CfgLoader.weekMaxPoint+""));
            return;
        }
        double point=playerFile.getDouble("Data.Point");
        point+=points;
        if(point>0) {
            playerFile.set("Data.Point", point);
        }else{
            playerFile.set("Data.Point", 0);
        }
        try{
            playerFile.save(PlayerDataLoader.getPlayerFile(player));
            if(checkLevel){
                updatePlayerLevel(passCardPlayer,points>0);
            }
            String changeType=points>0?"+":"-";
            if(notice) {
                player.sendMessage(LangLoader.title + LangLoader.noticeUpdatePoint.replaceAll("<changeType>", changeType)
                        .replaceAll("<point>", points + ""));
            }
        }catch (IOException ex){
            PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
            ex.printStackTrace();
        }
        if(checkLevel){
            updatePlayerWeekPoint(passCardPlayer,points);
        }
    }

    /**
     * 检查玩家等级并更新
     *
     * @param passCardPlayer 玩家
     * @param isAdd          是否为增加等级
     */
    @Override
    public void updatePlayerLevel(PassCardPlayer passCardPlayer, boolean isAdd) {
        Player player=passCardPlayer.getPlayer();
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return;
        }
        int playerLevel=isAdd?passCardPlayer.getPassCardLevel():0,addLevel=1;
        double playerNowPoint=passCardPlayer.getPoint();
        while (AwardLoader.award.get((playerLevel + addLevel) + "") != null && playerNowPoint>0) {
            if (playerNowPoint >= AwardLoader.award.getDouble((playerLevel + addLevel) + ".Point")) {
                //增加玩家等级
                int nowLevel=(playerLevel + addLevel);
                playerFile.set("Data.Level",nowLevel);
                try {
                    playerFile.save(PlayerDataLoader.getPlayerFile(player));
                    player.sendMessage(LangLoader.title+LangLoader.noticeLevelUp
                            .replaceAll("<oldLevel>",passCardPlayer.getPassCardLevel()+"")
                            .replaceAll("<newLevel>",getPlayerData(player).getPassCardLevel()+""));
                }catch (IOException ex){
                    PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
                    ex.printStackTrace();
                }
                //更新玩家点数数据.
                playerNowPoint-=AwardLoader.award.getDouble((playerLevel + addLevel) + ".Point");
                updatePlayerPoint(getPlayerData(player), -AwardLoader.award.getDouble((playerLevel + addLevel) + ".Point"),false,false);
            }else{
                break;
            }
            addLevel++;
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
        double playerWeenPoint=passCardPlayer.getWeekPoint();
        double maxWeekPoint=passCardPlayer.isVip()?CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
        return playerWeenPoint >= maxWeekPoint;
    }

    /**
     * 更新玩家每周点数
     *
     * @param passCardPlayer 玩家
     * @param points         点数 可为负数
     */
    @Override
    public void updatePlayerWeekPoint(PassCardPlayer passCardPlayer, double points) {
        if(points<0){
            return;
        }
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(passCardPlayer.getPlayer());
        if(playerFile==null){
            return;
        }
        playerFile.set("Data.WeekPoint",passCardPlayer.getWeekPoint()+points);
        try {
            playerFile.save(PlayerDataLoader.getPlayerFile(passCardPlayer.getPlayer()));
        }catch (IOException ex){
            PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
            ex.printStackTrace();
        }
    }

    /**
     * 新建玩家数据
     *
     * @param player 玩家
     */
    @Override
    public void createPlayerData(Player player) {
        PlayerDataLoader.createNewPlayerFile(player);
        PlayerDataLoader.createNewPlayerMissionData(player);
        PassCard.logger.sendMessage(PassCard.pluginTitle+"§a成功为 §e"+player.getName()+" §a[§b"+player.getUniqueId().toString()+"§a] 创建数据!");
    }

    /**
     * 新建玩家任务数据
     *
     * @param player 玩家
     */
    @Override
    public void createPlayerMissionData(Player player) {
        PlayerDataLoader.createNewPlayerMissionData(player);
    }

    /**
     * 获取一条随机任务
     *
     * @param needRandom 是否需要random项为true
     * @return 随机任务
     */
    @Override
    public Mission getRandomMission(boolean needRandom) {
        List<Mission> allMission=new ArrayList<>();
        for(String key:MissionDataLoader.missionData.getKeys(false)){
            if(!MissionDataLoader.missionData.getBoolean(key+".Random")&&needRandom){
                continue;
            }
            //String missionId,String type,String name,String require,double givenPoint,int maxTime,boolean random
            String type=MissionDataLoader.missionData.getString(key+".Type");
            String name= Objects.requireNonNull(MissionDataLoader.missionData.getString(key + ".Name")).replaceAll("&","§");
            String require=MissionDataLoader.missionData.getString(key+".Require");
            double givenPoint=MissionDataLoader.missionData.getDouble(key+".GivenPoint");
            int maxTime=MissionDataLoader.missionData.getInt(key+".MaxTime");
            String desc= Objects.requireNonNull(MissionDataLoader.missionData.getString(key + ".Desc")).replaceAll("&","§");
            Mission mission=new Mission(key,type,name,require,givenPoint,maxTime,true);
            mission.setDesc(desc);
            allMission.add(mission);
        }
        Random random = new Random();
        return allMission.get(random.nextInt(allMission.size()));
    }

    /**
     * 新建一条任务数据
     *
     * @param mission 任务
     * @return 是否成功
     */
    @Override
    public boolean createNewMissionData(Mission mission) {
        if(MissionDataLoader.missionData.get(mission.getMissionId())!=null){
            return false;
        }
        String missionId=mission.getMissionId();
        MissionDataLoader.missionData.set(missionId+".Name",mission.getName().replaceAll("&","§"));
        MissionDataLoader.missionData.set(missionId+".Type",mission.getType());
        MissionDataLoader.missionData.set(missionId+".Require",mission.getRequire());
        MissionDataLoader.missionData.set(missionId+".GivenPoint",mission.getGivenPoint());
        MissionDataLoader.missionData.set(missionId+".MaxTime",mission.getMaxTime());
        MissionDataLoader.missionData.set(missionId+".Desc",mission.getDesc()==null?"":mission.getDesc().replaceAll("&","§"));
        MissionDataLoader.missionData.set(missionId+".Random",mission.isRandom());
        MissionDataLoader.save();
        return true;
    }

    /**
     * 获取玩家的任务进度
     *
     * @param player 玩家
     * @return 任务进度列表
     */
    @Override
    public MissionPlayer getPlayerMissionData(Player player) {
        ConcurrentHashMap<String,Mission> missionMap=new ConcurrentHashMap<>();
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return null;
        }
        for(int i=1;i<7;i++){
            String missionId=playerFile.getString("Mission."+i+".Id");
            int finishTime=playerFile.getInt("Mission."+i+".Complete");
            double rate=playerFile.getDouble("Mission."+i+".Rate");
            Mission mission=getMissionById(missionId);
            mission.setFinishTime(finishTime);
            mission.setRate(rate+"");
            missionMap.put("mission_"+i,mission);
        }
        return new MissionPlayer(player,getPlayerData(player),missionMap);
    }

    /**
     * 获取玩家到达下一个等级需要多少点数
     *
     * @param player 玩家
     * @return 点数
     */
    @Override
    public double getPlayerNextLevelNeedPoint(Player player) {
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
    }

    /**
     * 获取前10玩家排名
     *
     * @return 排行Map
     */
    @Override
    public ConcurrentHashMap<Integer, TopPlayer> getTop10Player() {
        ConcurrentHashMap<Integer, TopPlayer> topPlayerMap= new ConcurrentHashMap<>();
        HashMap<TopPlayer,Double> sortMap=new HashMap<>();
        File file=new File(PassCard.getPlugin().getDataFolder()+File.separator+"players");
        File[] files=file.listFiles();
        assert files != null;
        for(File playerDataFile:files){
            FileConfiguration playerData= YamlConfiguration.loadConfiguration(playerDataFile);
            //String name,double point,int level
            TopPlayer topPlayer=new TopPlayer(playerData.getString("Name")
                    ,playerData.getDouble("Data.Point"),playerData.getInt("Data.Level"));
            sortMap.put(topPlayer, Math.getLevelPoint(topPlayer.getLevel())+topPlayer.getPoint());
        }
        List<HashMap.Entry<TopPlayer,Double>> sortedMap= new ArrayList<>(sortMap.entrySet());
        //排序
        Collections.sort(sortedMap, (o1, o2) -> {
            return (int)(o2.getValue() - o1.getValue()); //这里是从大到小进行排序，如果需从小到大进行排序则将o1和o2互换即可
        });
        int topLevel=1;
        for (HashMap.Entry<TopPlayer,Double> entry : sortedMap) {
            topPlayerMap.put(topLevel++,entry.getKey());
            if(topLevel>=10){
                break;
            }
        }
        return topPlayerMap;
    }

    /**
     * 重置每周任务
     */
    @Override
    public void resetWeekMission() {
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
        String weekId=IdGetter.getWeekId();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        WeekDataLoader.weekData.set(weekId+".StartData",df.format(new Date()));
        for(int i=1;i<=6;i++){
            WeekDataLoader.weekData.set(weekId+".Mission."+i,randomMissionList.get((i-1)).getMissionId());
        }
        WeekDataLoader.save();
        PassCard.logger.sendMessage(PassCard.pluginTitle+"§4每周任务内容重置: "+IdGetter.getWeekId()+" !");
    }

    /**
     * 删除玩家数据
     *
     * @param player 玩家
     */
    @Override
    public void deletePlayerMissionData(Player player) {
        File playerFile=PlayerDataLoader.getPlayerFile(player);
        if(!playerFile.exists()){
            PassCard.logger.sendMessage(PassCard.pluginTitle+" §4玩家"+player.getName()+"数据不存在!");
        }else{
            try {
                FileConfiguration playerData = PlayerDataLoader.getPlayerDataFileConfiguration(player);
                if (playerData != null) {
                    playerData.set("RandomMission", null);
                    playerData.set("Mission", null);
                    playerData.save(playerFile);
                }
            }catch (Exception ex){
                PassCard.logger.sendMessage(PassCard.pluginTitle+" §4玩家"+player.getName()+"数据删除失败!");
                ex.printStackTrace();
            }
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
        List<Mission> playerRandomMission=new ArrayList<>();
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return playerRandomMission;
        }
        for(int i=1;i<=3;i++){
            Mission mission=getMissionById(playerFile.getString("RandomMission.Mission"+i+".Id"));
            mission.setRate(playerFile.getDouble("RandomMission.Mission"+i+".Rate")+"");
            mission.setFinishTime(playerFile.getInt("RandomMission.Mission"+i+".Complete"));
            playerRandomMission.add(mission);
        }
        return playerRandomMission;
    }

    /**
     * 更新玩家数据库数据
     *
     * @param passCardPlayer 玩家
     * @param gets 获取
     * @param vipGets vip获取
     */
    @Override
    public void updatePassCardPlayerGets(PassCardPlayer passCardPlayer, int gets, int vipGets) {
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(passCardPlayer.getPlayer());
        if(playerFile==null){
            return ;
        }
        playerFile.set("Data.Gets.Default",passCardPlayer.getGets());
        playerFile.set("Data.Gets.Vip",passCardPlayer.getVipGets());
        try{
            playerFile.save(PlayerDataLoader.getPlayerFile(passCardPlayer.getPlayer()));
            passCardPlayer.getPlayer().sendMessage(LangLoader.title+
                    LangLoader.noticeGetAward
                            .replaceAll("<countGets>",(gets+vipGets)+"")
                            .replaceAll("<gets>",gets+"")
                            .replaceAll("<vipGets>",vipGets+""));
        }catch (IOException ex){
            PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
            ex.printStackTrace();
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
    public void updateMissionDesc(String missionId, String desc, Player player) {
        if(MissionDataLoader.missionData.get(missionId)==null){
            player.sendMessage(LangLoader.title+LangLoader.errorMissionNotExist);
            return;
        }
        MissionDataLoader.missionData.set(missionId+".Desc",desc.replaceAll("&","§"));
        MissionDataLoader.save();
        Mission mission=getMissionById(missionId);
        player.sendMessage(LangLoader.title+LangLoader.noticeUpdateDesc
                .replaceAll("<mission>",mission.getName()).replaceAll("<desc>",desc));
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
        FileConfiguration playerFile=PlayerDataLoader.getPlayerDataFileConfiguration(player);
        if(playerFile==null){
            return ;
        }
        playerFile.set("Data.Vip",true);
        try{
            playerFile.save(PlayerDataLoader.getPlayerFile(player));
            sender.sendMessage(LangLoader.title+LangLoader.noticeUpdateVip
                    .replaceAll("<player>",player.getName()).replaceAll("<vip>",setVip?CfgLoader.vipTitle:CfgLoader.defaultTitle));
        }catch (IOException ex){
            PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据更新失败！");
            ex.printStackTrace();
        }
    }

    /**
     * 获取当前赛季数据
     *
     * @return 当前赛季
     */
    @Override
    public Season getNowSeasonData() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int continueDays=SeasonDataLoader.seasonData.getInt("Continue");
        int leftDays=continueDays-
                com.bc.passcardpro.utils.Time.daysBetween(SeasonDataLoader.seasonData.getString("StartDate")
                        ,df.format(new Date()));
        try {
            Date seasonStartDate=df.parse(Objects.requireNonNull(SeasonDataLoader.seasonData.getString("StartDate")));
            return new Season("season", Objects.requireNonNull(SeasonDataLoader.seasonData.getString("Name")).replaceAll("&","§")
                    ,seasonStartDate,continueDays,leftDays);
        } catch (ParseException e) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4转换错误: 日期不合法!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 重置赛季数据
     */
    @Override
    public void resetPlayerSeasonData() {
        File file=new File(PassCard.getPlugin().getDataFolder()+File.separator+"players");
        File[] files=file.listFiles();
        assert files != null;
        for(File playerDataFile:files){
            if(playerDataFile.isFile()){
                playerDataFile.delete();
            }
        }
        //删除每周任务文件 并新建
        File weekFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"data", "Week.yml");
        if(weekFile.isFile()){
            if(weekFile.delete()){
                WeekDataLoader.loadWeek();
            }
        }
    }

    /**
     * 新建一个赛季
     *
     * @param season 赛季
     */
    @Override
    public void updateNewSeason(Season season) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SeasonDataLoader.seasonData.set("Name",season.getSeasonName().replaceAll("&","§"));
        SeasonDataLoader.seasonData.set("StartDate",df.format(season.getStartDate()));
        SeasonDataLoader.seasonData.set("Continue",season.getContinueDays());
        SeasonDataLoader.save();
        PassCard.logger.sendMessage(PassCard.pluginTitle+"§4赛季数据已全部重置...");
    }

    /**
     * 更新一个赛季名称
     *
     * @param seasonName 赛季名称
     * @param sender     执行者
     */
    @Override
    public void updateSeasonName(String seasonName, CommandSender sender) {
        SeasonDataLoader.seasonData.set("Name",seasonName.replaceAll("&","§"));
        SeasonDataLoader.save();
        sender.sendMessage(LangLoader.title+LangLoader.noticeUpdateSeasonName.replaceAll("<seasonName>",seasonName));
    }

    /**
     * 更新一个赛季时长
     *
     * @param continueDays  持续时间
     * @param commandSender 执行者
     */
    @Override
    public void updateSeasonContinueDays(int continueDays, CommandSender commandSender) {
        SeasonDataLoader.seasonData.set("Continue",continueDays);
        SeasonDataLoader.save();
        commandSender.sendMessage(LangLoader.title+LangLoader.noticeUpdateSeasonDays.replaceAll("<continueDays>",continueDays+""));
    }

    /**
     * 新建一个赛季数据
     */
    @Override
    public void createSeasonData() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SeasonDataLoader.seasonData.set("Name","§bUNKNOWN");
        SeasonDataLoader.seasonData.set("StartDate",df.format(new Date()));
        SeasonDataLoader.seasonData.set("Continue",30);
        SeasonDataLoader.save();
        PassCard.logger.sendMessage(PassCard.pluginTitle+"§c已新建赛季数据...");
    }

    /**
     * 获取所有的任务数据
     *
     * @return 任务数据集
     */
    @Override
    public List<Mission> getAllMissionData() {
        List<Mission> allMissionList=new ArrayList<>();
        for(String key:MissionDataLoader.missionData.getKeys(false)){
            allMissionList.add(getMissionById(key));
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
        MissionDataLoader.missionData.set(missionId,null);
        MissionDataLoader.save();
        commandSender.sendMessage(LangLoader.title+"§a删除任务序号: "+missionId+" !");
    }

    /**
     * 关闭数据连接
     */
    @Override
    public void closeDataBase() {
        //nothing
    }
}
