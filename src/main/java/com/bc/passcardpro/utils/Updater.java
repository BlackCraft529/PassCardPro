package com.bc.passcardpro.utils;

import com.bc.passcardpro.PassCard;
import org.bukkit.entity.Player;

/**
 * @author Luckily_Baby
 * @date 2020/8/10 9:40
 */
public class Updater {

    /**
     * 插件更新132版本
     * @param player 玩家
     */
    public static void updateVersion132(Player player){
        PassCard.passCardAPI.updatePlayerData(player);
    }
}
