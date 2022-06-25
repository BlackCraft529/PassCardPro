package com.bc.passcardpro.listener;

import com.bc.passcardpro.PassCard;
import com.bc.passcardpro.getter.InventoryGetter;
import com.bc.passcardpro.loader.AwardItemLoader;
import com.bc.passcardpro.loader.AwardLoader;
import com.bc.passcardpro.loader.CfgLoader;
import com.bc.passcardpro.loader.LangLoader;
import com.bc.passcardpro.pojo.PassCardPlayer;
import com.bc.passcardpro.utils.DataBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

/**
 * @author Luckily_Baby
 * @date 2020/7/6 23:31
 */
public class InventoryListener  implements Listener {
    /**
     * 点击任务界面
     *
     * @param event 事件
     */
    @EventHandler
    public void onClickMissionInv(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p= (Player) event.getWhoClicked();
        InventoryView gui=event.getView();
        if(!gui.getTitle().equalsIgnoreCase(CfgLoader.guiMissionTitle)){
            return;
        }
        event.setCancelled(true);
    }

    /**
     * 玩家点击主菜单
     *
     * @param event 事件
     */
    @EventHandler
    public void onClickMenuInv(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player= (Player) event.getWhoClicked();
        InventoryView gui=event.getView();
        if(!gui.getTitle().equalsIgnoreCase(CfgLoader.guiMenuTitle)){
            return;
        }
        event.setCancelled(true);
        int clickSlot=event.getRawSlot();
        if(clickSlot==11){
            player.openInventory(InventoryGetter.getMissionInventory(player));
        }else if(clickSlot==15){
            player.closeInventory();
            DataBase.viewer.put(player,1);
            InventoryGetter.openPlayerAwardInv(player,false,null);
        }
    }

    /**
     * 玩家点击奖励界面
     *
     * @param event 事件
     */
    @EventHandler
    public void onClickAwardInv(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        InventoryView gui = event.getView();
        if (!gui.getTitle().equalsIgnoreCase(CfgLoader.guiAwardTitle)) {
            return;
        }
        event.setCancelled(true);
        int clickSlot=event.getRawSlot();
        if(clickSlot==36){
            if(DataBase.viewer.get(player)-1>0) {
                DataBase.viewer.put(player, DataBase.viewer.get(player) - 1);
                InventoryGetter.openPlayerAwardInv(player, true,player.getOpenInventory().getTopInventory());
            }else{
                player.sendMessage(LangLoader.title+LangLoader.errorIsFirstAward);
            }
        }else if (clickSlot==44){
            if(AwardLoader.award.get((DataBase.viewer.get(player)+1)+"")!=null){
                DataBase.viewer.put(player, DataBase.viewer.get(player)+1);
                InventoryGetter.openPlayerAwardInv(player,true,player.getOpenInventory().getTopInventory());
            }else{
                player.sendMessage(LangLoader.title+ LangLoader.errorIsLastAward);
            }
        }
        if(clickSlot==49) {
            //领取奖励
            int getsCount = 0, vipGetsCount = 0;
            PassCardPlayer passCardPlayer = DataBase.passCardPlayerMap.get(player);
            String gets = passCardPlayer.getGets();
            String vipGets = passCardPlayer.getVipGets();
            for (String s : AwardLoader.award.getKeys(false)) {
                if (Integer.parseInt(s) > passCardPlayer.getPassCardLevel()) {
                    break;
                }
                if (AwardLoader.award.getString(s + ".Default.Display") == null) {
                    continue;
                }
                //给予普通奖励
                if (!gets.contains(s + ",")) {
                    gets += s + ",";
                    getsCount++;
                    List<String> awardCmd = AwardLoader.award.getStringList(s + ".Default.Command");
                    for (String cmd : awardCmd) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("<player>", player.getName()));
                    }
                    List<String> itemList = AwardLoader.award.getStringList(s + ".Default.Item");
                    for (String itemString : itemList) {
                        if (AwardItemLoader.itemData.get(itemString) != null && AwardItemLoader.itemData.getItemStack(itemString) != null) {
                            ItemStack itemStack = new ItemStack(Objects.requireNonNull(AwardItemLoader.itemData.getItemStack(itemString)));
                            player.getInventory().addItem(itemStack);
                        }
                    }
                }
            }
            if (passCardPlayer.isVip()) {
                for (String s : AwardLoader.award.getKeys(false)) {
                    if (Integer.parseInt(s) > passCardPlayer.getPassCardLevel()) {
                        break;
                    }
                    if (AwardLoader.award.getString(s + ".Vip.Display") == null) {
                        continue;
                    }
                    //给予普通奖励
                    if (!vipGets.contains(s + ",")) {
                        vipGets += s + ",";
                        vipGetsCount++;
                        List<String> awardCmd = AwardLoader.award.getStringList(s + ".Vip.Command");
                        for (String cmd : awardCmd) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("<player>", player.getName()));
                        }
                        List<String> itemList = AwardLoader.award.getStringList(s + ".Vip.Item");
                        for (String itemString : itemList) {
                            if (AwardItemLoader.itemData.get(itemString) != null && AwardItemLoader.itemData.getItemStack(itemString) != null) {
                                ItemStack itemStack = new ItemStack(Objects.requireNonNull(AwardItemLoader.itemData.getItemStack(itemString)));
                                player.getInventory().addItem(itemStack);
                            }
                        }
                    }
                }
            }
            if (getsCount == 0 && vipGetsCount == 0) {
                player.sendMessage(LangLoader.title + LangLoader.errorNoAward);
                return;
            }
            passCardPlayer.setGets(gets);
            passCardPlayer.setVipGets(vipGets);
            final int getsCounts=getsCount,vipGetsCounts=vipGetsCount;
            new BukkitRunnable() {
                @Override
                public void run() {
                    PassCard.passCardAPI.updatePassCardPlayerGets(passCardPlayer, getsCounts, vipGetsCounts);
                }
            }.runTaskAsynchronously(PassCard.getPlugin());
        }
    }
}
