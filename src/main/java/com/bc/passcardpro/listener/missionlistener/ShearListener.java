package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/21 19:55
 */
public class ShearListener implements Listener {

    /**
     * 剪羊毛等任务触发
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerShear(PlayerShearEntityEvent event){
        final Player player = event.getPlayer();
        final String entityType=event.getEntity().getType().toString();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for (Mission mission : playerMissions) {
                    if ("SHEAR".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String entityTypeNeed=mission.getType().replaceAll("SHEAR_","");
                        if(entityType.equalsIgnoreCase(entityTypeNeed)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for (Mission mission : playerRandomMissions) {
                    if ("SHEAR".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String entityTypeNeed=mission.getType().replaceAll("SHEAR_","");
                        if(entityType.equalsIgnoreCase(entityTypeNeed)) {
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
