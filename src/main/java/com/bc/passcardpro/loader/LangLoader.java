package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.Objects;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 16:26
 */
public class LangLoader {
    public static String title="";
    public static String noticeUpdateMission="",noticeMissionComplete="",noticeUpdatePoint=""
            ,noticeLevelUp="",noticeAddMission="",noticeWait="",noticeGetAward=""
            ,noticeUpdateDesc="",noticeUpdateVip="",noticeUpdateSeasonName=""
            ,noticeUpdateSeasonDays="",noticeOperateSuccessful="",noticeRefreshMissionData="";
    public static String errorMissionNotExist="",errorPointIsMax="",errorAddMission=""
            ,errorIsFirstAward="",errorIsLastAward="",errorNoAward="",errorNoPermission=""
            ,errorPlayerNotExist="";
    public static String titleShowCreateNewData="";
    private static FileConfiguration lang;
    private static File langFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"language", CfgLoader.langFileName+".yml");
    public static void loadLang(){
        outPutAllLang();
        if(!langFile.exists()) {
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§4未找到"+CfgLoader.langFileName+".yml，正在创建...");
            PassCard.getPlugin().saveResource("language"+File.separator+CfgLoader.langFileName+".yml",false);
            lang = YamlConfiguration.loadConfiguration(langFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§b文件"+CfgLoader.langFileName+".yml创建完成!");
        }else {
            lang = YamlConfiguration.loadConfiguration(langFile);
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§a文件"+CfgLoader.langFileName+".yml已找到!");
        }
        reLoadLang();
    }
    private static void reLoadLang(){
        title= Objects.requireNonNull(lang.getString("Title")).replaceAll("&","§");

        noticeUpdateMission= Objects.requireNonNull(lang.getString("Notice.updateMission")).replaceAll("&","§");
        noticeMissionComplete= Objects.requireNonNull(lang.getString("Notice.missionComplete")).replaceAll("&","§");
        noticeUpdatePoint= Objects.requireNonNull(lang.getString("Notice.updatePoint")).replaceAll("&","§");
        noticeLevelUp= Objects.requireNonNull(lang.getString("Notice.levelUp")).replaceAll("&","§");
        noticeAddMission= Objects.requireNonNull(lang.getString("Notice.addMission")).replaceAll("&","§");
        noticeWait= Objects.requireNonNull(lang.getString("Notice.wait")).replaceAll("&","§");
        noticeGetAward= Objects.requireNonNull(lang.getString("Notice.getAward")).replaceAll("&","§");
        noticeUpdateDesc= Objects.requireNonNull(lang.getString("Notice.updateDesc")).replaceAll("&","§");
        noticeUpdateVip= Objects.requireNonNull(lang.getString("Notice.updateVip")).replaceAll("&","§");
        noticeUpdateSeasonName= Objects.requireNonNull(lang.getString("Notice.updateSeasonName")).replaceAll("&","§");
        noticeUpdateSeasonDays= Objects.requireNonNull(lang.getString("Notice.updateSeasonDays")).replaceAll("&","§");
        noticeOperateSuccessful= Objects.requireNonNull(lang.getString("Notice.operateSuccessful")).replaceAll("&","§");
        noticeRefreshMissionData= Objects.requireNonNull(lang.getString("Notice.RefreshMissionData")).replaceAll("&","§");

        errorMissionNotExist= Objects.requireNonNull(lang.getString("Error.missionNotExist")).replaceAll("&","§");
        errorPointIsMax= Objects.requireNonNull(lang.getString("Error.isMaxPoint")).replaceAll("&","§");
        errorAddMission= Objects.requireNonNull(lang.getString("Error.addMission")).replaceAll("&","§");
        errorIsFirstAward= Objects.requireNonNull(lang.getString("Error.isFirstAward")).replaceAll("&","§");
        errorIsLastAward= Objects.requireNonNull(lang.getString("Error.isLastAward")).replaceAll("&","§");
        errorNoAward= Objects.requireNonNull(lang.getString("Error.noAward")).replaceAll("&","§");
        errorNoPermission= Objects.requireNonNull(lang.getString("Error.noPermission")).replaceAll("&","§");
        errorPlayerNotExist= Objects.requireNonNull(lang.getString("Error.playerNotExist")).replaceAll("&","§");

        titleShowCreateNewData= Objects.requireNonNull(lang.getString("TitleShow.createNewData")).replaceAll("&","§");
    }
    public static void outPutAllLang(){
        File zhCnFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"language", "zh_CN.yml");
        File zhTwFile =new File(PassCard.getPlugin().getDataFolder()+File.separator+"language", "zh_TW.yml");
        if(!zhCnFile.exists()){
            PassCard.getPlugin().saveResource("language"+File.separator+"zh_CN.yml",false);
        }
        if(!zhTwFile.exists()){
            PassCard.getPlugin().saveResource("language"+File.separator+"zh_TW.yml",false);
        }
    }
}
