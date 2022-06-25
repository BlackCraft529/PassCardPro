package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/21 20:08
 */
public class ConsumeListener implements Listener {

    /**
     * 玩家吃东西喝牛奶
     *
     * @param event 事件
     */
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event){
        final Player player=event.getPlayer();
        final String itemType=event.getItem().getType().toString();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for (Mission mission : playerMissions) {
                    if ("CONSUME".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String itemTypeNeed=mission.getType().replaceAll("CONSUME_","");
                        if(itemType.equalsIgnoreCase(itemTypeNeed)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for (Mission mission : playerRandomMissions) {
                    if ("CONSUME".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String itemTypeNeed=mission.getType().replaceAll("CONSUME_","");
                        if(itemType.equalsIgnoreCase(itemTypeNeed)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
}
