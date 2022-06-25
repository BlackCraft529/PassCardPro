package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/7/7 11:46
 */
public class MenuGuiLoader {
    public static FileConfiguration menuGui;
    private static File guiFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"gui", "menuGui.yml");
    public static void loadGui(){
        if(!guiFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到[Gui]menuGui.yml，正在创建...");
            PassCard.getPlugin().saveResource("gui"+File.separator+"menuGui.yml",false);
            menuGui = YamlConfiguration.loadConfiguration(guiFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件[Gui]menuGui.yml创建完成!");
        }else {
            menuGui = YamlConfiguration.loadConfiguration(guiFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件[Gui]menuGui.yml已找到!");
        }
    }
}
