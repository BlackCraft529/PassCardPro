package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.Objects;

/**
 * @author Luckily_Baby
 * @date 2020/7/3 11:15
 */
public class BreakDestroyListener implements Listener {

    /**
     * 玩家工具损坏事件 - (比如铲子，打火石，铁制工具)
     *
     * @param event 事件
     */
    @EventHandler
    public void onBreakItem(PlayerItemBreakEvent event){
        final Player player=event.getPlayer();
        final String eventItemType= event.getBrokenItem().getType().toString();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions= PassCard.passCardAPI.getPlayerMissionList(player);
                for(Mission mission:playerMissions){
                    if("ITEMBREAK".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String itemType=mission.getType().replaceAll("ITEMBREAK_","");
                        if(eventItemType.equalsIgnoreCase(itemType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRate())+1)+"";
                            PassCard.passCardAPI.updatePlayerMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("ITEMBREAK".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String itemType=mission.getType().replaceAll("ITEMBREAK_","");
                        if(eventItemType.equalsIgnoreCase(itemType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRate())+1)+"";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }

    /**
     * 玩家破坏方块时触发
     * @param event 事件
     */
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){
        final Player player=event.getPlayer();
        final String eventBlockType=event.getBlock().getType().toString();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for(Mission mission:playerMissions){
                    if("DESTROY".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String blockType=mission.getType().replaceAll("DESTROY_","");
                        if(eventBlockType.equalsIgnoreCase(blockType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRate())+1)+"";
                            PassCard.passCardAPI.updatePlayerMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("DESTROY".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String blockType=mission.getType().replaceAll("DESTROY_","");
                        if(eventBlockType.equalsIgnoreCase(blockType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRate())+1)+"";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
}
