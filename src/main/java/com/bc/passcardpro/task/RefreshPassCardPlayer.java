package com.bc.passcardpro.task;

import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.pojo.PassCardPlayer;
import com.bc.passcardpro.utils.DataBase;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Luckily_Baby
 * @date 2020/7/6 22:09
 */
public class RefreshPassCardPlayer extends BukkitRunnable {
    private static boolean keepRunning=true;

    public static boolean isKeepRunning() {
        return keepRunning;
    }

    public static void setKeepRunning(boolean keepRunning) {
        RefreshPassCardPlayer.keepRunning = keepRunning;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while(keepRunning){
            try {
                for (Player player: PassCard.getPlugin().getServer().getOnlinePlayers()) {
                    if(PassCard.passCardAPI.getPlayerData(player)==null){
                        continue;
                    }
                    PassCardPlayer passCardPlayer=PassCard.passCardAPI.getPlayerData(player);
                    passCardPlayer.setNextLevelNeedPoint(PassCard.passCardAPI.getPlayerNextLevelNeedPoint(player));
                    DataBase.passCardPlayerMap.put(player, passCardPlayer);
                }
                //3秒刷新一次
                Thread.sleep((long)CfgLoader.refreshTime);
            } catch (Exception ignored) { }
        }
    }
}
