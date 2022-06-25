package com.bc.passcardpro.loader;

import com.bc.passcardpro.PassCard;
import java.io.File;

/**
 * @author Luckily_Baby
 * @date 2020/7/7 16:12
 */
public class OutPut {

    public static void outPutMissionType(){
        File missionType =new File(PassCard.getPlugin().getDataFolder(), "MissionType.yml");
        if(!missionType.exists()) {
            PassCard.getPlugin().saveResource("MissionType.yml", false);
            PassCard.logger.sendMessage(PassCard.pluginTitle + "§b已为您重新生成任务帮助: §aMissionType.yml §b文件!");
        }
    }
}
