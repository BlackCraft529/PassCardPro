package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/6/8 21:41
 */
public class AwardLoader {
    public static FileConfiguration award;
    private static File awardFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"award", "awardLevel.yml");
    public static void loadAward(){
        if(!awardFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Award]awardLevel.yml，正在创建...");
            PassCard.getPlugin().saveResource("award"+File.separator+"awardLevel.yml",false);
            award = YamlConfiguration.loadConfiguration(awardFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Award]awardLevel.yml创建完成!");
        }else {
            award = YamlConfiguration.loadConfiguration(awardFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Award]awardLevel.yml已找到!");
        }
    }
}
