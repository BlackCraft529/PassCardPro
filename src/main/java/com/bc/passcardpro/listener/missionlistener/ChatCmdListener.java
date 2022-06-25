package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/3 10:02
 */
public class ChatCmdListener implements Listener {
    /**
     * 玩家发言事件
     *
     * @param event 事件
     */
    @EventHandler(ignoreCancelled=true)
    public void onPlayerChat(AsyncPlayerChatEvent event){
        final Player player=event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for(Mission mission:playerMissions){
                    if("CHAT".equalsIgnoreCase(mission.getType())){
                        //玩家任务进度增加
                        String rate=(Double.parseDouble(mission.getRate())+1)+"";
                        PassCard.passCardAPI.updatePlayerMissionRate(player,mission.getMissionId(),rate);
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("CHAT".equalsIgnoreCase(mission.getType())){
                        //玩家任务进度增加
                        String rate=(Double.parseDouble(mission.getRate())+1)+"";
                        PassCard.passCardAPI.updatePlayerRandomMissionRate(player,mission.getMissionId(),rate);
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }

    /**
     * 玩家使用指令事件
     *
     * @param event 事件
     */
    @EventHandler(ignoreCancelled=true)
    public void onPlayerUseCmd(PlayerCommandPreprocessEvent event){
        String cmd=event.getMessage().trim();
        final Player player=event.getPlayer();
        if(!CfgLoader.missionComplete){
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for (Mission mission : playerMissions) {
                    if("COMMAND".equalsIgnoreCase(mission.getType().split("_")[0])){
                        if(cmd.startsWith("/"+mission.getType().replaceAll("COMMAND_","").replaceAll("_"," "))||
                            cmd.startsWith("/"+mission.getType().replaceAll("COMMAND_","").replaceAll("_"," ").toLowerCase())) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for (Mission mission : playerRandomMissions) {
                    if("COMMAND".equalsIgnoreCase(mission.getType().split("_")[0])){
                        if(cmd.startsWith("/"+mission.getType().replaceAll("COMMAND_","").replaceAll("_"," "))||
                                cmd.startsWith("/"+mission.getType().replaceAll("COMMAND_","").replaceAll("_"," ").toLowerCase())) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
}
