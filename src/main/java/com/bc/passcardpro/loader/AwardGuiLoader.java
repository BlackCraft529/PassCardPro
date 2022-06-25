package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/7/7 22:47
 */
public class AwardGuiLoader {
    public static FileConfiguration gui;
    private static File guiFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"gui", "awardGui.yml");
    public static void loadGui(){
        if(!guiFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Gui]awardGui.yml，正在创建...");
            PassCard.getPlugin().saveResource("gui"+File.separator+"awardGui.yml",false);
            gui = YamlConfiguration.loadConfiguration(guiFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Gui]awardGui.yml创建完成!");
        }else {
            gui = YamlConfiguration.loadConfiguration(guiFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Gui]awardGui.yml已找到!");
        }
    }
}
