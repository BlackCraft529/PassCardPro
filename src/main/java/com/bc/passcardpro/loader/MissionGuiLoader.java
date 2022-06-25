package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/7/6 22:47
 */
public class MissionGuiLoader {
    public static FileConfiguration missionGui;
    private static File guiFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"gui", "missionGui.yml");
    public static void loadGui(){
        if(!guiFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Gui]missionGui.yml，正在创建...");
            PassCard.getPlugin().saveResource("gui"+File.separator+"missionGui.yml",false);
            missionGui = YamlConfiguration.loadConfiguration(guiFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Gui]missionGui.yml创建完成!");
        }else {
            missionGui = YamlConfiguration.loadConfiguration(guiFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Gui]missionGui.yml已找到!");
        }
    }
}
