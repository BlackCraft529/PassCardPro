package com.bc.passcardpro.utils;

import com.bc.passcardpro.PassCard;

/**
 * @author Luckily_Baby
 * @date 2020/7/7 21:54
 */
public class Banner {
    public static void outBigBanner(){
        PassCard.logger.sendMessage(" ");
        PassCard.logger.sendMessage("§e_____               _____              _");
        PassCard.logger.sendMessage("§e|  __ \\             / ____|            | |");
        PassCard.logger.sendMessage("§e| |__) |_ _ ___ ___| |     __ _ _ __ __| |_ __  _ __ ___");
        PassCard.logger.sendMessage("§e|  ___/ _` / __/ __| |    / _` | '__/ _` | '_ \\| '__/ _ \\ ");
        PassCard.logger.sendMessage("§e| |  | (_| \\__ \\__ \\ |___| (_| | | | (_| | |_) | | | (_) |");
        PassCard.logger.sendMessage("§e|_|   \\__,_|___/___/\\_____\\__,_|_|  \\__,_| .__/|_|  \\___/");
        PassCard.logger.sendMessage("§e                                         | |              ");
        PassCard.logger.sendMessage("§e                                         |_|");
        PassCard.logger.sendMessage("§d                                             By bbs:(§eLuckily_Baby§d) §bSCT!");
    }
    public static void outSlantBanner(){
        PassCard.logger.sendMessage(" ");
        PassCard.logger.sendMessage("§b    ____                  ______               ______ ");
        PassCard.logger.sendMessage("§b   / __ \\____ ___________/ ____/___ __________/ / __ \\_________");
        PassCard.logger.sendMessage("§b  / /_/ / __ `/ ___/ ___/ /   / __ `/ ___/ __  / /_/ / ___/ __ \\");
        PassCard.logger.sendMessage("§b / ____/ /_/ (__  |__  ) /___/ /_/ / /  / /_/ / ____/ /  / /_/ /");
        PassCard.logger.sendMessage("§b/_/    \\__,_/____/____/\\____/\\__,_/_/   \\__,_/_/   /_/   \\____/");
        PassCard.logger.sendMessage("§d                                             By bbs:(§eLuckily_Baby§d) §bSCT!");
    }
}
