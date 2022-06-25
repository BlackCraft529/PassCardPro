package com.bc.passcardpro.loader.yumLoader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/7/16 16:51
 */
public class MissionDataLoader {
    public static FileConfiguration missionData;
    private static File missionFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"data", "Missions.yml");
    public static void loadMission(){
        if(!missionFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Data]Missions.yml，正在创建...");
            PassCard.getPlugin().saveResource("data"+File.separator+"Missions.yml",false);
            missionData = YamlConfiguration.loadConfiguration(missionFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Data]Missions.yml创建完成!");
        }else {
            missionData = YamlConfiguration.loadConfiguration(missionFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Data]Missions.yml已找到!");
        }
    }
    public static void save(){
        try {
            missionData.save(missionFile);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
