package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/21 20:01
 */
public class RiptideListener implements Listener {
    @EventHandler
    public void onPlayerRiptide(PlayerRiptideEvent event){
        final Player player=event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for (Mission mission : playerMissions) {
                    if ("RIPTIDE".equalsIgnoreCase(mission.getType())) {
                        //玩家任务进度增加
                        String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                        PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);

                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for (Mission mission : playerRandomMissions) {
                    if ("RIPTIDE".equalsIgnoreCase(mission.getType())) {
                        //玩家任务进度增加
                        String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                        PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
}
