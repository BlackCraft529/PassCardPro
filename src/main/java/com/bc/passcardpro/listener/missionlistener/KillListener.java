package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.UUID;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 15:54
 */
public class KillListener implements Listener {
    /**
     * 怪物名称击杀任务
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerKillEntityByName(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getDamager();
        final String eventEntityName = event.getEntity().getCustomName() == null ? event.getEntity().getName() : event.getEntity().getCustomName();
        final UUID entityUUID = event.getEntity().getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Entity entity = Bukkit.getServer().getEntity(entityUUID);
                if (entity == null || !entity.isDead()) {
                    return;
                }
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for (Mission mission : playerMissions) {
                    if ("KILLE".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String killName = mission.getType().replaceAll("KILLE_", "");
                        if (eventEntityName.contains(killName)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions = PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for (Mission mission : playerRandomMissions) {
                    if ("KILLE".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String killName = mission.getType().replaceAll("KILLE_", "");
                        if (eventEntityName.contains(killName)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
    /**
     * 怪物类型击杀任务
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerKillEntity(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getDamager();
        final String eventType = event.getEntity().getType().toString();
        final UUID entityUUID =event.getEntity().getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Entity entity=Bukkit.getServer().getEntity(entityUUID );
                if(entity==null || !entity.isDead()){
                    return;
                }
                List<Mission> playerMissions = PassCard.passCardAPI.getPlayerMissionList(player);
                for (Mission mission : playerMissions) {
                    if ("KILL".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String killType = mission.getType().replaceAll("KILL_", "");
                        if (eventType.equalsIgnoreCase(killType)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions = PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for (Mission mission : playerRandomMissions) {
                    if ("KILL".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String killType = mission.getType().replaceAll("KILL_", "");
                        if (eventType.equalsIgnoreCase(killType)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }

    /**
     * 玩家击杀玩家事件
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerKillPlayer(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player)||!(event.getEntity() instanceof Player)){ return; }
        final Player player=(Player)event.getDamager();
        final Player deadPlayer =(Player)event.getEntity();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!deadPlayer.isDead()){
                    return;
                }
                List<Mission> playerMissions= PassCard.passCardAPI.getPlayerMissionList(player);
                for(Mission mission:playerMissions){
                    if("KILLPLAYER".equalsIgnoreCase(mission.getType())){
                        //玩家任务进度增加
                        String rate=(Double.parseDouble(mission.getRate())+1)+"";
                        PassCard.passCardAPI.updatePlayerMissionRate(player,mission.getMissionId(),rate);
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("KILLPLAYER".equalsIgnoreCase(mission.getType())){
                        //玩家任务进度增加
                        String rate=(Double.parseDouble(mission.getRate())+1)+"";
                        PassCard.passCardAPI.updatePlayerRandomMissionRate(player,mission.getMissionId(),rate);
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
}
