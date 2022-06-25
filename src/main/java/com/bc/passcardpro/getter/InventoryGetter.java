package com.bc.passcardpro.getter;

import com.bc.passcardpro.loader.*;
import com.bc.passcardpro.pojo.*;
import com.bc.passcardpro.utils.DataBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 19:02
 */
public class InventoryGetter {
    /**
     * 获取玩家任务列表界面
     *
     * @param player 玩家
     * @return 任务界面
     */
    public static Inventory getMissionInventory(Player player){
        Inventory inventory= Bukkit.createInventory(null,27, CfgLoader.guiMissionTitle);
        for(int i=0;i<27;i++){
            List<String> lore=CfgLoader.cfg.getStringList("Gui.Mission.Desc");
            if(MissionGuiLoader.missionGui.get(""+i)==null){
                continue;
            }
            String itemId= MissionGuiLoader.missionGui.getString(""+i);
            assert itemId != null;
            if(GuiItemLoader.itemData.get(itemId)==null){
                continue;
            }
            ItemStack itemStack=new ItemStack(Objects.requireNonNull(GuiItemLoader.itemData.getItemStack(itemId)));
            MissionPlayer missionPlayer=DataBase.missionPlayerMap.get(player);
            List<Mission> randomMission=DataBase.playerRandomMission.get(player);
            if(i>=9&&i<=17){
                if(itemStack.getItemMeta()==null){ continue; }
                ItemMeta itemMeta=itemStack.getItemMeta();
                Mission mission;
                if(i<15) {
                    mission = missionPlayer
                            .getPlayerMission()
                            .get("mission_" + (i - 8));
                }else {
                    mission=randomMission.get(i-15);
                }
                String desc="";
                if(mission.getDesc()!=null){
                    desc=mission.getDesc().replaceAll("&","§");
                }
                for(int s=0;s<lore.size();s++){
                    lore.set(s,lore.get(s).replaceAll("<missionName>",mission.getName())
                            .replaceAll("<rate>",mission.getRate())
                            .replaceAll("<require>",mission.getRequire())
                            .replaceAll("<finishTime>",mission.getFinishTime()+"")
                            .replaceAll("<maxTime>",mission.getMaxTime()+"")
                            .replaceAll("<givenPoint>",mission.getGivenPoint()+"")
                            .replaceAll("<missionId>",mission.getMissionId())
                            .replaceAll("<missionDesc>",desc)
                            .replaceAll("&","§"));
                }
                if(mission.getFinishTime()>=mission.getMaxTime()){
                    itemMeta.setDisplayName(itemMeta.getDisplayName()+"  "+CfgLoader.guiMissionNoticeFinish);
                }else{
                    itemMeta.setDisplayName(itemMeta.getDisplayName()+"  "+CfgLoader.guiMissionNoticeContinue);
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(i,itemStack);
        }
        return inventory;
    }

    /**
     * 获取玩家主菜单
     *
     * @param player 玩家
     * @return 主菜单
     */
    public static Inventory getPlayerMenu(Player player){
        Inventory inventory= Bukkit.createInventory(null,27, CfgLoader.guiMenuTitle);
        if(DataBase.passCardPlayerMap.get(player)==null){
            return null;
        }
        for(int i=0;i<27;i++){
            if(MenuGuiLoader.menuGui.get(""+i)==null){
                continue;
            }
            String itemId= MenuGuiLoader.menuGui.getString(""+i);
            assert itemId != null;
            if(GuiItemLoader.itemData.get(itemId)==null){
                continue;
            }
            ItemStack itemStack=new ItemStack(Objects.requireNonNull(GuiItemLoader.itemData.getItemStack(itemId)));
            PassCardPlayer passCardPlayer=DataBase.passCardPlayerMap.get(player);
            if(i==13){
                if(itemStack.getItemMeta()==null){ continue; }
                ItemMeta itemMeta=itemStack.getItemMeta();
                List<String> lore=CfgLoader.cfg.getStringList("Gui.Menu.Desc");
                for(int s=0;s<lore.size();s++){
                    lore.set(s,lore.get(s).replaceAll("<player>",player.getName())
                            .replaceAll("<level>",passCardPlayer.getPassCardLevel()+"")
                            .replaceAll("<point>",passCardPlayer.getPoint()+"")
                            .replaceAll("<weekPoint>",passCardPlayer.getWeekPoint()+"")
                            .replaceAll("<maxWeekPoint>",passCardPlayer.isVip()?CfgLoader.vipWeekMaxPoint+"":CfgLoader.weekMaxPoint+"")
                            .replaceAll("<nextLevelNeedPoint>",passCardPlayer.getNextLevelNeedPoint()+"")
                            .replaceAll("<defaultGets>",(passCardPlayer.getGets().split(",").length-1)+"")
                            .replaceAll("<vipGets>",(passCardPlayer.getVipGets().split(",").length-1)+"")
                            .replaceAll("<vip>",passCardPlayer.isVip()?CfgLoader.vipTitle:CfgLoader.defaultTitle)
                            .replaceAll("&","§"));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(i,itemStack);
        }
        return inventory;
    }

    /**
     * 打开玩家领取界面
     *
     * @param player 玩家
     */
    public static void openPlayerAwardInv(Player player, boolean isUpdate,Inventory playerOpenedInventory){
        Inventory inventory= Bukkit.createInventory(null,54, CfgLoader.guiAwardTitle);
        if(isUpdate){
            inventory=playerOpenedInventory;
        }
        int[] defaultAwardSlot={19,20,21,22,23,24,25};
        int[] vipAwardSlot={28,29,30,31,32,33,34};
        int topButton=8;
        for(int i=0;i<54;i++){
            if(isUpdate){
                inventory.clear(i);
            }
            //物品为空或者不存在
            if(AwardGuiLoader.gui.get(i+"")==null||
                    GuiItemLoader.itemData.getItemStack(Objects.requireNonNull(AwardGuiLoader.gui.getString(i + "")))==null){
                continue;
            }
            ItemStack itemStack=new ItemStack(Objects.requireNonNull(GuiItemLoader.itemData.getItemStack(Objects.requireNonNull(AwardGuiLoader.gui.getString(i + "")))));
            if(i==0){
                if(itemStack.getItemMeta()==null){ continue; }
                ItemMeta itemMeta=itemStack.getItemMeta();
                List<String> seasonInfo=CfgLoader.cfg.getStringList("Gui.Award.SeasonDesc");
                Season season=DataBase.seasonMap.get("season");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                for(int s=0;s<seasonInfo.size();s++){
                    seasonInfo.set(s,seasonInfo.get(s).replaceAll("<seasonName>",season.getSeasonName())
                            .replaceAll("<continue>",season.getContinueDays()+"")
                            .replaceAll("<startDay>",df.format(season.getStartDate()))
                            .replaceAll("<leftDay>",season.getLeftDays()+""));
                }
                itemMeta.setLore(seasonInfo);
                itemStack.setItemMeta(itemMeta);
            }
            if(i==topButton){
                ItemMeta itemMeta=itemStack.getItemMeta();
                if(itemMeta!=null){
                    //"§eTop.<top> §a<player> §9(§d<level>级§e<point>点§9)"
                    int top=1;
                    List<String> topLore=new ArrayList<>();
                    for(Map.Entry<Integer, TopPlayer> entry:DataBase.top10PlayerMap.entrySet()){
                        String topString = CfgLoader.topDesc.replaceAll("<top>",top+++"")
                                .replaceAll("<player>",entry.getValue().getName())
                                .replaceAll("<level>",entry.getValue().getLevel()+"")
                                .replaceAll("<point>",entry.getValue().getPoint()+"");
                        topLore.add(topString);
                    }
                    itemMeta.setLore(topLore);
                }
                itemStack.setItemMeta(itemMeta);
            }
            inventory.setItem(i,itemStack);
        }
        int defaultPage=DataBase.viewer.get(player),vipPage =DataBase.viewer.get(player);
        for(int i:defaultAwardSlot){
            if(isUpdate){
                inventory.clear(i);
            }
            //存在奖励
            if(AwardLoader.award.get(defaultPage+"")!=null){
                if(AwardLoader.award.getString(defaultPage + ".Default.Display")==null){
                    defaultPage++;
                    continue;
                }
                //获取奖励物品
                ItemStack itemStack=new ItemStack(
                        Objects.requireNonNull(AwardItemLoader.itemData.getItemStack(
                                Objects.requireNonNull(AwardLoader.award.getString(defaultPage + ".Default.Display")))));
                if(AwardLoader.award.get(defaultPage + ".Default.Lore")!=null){
                    ItemMeta itemMeta=itemStack.getItemMeta();
                    List<String> lore=AwardLoader.award.getStringList(defaultPage + ".Default.Lore");
                    assert itemMeta != null;
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                }
                inventory.setItem(i,itemStack);
            }

            defaultPage++;
        }
        for(int i:vipAwardSlot){
            if(isUpdate){
                inventory.clear(i);
            }
            //存在奖励
            if(AwardLoader.award.get(vipPage+"")!=null){
                if(AwardLoader.award.getString(vipPage + ".Vip.Display")==null){
                    vipPage++;
                    continue;
                }
                //获取奖励物品
                ItemStack itemStack=new ItemStack(
                        Objects.requireNonNull(AwardItemLoader.itemData.getItemStack(
                                Objects.requireNonNull(AwardLoader.award.getString(vipPage + ".Vip.Display")))));
                if(AwardLoader.award.get(vipPage + ".Vip.Lore")!=null){
                    ItemMeta itemMeta=itemStack.getItemMeta();
                    List<String> lore=AwardLoader.award.getStringList(vipPage + ".Vip.Lore");
                    assert itemMeta != null;
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                }
                inventory.setItem(i,itemStack);
            }
            vipPage++;
        }
        if(isUpdate){
            player.updateInventory();
        }else{
            player.openInventory(inventory);
        }
    }

}
