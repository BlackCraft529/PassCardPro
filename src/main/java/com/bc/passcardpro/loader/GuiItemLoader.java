package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/6/8 20:36
 */
public class GuiItemLoader {
    public static FileConfiguration itemData;
    private static File itemFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"gui", "item.yml");
    public static void loadItem(){
        if(!itemFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Gui]item.yml，正在创建...");
            PassCard.getPlugin().saveResource("gui"+File.separator+"item.yml",false);
            itemData = YamlConfiguration.loadConfiguration(itemFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Gui]item.yml创建完成!");
        }else {
            itemData = YamlConfiguration.loadConfiguration(itemFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Gui]item.yml已找到!");
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
