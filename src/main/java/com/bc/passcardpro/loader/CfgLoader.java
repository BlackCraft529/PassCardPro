package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Sql;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.Objects;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 11:06
 */
public class CfgLoader {
    public static FileConfiguration cfg;
    public static Sql sql;
    public static String langFileName="zh_CN";
    public static boolean useSql=false,missionComplete=false,noticeTitle=true,noticeMsg=false,enableDoubleJump=true;
    public static double weekMaxPoint=1000,refreshTime=3000,vipWeekMaxPoint=1500;
    public static String guiMissionTitle="",guiMissionNoticeFinish="",guiMissionNoticeContinue="";
    public static String guiMenuTitle="",guiAwardTitle="";
    public static String topDesc="",vipTitle="",defaultTitle="";
    public static String permissionFly="essentials.fly",refreshType="weekly";
    public static int hookType=2;
    public static void loadCfg(){
        if(!new File(PassCard.getPlugin().getDataFolder(), "config.yml").exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到config.yml，正在创建...");
            PassCard.getPlugin().saveDefaultConfig();
            cfg= PassCard.getPlugin().getConfig();
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件config.yml创建完成!");
        }else {
            cfg =  YamlConfiguration.loadConfiguration(new File(PassCard.getPlugin().getDataFolder(), "config.yml"));
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件config.yml已找到!");
        }
        reLoadCfg();
    }
    private static void reLoadCfg() {
        String databaseName = cfg.getString("MySql.DatabaseName");
        String userName = cfg.getString("MySql.UserName");
        String password = cfg.getString("MySql.Password");
        String point = cfg.getString("MySql.Port");
        String ip = cfg.getString("MySql.Ip");
        sql = new Sql(databaseName, password, userName, point, ip);

        useSql=cfg.getBoolean("MySql.UseSql");
        weekMaxPoint=cfg.getDouble("WeekMaxPoint.Default");
        vipWeekMaxPoint=cfg.getDouble("WeekMaxPoint.Vip");
        missionComplete=cfg.getBoolean("Mission.Complete");
        refreshTime=cfg.getDouble("Refresh")*1000;

        guiMissionTitle= Objects.requireNonNull(cfg.getString("Gui.Mission.Title")).replaceAll("&","§");
        guiMissionNoticeFinish= Objects.requireNonNull(cfg.getString("Gui.Mission.Finish")).replaceAll("&","§");
        guiMissionNoticeContinue= Objects.requireNonNull(cfg.getString("Gui.Mission.Continue")).replaceAll("&","§");
        guiMenuTitle= Objects.requireNonNull(cfg.getString("Gui.Menu.Title")).replaceAll("&","§");
        guiAwardTitle= Objects.requireNonNull(cfg.getString("Gui.Award.Title")).replaceAll("&","§");
        topDesc= Objects.requireNonNull(cfg.getString("Gui.Award.TopDesc")).replaceAll("&","§");
        vipTitle= Objects.requireNonNull(cfg.getString("Gui.Menu.Vip")).replaceAll("&","§");
        defaultTitle= Objects.requireNonNull(cfg.getString("Gui.Menu.Default")).replaceAll("&","§");
        noticeTitle=cfg.getBoolean("Mission.Notice.Title");
        noticeMsg=cfg.getBoolean("Mission.Notice.Msg");
        permissionFly=cfg.getString("Permission.Fly");
        hookType=cfg.getInt("Papi");
        langFileName=cfg.getString("Language");
        enableDoubleJump=cfg.getBoolean("DoubleJump");
        refreshType=cfg.getString("Mission.Refresh");
    }
}
