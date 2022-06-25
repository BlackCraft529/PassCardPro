package com.bc.passcardpro.loader;

import com.bc.passcardpro.getter.IdGetter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 14:59
 */
public class HelpLoader {
    public static List<String> getHelp(boolean isOp){
        List<String> help=new ArrayList<>();
        help.add("§a本周ID: §b"+ IdGetter.getWeekId());
        help.add("§a帮助列表:");
        help.add("§a1./pcp open  §8打开个人界面");
        if(isOp){
            help.add("§4以下内容仅OP可见:");
            help.add("§b/pcp operate <player>  §8打开玩家操作界面");
            help.add("§b/pcp addMission <missionId> <name> <type> <require> <givenPoint> <maxTime> <random>  §8新增一条任务数据");
            help.add("§b/pcp setDesc <missionId> <desc>  §8设置任务描述");
            help.add("§b/pcp season <day/name> <value>  §8设置赛季持续天数或名称");
            help.add("§b/pcp addItem <gui/award> <id>  §8将手中物品添加到配置文件中(gui:界面,award:奖励)");
            help.add("§b/pcp setVip <player> <true/false>  §8设置玩家的VIP状态");
            help.add("§b/pcp addPoint <player> <point>  §8新增玩家点数");
            help.add("§b/pcp missionList  §8查看所有任务列表");
            help.add("§b/pcp deleteMission <id>  §8删除一个任务 §4请勿在任务完备状态下删除任务!");
            help.add("§b/pcp itemType  §8查看手中的物品类型");
            help.add("§b/pcp clicker  §8转变右击生物查看类型状态");
            help.add("§b/pcp reload  §8重载插件");
        }
        return  help;
    }
}
