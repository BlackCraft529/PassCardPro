package com.bc.passcardpro.listener.missionlistener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.Mission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/3 9:52
 */
public class DamageListener implements Listener {
    /**
     * 玩家攻击实体次数事件
     *
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onDamageEntityTime(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player)){ return; }
        final Player player=(Player)event.getDamager();
        final String eventEntityName=event.getEntity().getCustomName()==null?event.getEntity().getName():event.getEntity().getCustomName();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions= PassCard.passCardAPI.getPlayerMissionList(player);
                for(Mission mission:playerMissions){
                    if("DAMAGETIME".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String damageEntityName = mission.getType().replaceAll("DAMAGETIME","");
                        if (eventEntityName.contains(damageEntityName)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + 1) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("DAMAGETIME".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String damageEntityName = mission.getType().replaceAll("DAMAGETIME","");
                        if (eventEntityName.contains(damageEntityName)) {
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
     * 玩家伤害实体事件
     *
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onDamageEntity(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player)){ return; }
        final Player player=(Player)event.getDamager();
        final String eventEntityName=event.getEntity().getCustomName()==null?event.getEntity().getName():event.getEntity().getCustomName();
        final double eventDamage=event.getDamage();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions= PassCard.passCardAPI.getPlayerMissionList(player);
                for(Mission mission:playerMissions){
                    if("DAMAGE".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String damageEntityName = mission.getType().replaceAll("DAMAGE_","");
                        if (eventEntityName.contains(damageEntityName)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + eventDamage) + "";
                            PassCard.passCardAPI.updatePlayerMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("DAMAGE".equalsIgnoreCase(mission.getType().split("_")[0])) {
                        String damageEntityName = mission.getType().replaceAll("DAMAGE_","");
                        if (eventEntityName.contains(damageEntityName)) {
                            //玩家任务进度增加
                            String rate = (Double.parseDouble(mission.getRate()) + eventDamage) + "";
                            PassCard.passCardAPI.updatePlayerRandomMissionRate(player, mission.getMissionId(), rate);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }

    /**
     * 玩家伤害玩家事件
     *
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onDamagePlayer(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player)||!(event.getEntity() instanceof Player)){ return; }
        final Player player=(Player)event.getDamager();
        final double eventDamage=event.getDamage();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mission> playerMissions= PassCard.passCardAPI.getPlayerMissionList(player);
                for(Mission mission:playerMissions){
                    if("DAMAGEPLAYER".equalsIgnoreCase(mission.getType())){
                        //玩家任务进度增加
                        String rate=(Double.parseDouble(mission.getRate())+ eventDamage)+"";
                        PassCard.passCardAPI.updatePlayerMissionRate(player,mission.getMissionId(),rate);
                    }
                }
                List<Mission> playerRandomMissions=PassCard.passCardAPI.getPlayerRandomMissionData(player);
                for(Mission mission:playerRandomMissions){
                    if("DAMAGEPLAYER".equalsIgnoreCase(mission.getType())){
                        //玩家任务进度增加
                        String rate=(Double.parseDouble(mission.getRate())+ eventDamage)+"";
                        PassCard.passCardAPI.updatePlayerRandomMissionRate(player,mission.getMissionId(),rate);
                    }
                }
            }
        }.runTaskAsynchronously(PassCard.getPlugin());
    }
}
