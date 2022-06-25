package com.bc.passcardpro.utils;

import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.pojo.Season;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;

/**
 * @author Luckily_Baby
 * @date 2020/7/20 2:33
 */
public class PapiReg extends PlaceholderHook {
    private static final String HOOK_NAME="PassCardPro";
    private Season seasons=new Season();
    @Override
    public String onPlaceholderRequest(Player player,String arg){
        if(DataBase.passCardPlayerMap.get(player) == null){
            return "§4暂无数据";
        }
        seasons=DataBase.seasonMap.get("season");
        double maxWeekPoint=DataBase.passCardPlayerMap.get(player).isVip()? CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
        switch (arg){
            case "point": return DataBase.passCardPlayerMap.get(player).getPoint()+"";
            case "level": return DataBase.passCardPlayerMap.get(player).getPassCardLevel()+"";
            case "maxWeekPoint": return maxWeekPoint+"";
            case "isVip": return DataBase.passCardPlayerMap.get(player).isVip()?CfgLoader.vipTitle:CfgLoader.defaultTitle;
            case "upgrade": return DataBase.passCardPlayerMap.get(player).getNextLevelNeedPoint()+"";
            case "seasonName": return seasons.getSeasonName();
            case "seasonLeftDay": return seasons.getLeftDays()+"";
            default: return "";
        }
    }
    public static void hook() {
        PlaceholderAPI.registerPlaceholderHook(HOOK_NAME, new PapiReg());
    }

    public static void unhook() {
        PlaceholderAPI.unregisterPlaceholderHook(HOOK_NAME);
    }
}
