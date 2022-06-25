package com.bc.passcardpro.loader.yumLoader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/7/16 16:56
 */
public class WeekDataLoader {
    public static FileConfiguration weekData;
    private static File weekFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"data", "Week.yml");
    public static void loadWeek(){
        if(!weekFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Data]Week.yml，正在创建...");
            PassCard.getPlugin().saveResource("data"+File.separator+"Week.yml",false);
            weekData = YamlConfiguration.loadConfiguration(weekFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Data]Week.yml创建完成!");
        }else {
            weekData = YamlConfiguration.loadConfiguration(weekFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Data]Week.yml已找到!");
        }
    }
    public static void save(){
        try {
            weekData.save(weekFile);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
