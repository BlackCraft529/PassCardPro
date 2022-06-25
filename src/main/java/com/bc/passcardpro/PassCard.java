package com.bc.passcardpro;

import com.bc.passcardpro.api.PassCardAPI;
import com.bc.passcardpro.api.SqlManager;
import com.bc.passcardpro.api.YmlManager;
import com.bc.passcardpro.getter.InventoryGetter;
import com.bc.passcardpro.listener.InventoryListener;
import com.bc.passcardpro.listener.PlayerListener;
import com.bc.passcardpro.loader.*;
import com.bc.passcardpro.listener.missionlistener.*;
import com.bc.passcardpro.pojo.Mission;
import com.bc.passcardpro.task.RefreshPassCardPlayer;
import com.bc.passcardpro.task.RefreshPlayerMission;
import com.bc.passcardpro.task.RefreshSeasonData;
import com.bc.passcardpro.task.RefreshTopPlayer;
import com.bc.passcardpro.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Objects;

/**
 * @author Luckily_Baby
 * @date 2020/7/2 10:56
 *
 */
public class PassCard extends JavaPlugin {
    private static PassCard plugin;
    public static String version="1.3.2b",pluginTitle="§a[§bPassCard-§e§lPro§a] ";
    public static PassCardAPI passCardAPI=null;
    public static CommandSender logger;
    public static PassCard getPlugin(){return  plugin;}

    /**
     * 插件卸载
     */
    @Override
    public void onDisable(){
        Banner.outBigBanner();
        logger.sendMessage(pluginTitle+"§4尝试卸载PassCard-§e§lPro§b插件...");
        passCardAPI.closeDataBase();
        PapiReg.unhook();
        logger.sendMessage(pluginTitle+"§4卸载完成！");
    }

    /**
     * 插件启动
     */
    @Override
    public void onEnable() {
        plugin = this;
        logger = Bukkit.getConsoleSender();
        Banner.outSlantBanner();
        logger.sendMessage(pluginTitle+"§bFor MC §41.12.2-1.16.1 ");
        logger.sendMessage(pluginTitle+"§c当前版本: "+this.getServer().getVersion());
        logger.sendMessage(pluginTitle+"§b尝试加载PassCard-§e§lPro§b插件!");
        //监听器载入
        registerListeners();

        //加载文件
        CfgLoader.loadCfg();
        LangLoader.loadLang();
        AwardItemLoader.loadItem();
        AwardLoader.loadAward();
        AwardGuiLoader.loadGui();
        GuiItemLoader.loadItem();
        MissionGuiLoader.loadGui();
        MenuGuiLoader.loadGui();
        //获取API
        passCardAPI=CfgLoader.useSql?new SqlManager():new YmlManager();

        //进行数据连接
        logger.sendMessage(pluginTitle+"§b异步加载数据库...");
        new BukkitRunnable() {
            @Override
            public void run() {
                if(PassCard.passCardAPI.enableDataBase()) {
                    passCardAPI.updateSqlTableData();
                    //启动任务
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new RefreshPassCardPlayer().run();
                        }
                    }.runTaskAsynchronously(PassCard.getPlugin());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new RefreshPlayerMission().run();
                        }
                    }.runTaskAsynchronously(PassCard.getPlugin());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new RefreshTopPlayer().run();
                        }
                    }.runTaskAsynchronously(PassCard.getPlugin());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new RefreshSeasonData().run();
                        }
                    }.runTaskAsynchronously(PassCard.getPlugin());
                    PassCard.logger.sendMessage(PassCard.pluginTitle + "§6数据(" + (CfgLoader.useSql?"MySql":"本地") + ")初始化完毕!");
                }
            }
        }.runTaskAsynchronously(this);
        if(CfgLoader.hookType==1) {
            new PapiHook(this, "PassCardPro");
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§aPlaceholderAPI已Hook!");
        }else if(CfgLoader.hookType==2){
            PapiReg.hook();
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§aPlaceholderAPI已Hook!");
        }else{
            PapiReg.hook();
            PassCard.logger.sendMessage(PassCard.pluginTitle+"§aPlaceholderAPI的HOOK类型错误，已为您自动选择方式: 2");
        }
        Metrics metrics = new Metrics(this,	8243);
        OutPut.outPutMissionType();
        logger.sendMessage(pluginTitle+"§a加载完成,插件版本: "+version+" !");
    }

    /**
     * 注册监听器
     */
    private void registerListeners(){
        this.getServer().getPluginManager().registerEvents(new KillListener(),this);
        this.getServer().getPluginManager().registerEvents(new DamageListener(),this);
        this.getServer().getPluginManager().registerEvents(new ChatCmdListener(),this);
        this.getServer().getPluginManager().registerEvents(new BreakDestroyListener(),this);
        this.getServer().getPluginManager().registerEvents(new CostListener(),this);
        this.getServer().getPluginManager().registerEvents(new GetDropListener(),this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(),this);
        this.getServer().getPluginManager().registerEvents(new JumpListener(),this);
        this.getServer().getPluginManager().registerEvents(new ShearListener(),this);
        this.getServer().getPluginManager().registerEvents(new RiptideListener(),this);
        this.getServer().getPluginManager().registerEvents(new ConsumeListener(),this);
    }

    /**
     * 指令监听
     *
     * @param sender 发送者
     * @param cmd 指令
     * @param label label
     * @param args args
     * @return 是否成功执行
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!"pcp".equalsIgnoreCase(cmd.getName())) {
            return true;
        }
        if (args.length == 0) {
            for (String s : HelpLoader.getHelp(sender.isOp())) {
                sender.sendMessage(s);
            }
            return true;
        }
        if(args.length == 1){
            if("reload".equalsIgnoreCase(args[0])){
                CfgLoader.loadCfg();
                LangLoader.loadLang();
                AwardItemLoader.loadItem();
                AwardLoader.loadAward();
                AwardGuiLoader.loadGui();
                GuiItemLoader.loadItem();
                MissionGuiLoader.loadGui();
                MenuGuiLoader.loadGui();
                sender.sendMessage(LangLoader.title+"§a完成!");
                return true;
            }
            if(!(sender instanceof Player)){
                sender.sendMessage(pluginTitle+"§4控制台无法使用该指令...");
                return true;
            }
            if("missionList".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.open"));
                    return true;
                }
                final CommandSender commandSender=sender;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(Mission mission:passCardAPI.getAllMissionData()) {
                            commandSender.sendMessage("§c╔╦╦╦");
                            commandSender.sendMessage("§c║§e任务ID-> §a"+mission.getMissionId()+"  §e任务名称-> §a"+mission.getName());
                            commandSender.sendMessage("§c║§e任务类型-> §a"+mission.getType()+"  §e最大次数-> §a"+mission.getMaxTime());
                            commandSender.sendMessage("§c║§e任务点数-> §a"+mission.getGivenPoint()+"  §e任务需求-> §a"+mission.getRequire());
                            commandSender.sendMessage("§c║§e任务描述-> "+mission.getDesc().replaceAll("&","§"));
                            commandSender.sendMessage("§c╚╩╩╩");
                        }
                    }
                }.runTaskAsynchronously(this);
            }
            if("open".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.open")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.open"));
                    return true;
                }
                if(InventoryGetter.getPlayerMenu((Player)sender)==null){
                    sender.sendMessage(LangLoader.title+LangLoader.noticeWait);
                    return true;
                }
                ((Player)sender).openInventory(Objects.requireNonNull(InventoryGetter.getPlayerMenu((Player) sender)));
            }

            if("itemType".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                Player player=(Player)sender;
                if(player.getInventory().getItemInMainHand().getType()!=Material.AIR){
                    sender.sendMessage(LangLoader.title+"§eItemType 物品类型-> §a"+player.getInventory().getItemInMainHand().getType().toString());
                    return true;
                }
            }
            if("clicker".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                Player player=(Player)sender;
                if(DataBase.clicker.contains(player)){
                    DataBase.clicker.remove(player);
                    player.sendMessage(LangLoader.title+"§4已关闭");
                    return true;
                }else{
                    DataBase.clicker.add(player);
                    player.sendMessage(LangLoader.title+"§a已开启");
                    return true;
                }
            }
        }
        if(args.length==2){
            if("deleteMission".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                final CommandSender commandSender=sender;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(passCardAPI.getMissionById(args[1])!=null) {
                            passCardAPI.deleteMissionById(args[1], commandSender);
                        }else{
                            commandSender.sendMessage(LangLoader.title+LangLoader.errorMissionNotExist);
                        }
                    }
                }.runTaskAsynchronously(this);
            }
        }
        if(args.length==3){
            if("addPoint".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                if(Bukkit.getPlayer(args[1])!=null){
                    final Player player=Bukkit.getPlayer(args[1]);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            passCardAPI.updatePlayerPoint(passCardAPI.getPlayerData(player),Double.parseDouble(args[2]),true,true);
                            sender.sendMessage(LangLoader.title+LangLoader.noticeOperateSuccessful);
                        }
                    }.runTaskAsynchronously(this);
                }else if(Bukkit.getOfflinePlayer(args[1])!=null){
                    final Player player=Bukkit.getOfflinePlayer(args[1]).getPlayer();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            passCardAPI.updatePlayerPoint(passCardAPI.getPlayerData(player),Double.parseDouble(args[2]),true,true);
                            sender.sendMessage(LangLoader.title+LangLoader.noticeOperateSuccessful);
                        }
                    }.runTaskAsynchronously(this);
                }else{
                    sender.sendMessage(LangLoader.title+LangLoader.errorPlayerNotExist);
                    return true;
                }
            }
            if("season".equalsIgnoreCase(args[0])){
                //pcp season <day/name> <value>
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                if("day".equalsIgnoreCase(args[1])){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                int continueDays=Integer.parseInt(args[2]);
                                passCardAPI.updateSeasonContinueDays(continueDays, sender);
                            }catch (Exception ex){
                                sender.sendMessage(LangLoader.title+"§4参数错误!");
                            }
                        }
                    }.runTaskAsynchronously(this);
                }else if("name".equalsIgnoreCase(args[1])){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            passCardAPI.updateSeasonName(args[2],sender);
                        }
                    }.runTaskAsynchronously(this);
                }

            }
            if("setDesc".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                if(!(sender instanceof Player)){
                    sender.sendMessage(pluginTitle+"§4控制台无法使用该指令...");
                    return true;
                }
                Player player=(Player)sender;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        passCardAPI.updateMissionDesc(args[1],args[2],player);
                    }
                }.runTaskAsynchronously(this);
            }
            if("setVip".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                Player updatePlayer=Bukkit.getPlayer(args[1]);
                if(updatePlayer==null){
                    sender.sendMessage(LangLoader.title+LangLoader.errorPlayerNotExist);
                    return true;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        passCardAPI.updatePlayerVip(updatePlayer,Boolean.parseBoolean(args[2]),sender);
                    }
                }.runTaskAsynchronously(this);
            }
            if("addItem".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                if(!(sender instanceof Player)){
                    sender.sendMessage(pluginTitle+"§4控制台无法使用该指令...");
                    return true;
                }
                Player player=(Player)sender;
                if(player.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                    player.sendMessage(LangLoader.title+"§4手中物品为空,请重试...");
                    return true;
                }
                if("gui".equalsIgnoreCase(args[1])){
                    if(GuiItemLoader.itemData.get(args[2])!=null){
                        player.sendMessage(LangLoader.title+"§4物品代号已存在,请重试...");
                        return true;
                    }
                    ItemStack itemStack=new ItemStack(player.getInventory().getItemInMainHand());
                    GuiItemLoader.itemData.set(args[2],itemStack);
                    GuiItemLoader.save();
                    player.sendMessage(LangLoader.title+"§a完成.");
                    return true;
                }else if("award".equalsIgnoreCase(args[1])){
                    if(AwardItemLoader.itemData.get(args[2])!=null){
                        player.sendMessage(LangLoader.title+"§4物品代号已存在,请重试...");
                        return true;
                    }
                    ItemStack itemStack=new ItemStack(player.getInventory().getItemInMainHand());
                    AwardItemLoader.itemData.set(args[2],itemStack);
                    AwardItemLoader.save();
                    player.sendMessage(LangLoader.title+"§a完成.");
                    return true;
                }
            }
        }
        if( args.length == 5){
            if("addSeason".equalsIgnoreCase(args[0])){
                //pcp addSeason <seasonId> <name> <continueDays> <state(true/false)>
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {

                    }
                }.runTaskAsynchronously(this);
            }
        }
        if (args.length == 8){
            if("addMission".equalsIgnoreCase(args[0])){
                if(!sender.hasPermission("pcp.admin")){
                    sender.sendMessage(LangLoader.title+LangLoader.errorNoPermission.replaceAll("<Permission>","pcp.admin"));
                    return true;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            //String missionId,String type,String name,String require,double givenPoint,int maxTime,boolean random
                            //pcp addMission <missionId> <name> <type> <require> <givenPoint> <maxTime> <random>  §8新增一条任务数据
                            Mission mission = new Mission(args[1], args[3], args[2].replaceAll("&", "§"), args[4]
                                    , Double.parseDouble(args[5]), Integer.parseInt(args[6]), Boolean.parseBoolean(args[7]));
                            if (passCardAPI.createNewMissionData(mission)) {
                                sender.sendMessage(LangLoader.title+LangLoader.noticeAddMission
                                        .replaceAll("<missionName>",args[2].replaceAll("&", "§"))
                                        .replaceAll("<missionId>",args[1]));
                            }else{
                                sender.sendMessage(LangLoader.title+LangLoader.errorAddMission);
                            }
                        }catch (Exception ex){
                            logger.sendMessage(pluginTitle+"§4addMission执行错误,具体报错信息如下: ");
                            ex.printStackTrace();
                            sender.sendMessage(pluginTitle+"§4参数错误,请查看帮助!");
                        }
                    }
                }.runTaskAsynchronously(this);
            }
        }
        return true;
    }
}
