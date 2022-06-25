package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/6/8 21:28
 */
public class AwardItemLoader {
    public static FileConfiguration itemData;
    private static File itemFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"award", "item.yml");
    public static void loadItem(){
        if(!itemFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Award]item.yml，正在创建...");
            PassCard.getPlugin().saveResource("award"+File.separator+"item.yml",false);
            itemData = YamlConfiguration.loadConfiguration(itemFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Award]item.yml创建完成!");
        }else {
            itemData = YamlConfiguration.loadConfiguration(itemFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Award]item.yml已找到!");
        }
    }
    public static void save(){
        try {
            itemData.save(itemFile);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
