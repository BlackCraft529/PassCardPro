package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/5 19:25
 */
public class GetDropListener implements Listener {

    /**
     * 玩家捡起物品事件 - 收集
     *
     * 注意: 存在一个物品反复拾取刷任务的问题
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerCollect(PlayerPickupItemEvent event){
        //System.out.println("-PickUp-");
        final Item item=event.getItem();
        final Player player=event.getPlayer();
        final String itemType=item.getItemStack().getType().toString().replaceAll("_BLOCK","");
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions= PassCard.passCardAPI.getPlayerMissionList(player);
                int amount=item.getItemStack().getAmount();
                for(Mission mission:playerMissions){
                    if("COLLECT".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String pickUpItemType=mission.getType().replaceAll("COLLECT_","");
                        if(itemType.equalsIgnoreCase(pickUpItemType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRequire())+amount)+"";
                            PassCard.passCardAPI.updatePlayerMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("COLLECT".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String pickUpItemType=mission.getType().replaceAll("COLLECT_","");
                        if(itemType.equalsIgnoreCase(pickUpItemType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRequire())+amount)+"";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }

    /**
     * 玩家丢弃物品事件，丢弃物品任务
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        final Item item=event.getItemDrop();
        final Player player=event.getPlayer();
        final String itemType=item.getItemStack().getType().toString();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions= PassCard.passCardAPI.getPlayerMissionList(player);
                int amount=item.getItemStack().getAmount();
                for(Mission mission:playerMissions){
                    if("DROP".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String dropItemType=mission.getType().replaceAll("DROP_","");
                        if(itemType.equalsIgnoreCase(dropItemType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRequire())+amount)+"";
                            PassCard.passCardAPI.updatePlayerMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("DROP".equalsIgnoreCase(mission.getType().split("_")[0])){
                        String dropItemType=mission.getType().replaceAll("DROP_","");
                        if(itemType.equalsIgnoreCase(dropItemType)){
                            //玩家任务进度增加
                            String rate=(Double.parseDouble(mission.getRequire())+amount)+"";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player,mission.getMissionId(),rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
}
