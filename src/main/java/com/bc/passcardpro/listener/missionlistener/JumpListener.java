package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/3 10:38
 */
public class JumpListener implements Listener {
    /**
     * 设置可飞行，用于监听双跳
     *
     * @param event 移动事件
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!CfgLoader.enableDoubleJump){
            return;
        }
        if (!event.isCancelled() && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && event.getPlayer().isOnGround()) {
            event.getPlayer().setAllowFlight(true);
        }

    }
    /**
     * 玩家双击跳跃事件触发
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDoubleJump(PlayerToggleFlightEvent event){
        if(!CfgLoader.enableDoubleJump){
            return;
        }
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            if(!player.getAllowFlight()){
                return;
            }
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            if(!player.hasPermission(CfgLoader.permissionFly)){
                event.setCancelled(true);
                player.setAllowFlight(false);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                    for (Mission mission : playerMissions) {
                        if ("DOUBLEJUMP".equalsIgnoreCase(mission.getType())) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                    List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                    for (Mission mission : playerRandomMissions) {
                        if ("DOUBLEJUMP".equalsIgnoreCase(mission.getType())) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
            }.runTaskAsynchronously(PassCard.getPlugin());
        }
    }
}
