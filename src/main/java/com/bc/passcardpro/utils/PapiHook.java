package com.bc.passcardpro.utils;

import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.pojo.Season;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Luckily_Baby
 * @date 2020/7/11 12:01
 */
public class PapiHook extends EZPlaceholderHook {
    private final Season seasons=new Season();
    public PapiHook(Plugin plugin, String identifier) {
        super(plugin, identifier);
    }

    @Override
    public String onPlaceholderRequest(Player player, String args) {
        if(DataBase.passCardPlayerMap.get(player) == null){
            return "-1";
        }
        double maxWeekPoint=DataBase.passCardPlayerMap.get(player).isVip()?CfgLoader.vipWeekMaxPoint:CfgLoader.weekMaxPoint;
        switch (args){
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
}