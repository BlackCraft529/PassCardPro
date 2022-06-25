package com.bc.passcardpro.loader.yumLoader;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.getter.IdGetter;
import com.bc.passcardpro.loader.LangLoader;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/16 17:20
 */
public class PlayerDataLoader {
    /**
     * 获取玩家数据文件连接
     *
     * @param player 玩家
     * @return 文件连接
     */
    public static FileConfiguration getPlayerDataFileConfiguration(Player player){
        String playerName=player.getName();
        File playerFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"players", playerName+".yml");
        return playerFile.exists() ? YamlConfiguration.loadConfiguration(playerFile):null;
    }

    /**
     * 获取玩家文件
     *
     * @param player 玩家
     * @return 文件File
     */
    public static File getPlayerFile(Player player){
        String playerName=player.getName();
        return new File(PassCard.getPlugin().getDataFolder()+File.separator+"players", playerName+".yml");
    }

    /**
     * 新建玩家任务数据
     *
     * @param player 玩家
     */
    public static void createNewPlayerMissionData(Player player){
        String playerName=player.getName();
        File playerFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"players", playerName+".yml");
        if(playerFile.exists()) {
            try {
                FileConfiguration playerData=YamlConfiguration.loadConfiguration(playerFile);
                List<Mission> randomMission = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Mission mission = PassCard.passCardAPI.getRandomMission(true);
                    for (int j = 0; j < randomMission.size(); j++) {
                        if (randomMission.get(j).getMissionId().equalsIgnoreCase(mission.getMissionId())) {
                            mission = PassCard.passCardAPI.getRandomMission(true);
                            j = -1;
                        }
                    }
                    randomMission.add(mission);
                }
                for (int i = 0; i < randomMission.size(); i++) {
                    playerData.set("RandomMission.Mission" + (i + 1) + ".Id", randomMission.get(i).getMissionId());
                    playerData.set("RandomMission.Mission" + (i + 1) + ".Rate", 0);
                    playerData.set("RandomMission.Mission" + (i + 1) + ".Complete", 0);
                }
                List<Mission> weekMission = PassCard.passCardAPI.getWeekMissionList(IdGetter.getWeekId());
                for(int i=1;i<=6;i++){
                    playerData.set("Mission."+i+".Id",weekMission.get(i-1).getMissionId());
                    playerData.set("Mission."+i+".Complete",0);
                    playerData.set("Mission."+i+".Rate",0);
                }
                playerData.save(playerFile);
            } catch (IOException ex) {
                PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据新建失败！");
                ex.printStackTrace();
            }
        }
    }

    /**
     * 新建玩家数据文件
     *
     * @param player 玩家
     */
    public static void createNewPlayerFile(Player player) {
        String playerName=player.getName();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        File playerFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"players", playerName+".yml");
        if(!playerFile.exists()){
            File fileParents = playerFile.getParentFile();
            //新建文件夹
            if (!fileParents.exists()) {
                fileParents.mkdirs();
            }
            try {
                playerFile.createNewFile();
                FileConfiguration playerData=YamlConfiguration.loadConfiguration(playerFile);
                playerData.set("Name",playerName);
                playerData.set("Data.Level",0);
                playerData.set("Data.Date",dateFormat.format(new Date()));
                playerData.set("Data.WeekPoint",0);
                playerData.set("Data.Point",0);
                playerData.set("Data.WeekId", IdGetter.getWeekId());
                playerData.set("Data.Vip",false);
                playerData.set("Data.Gets.Default","0,");
                playerData.set("Data.Gets.Vip","0,");
                playerData.set("UUID",player.getUniqueId().toString());
                playerData.save(playerFile);
            }catch (IOException ex){
                PassCard.logger.sendMessage(LangLoader.title+"§4Error文件错误: 玩家数据新建失败！");
                ex.printStackTrace();
            }

        }
    }
}
