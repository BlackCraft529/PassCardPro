package com.bc.passcardpro.pojo;

import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luckily_Baby
 * @date 2020/7/6 22:12
 */
public class MissionPlayer {
    private Player player;
    private PassCardPlayer passCardPlayer;
    private ConcurrentHashMap<String,Mission> playerMission;

    public MissionPlayer(){}

    public MissionPlayer(Player player,PassCardPlayer passCardPlayer,ConcurrentHashMap<String,Mission> playerMission){
        this.passCardPlayer=passCardPlayer;
        this.player=player;
        this.playerMission=playerMission;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PassCardPlayer getPassCardPlayer() {
        return passCardPlayer;
    }

    public void setPassCardPlayer(PassCardPlayer passCardPlayer) {
        this.passCardPlayer = passCardPlayer;
    }

    public ConcurrentHashMap<String, Mission> getPlayerMission() {
        return playerMission;
    }

    public void setPlayerMission(ConcurrentHashMap<String, Mission> playerMission) {
        this.playerMission = playerMission;
    }
}
