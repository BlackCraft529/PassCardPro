package com.bc.passcardpro.utils;

import com.bc.passcardpro.loader.AwardLoader;

/**
 * @author Luckily_Baby
 * @date 2020/7/17 15:16
 */
public class Math {
    /**
     * 获取从一级到level需要多少点数
     *
     * @param level 等级
     * @return 点数总和
     */
    public static double getLevelPoint(int level){
        double points=0;
        for(int i=1;i<=level;i++){
            if(AwardLoader.award.get(i+"")==null){
                break;
            }
            points+=AwardLoader.award.getDouble(i+".Point");
        }
        return points;
    }
}
