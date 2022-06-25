package com.bc.passcardpro.loader.yumLoader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Luckily_Baby
 * @date 2020/7/16 16:54
 */
public class SeasonDataLoader {
    public static FileConfiguration seasonData;
    private static File seasonFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"data", "Season.yml");
    public static void loadSeason(){
        if(!seasonFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Data]Season.yml，正在创建...");
            PassCard.getPlugin().saveResource("data"+File.separator+"Season.yml",false);
            seasonData = YamlConfiguration.loadConfiguration(seasonFile);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            seasonData.set("StartDate",df.format(new Date()));
            save();
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Data]Season.yml创建完成!");
        }else {
            seasonData = YamlConfiguration.loadConfiguration(seasonFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Data]Season.yml已找到!");
        }
    }
    public static void save(){
        try {
            seasonData.save(seasonFile);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
