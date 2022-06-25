package com.bc.passcardpro.listener;

import com.bc.passcardpro.getter.IdGetter;
import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.loader.LangLoader;
import com.bc.passcardpro.pojo.PassCardPlayer;
import com.bc.passcardpro.pojo.Season;
import com.bc.passcardpro.utils.DataBase;
import com.bc.passcardpro.utils.Updater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Luckily_Baby
 * @date 2020/7/5 23:40
 */
public class PlayerListener  implements Listener {
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 玩家首次加入游戏创建数据文件
     * 玩家加入时判断是否为本周的任务
     *
     * @param event 事件
     */
    @EventHandler
    public void onPlayerFirstJoin(PlayerJoinEvent event){
        final Player player=event.getPlayer();
        if(CfgLoader.missionComplete) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Updater.updateVersion132(player);
                    Season season = PassCard.passCardAPI.getNowSeasonData();
                    if(season==null){
                        PassCard.passCardAPI.createSeasonData();
                        PassCard.passCardAPI.resetPlayerSeasonData();
                    }else if(season.getLeftDays()<=0){
                        PassCard.passCardAPI.resetPlayerSeasonData();
                        PassCard.passCardAPI.updateNewSeason(new Season("season","§bUNKNOWN",new Date(),30,30));
                    }
                    if(PassCard.passCardAPI.getWeekMissionList(IdGetter.getWeekId()).size()<=0){
                        PassCard.passCardAPI.resetWeekMission();
                    }
                    if (PassCard.passCardAPI.getPlayerData(player) == null) {
                        PassCard.passCardAPI.createPlayerData(player);
                        try {
                            Thread.sleep(3000);
                            player.sendTitle("", LangLoader.titleShowCreateNewData,20,40,20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        PassCardPlayer passCardPlayer = PassCard.passCardAPI.getPlayerData(player);
                        if("daily".equalsIgnoreCase(CfgLoader.refreshType)&&
                                !passCardPlayer.getDate().equalsIgnoreCase(dateFormat.format(new Date()))){
                            PassCard.passCardAPI.updatePlayerDailyDateAndMission(player);
                        }else if("weekly".equalsIgnoreCase(CfgLoader.refreshType)&&
                                !passCardPlayer.getWeekId().equalsIgnoreCase(IdGetter.getWeekId())){
                            PassCard.passCardAPI.updatePlayerWeeklyDateAndMission(player);
                        }
                        if (!passCardPlayer.getWeekId().equalsIgnoreCase(IdGetter.getWeekId())) {
                            //更新每周点数
                            PassCard.passCardAPI.updatePlayerWeekPoint(passCardPlayer, 0);
                        }
                    }
                }
            }.runTaskAsynchronously(PassCard.getPlugin());
        }
    }

    /**
     * 获取生物类型
     *
     * @param entityEvent 生物类型
     */
    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent entityEvent){
        if(DataBase.clicker.contains(entityEvent.getPlayer())) {
            entityEvent.getPlayer().sendMessage(LangLoader.title + "§bEntityType 生物类型-> §a" + entityEvent.getRightClicked().getType().toString());
        }
    }

    /**
     * 获取方块类型
     *
     * @param event 方块类型
     */
    @EventHandler
    public void onClickBlock(PlayerInteractEvent event){
        if(DataBase.clicker.contains(event.getPlayer())){
            if(event.getClickedBlock()!=null){
                event.getPlayer().sendMessage(LangLoader.title + "§bEntityType 生物类型-> §a" + event.getClickedBlock().getType().toString());
            }
        }
    }

}
