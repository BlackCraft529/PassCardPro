package com.bc.passcardpro.task;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.utils.DataBase;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Luckily_Baby
 * @date 2020/7/11 12:29
 */
public class RefreshSeasonData extends BukkitRunnable {
    private static boolean keepRunning=true;

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public static void setKeepRunning(boolean keepRunning) {
        RefreshSeasonData.keepRunning = keepRunning;
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
        while (keepRunning){
            try {
                if(PassCard.passCardAPI.getNowSeasonData()!=null) {
                    DataBase.seasonMap.put("season", PassCard.passCardAPI.getNowSeasonData());
                }
                //3秒刷新一次
                Thread.sleep((long) CfgLoader.refreshTime);
            } catch (Exception ignored) { }
        }
    }
}
