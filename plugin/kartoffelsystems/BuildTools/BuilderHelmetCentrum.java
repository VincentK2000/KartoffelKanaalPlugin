package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.RenewableFile;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.logging.Logger;

/**
 * Created by Vincent on 10-Jan-15.
 */

public class BuilderHelmetCentrum implements Listener{
    protected BuilderHelmet[] registeredBuilderHelmets = new BuilderHelmet[32];

    private BuilderHelmetsLoader loader;
    private BuilderHelmetsSaver saver;

    protected final RenewableFile res;
    protected boolean changed = false;

    protected byte[] header;

    protected static final byte VersionA = 0x00;
    protected static final byte VersionB = 0x00;

    private BuildToolsService parent;

    public BuilderHelmetCentrum(BuildToolsService parent) throws Exception{
        this.parent = parent;
        try {
            this.res = new RenewableFile(Main.plugin.keypaths[6], 7, "builderHelmets", Main.plugin.keypaths[7], VersionA, VersionB);
        }catch(Exception e){
            throw new Exception("Kon RenewableFile resource niet initializen: " + e.toString(), e);
        }
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }


    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e){
        if(this.denyBuilding(e.getPlayer(), e.getBlock().getWorld().getName(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))e.setCancelled(true);
    }
    @EventHandler
    public void onBlockBuild(org.bukkit.event.block.BlockPlaceEvent e){
        if(this.denyBuilding(e.getPlayer(), e.getBlock().getWorld().getName(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerBucket(org.bukkit.event.player.PlayerBucketEvent e){
        if(this.denyBuilding(e.getPlayer(), e.getBlockClicked().getWorld().getName(), e.getBlockClicked().getX(), e.getBlockClicked().getY(), e.getBlockClicked().getZ()))e.setCancelled(true);
    }

    public boolean denyBuilding(Player p, String worldName, int x, int y, int z){
        if(p == null || worldName == null)return true;
        if(p.getGameMode() == GameMode.CREATIVE && !p.isOp()){
            BuilderHelmet builderHelmet = this.getBuilderHelmetByID(getBuilderHelmetID(p.getInventory().getHelmet()));
            if(builderHelmet == null)return false;
            if(!builderHelmet.hasAccessToBuilderCrea())return true;
            return !builderHelmet.isInRegion(worldName, x, y, z);
        }else {
            return false;
        }
    }


    @EventHandler
    public void onPlayerDropItem(org.bukkit.event.player.PlayerDropItemEvent e){
        if(this.denyInteraction(e.getPlayer()))e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent e){
        if(this.denyInteraction(e.getPlayer()))e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(org.bukkit.event.player.PlayerInteractEntityEvent e){
        if(this.denyInteraction(e.getPlayer()))e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteractEvent(org.bukkit.event.inventory.InventoryInteractEvent e){
        if(e.getInventory() != e.getWhoClicked().getInventory() && !e.getWhoClicked().isOp() && this.denyInteraction(e.getWhoClicked()))e.setCancelled(true);
    }

    public boolean denyInteraction(HumanEntity p){
        if(p == null)return true;
        if(p.getGameMode() == GameMode.CREATIVE && !p.isOp()){
            BuilderHelmet builderHelmet = this.getBuilderHelmetByID(getBuilderHelmetID(p.getInventory().getHelmet()));
            if(builderHelmet == null)return false;
            return builderHelmet.hasAccessToBuilderCrea();
        }else{
            return false;
        }
    }

    @EventHandler
    public void onInventoryClickEvent(org.bukkit.event.inventory.InventoryClickEvent e){
        InventoryAction action = e.getAction();
        if(action == InventoryAction.DROP_ALL_CURSOR || action == InventoryAction.COLLECT_TO_CURSOR)
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e){
        this.changeToSurvival(e.getPlayer());
    }
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e){
        this.changeToSurvival(e.getPlayer());
    }

    public void changeToSurvival(Player p){
        if(p.getGameMode() == GameMode.CREATIVE){
            ItemStack saveItem = null;
            PlayerInventory inv = p.getInventory();
            if(getBuilderHelmetID(inv.getHelmet()) != -1)saveItem = inv.getHelmet();
            inv.clear();
            p.setGameMode(GameMode.SURVIVAL);
            inv.clear();
            inv.setHeldItemSlot(1);
            inv.setItem(0, saveItem);
        }
    }

    public boolean builderCreaAllowed(Player p){
        if(p == null)return false;
        BuilderHelmet builderHelmet = this.getBuilderHelmetByID(getBuilderHelmetID(p.getInventory().getHelmet()));
        if(builderHelmet == null)return false;
        return builderHelmet.hasAccessToBuilderCrea();
    }

    public int getBuilderHelmetID(ItemStack s) {
        if (s == null || s.getType() != org.bukkit.Material.LEATHER_HELMET || s.getAmount() == 0 || !s.hasItemMeta())return -1;
        LeatherArmorMeta itemMeta;
        try {
            itemMeta = (LeatherArmorMeta) s.getItemMeta();
        }catch(Exception e){return -1;}
        if(!itemMeta.getDisplayName().equals("§2BuilderHelmet"))return -1;
        int c = itemMeta.getColor().asRGB();
        if((c & 0xFFFFE0) == 0xFEFE00){
            return c & 0x00001F;
        }else{
            return -1;
        }
    }

    public BuilderHelmet getBuilderHelmetByID(int id){
        if(id < 0 || id >= this.registeredBuilderHelmets.length)return null;
        return this.registeredBuilderHelmets[id];
    }

    protected int getAvailableHelmetID(){
        for(int i = 0; i < this.registeredBuilderHelmets.length; i++){
            if(this.registeredBuilderHelmets[i] == null)return i;
        }
        for(int i = 0; i < this.registeredBuilderHelmets.length; i++){
            if(this.registeredBuilderHelmets[i].isExpired()){
                this.registeredBuilderHelmets[i] = null;
                return i;
            }
        }
        return 0;
    }

    public void loadBuilderHelmets(){
        if(this.parent.preventAction())return;
        this._loadBuilderHelmets();
    }

    public void _loadBuilderHelmets() {
        if (loader == null) loader = new BuilderHelmetsLoader(this);
        loader.loadFile();
    }

    protected void Save(boolean checkChanged){
        if(checkChanged && !this.isChanged())return;
        if(saver == null)saver = new BuilderHelmetsSaver(this);
        saver.Save();
    }

    protected void SaveBlocking(boolean checkChanged){
        if(checkChanged && !this.isChanged()){
            Logger.getLogger("Minecraft").info("[KKP] BuilderHelmetsFile wordt niet bewaard omdat er geen veranderingen waren");
            return;
        }
        if(saver == null)saver = new BuilderHelmetsSaver(this);
        Logger.getLogger("Minecraft").info("[KKP] BuilderHelmetsFile bewaren...");
        saver.SaveBlocking();
        Logger.getLogger("Minecraft").info("[KKP] BuilderHelmetsFile bewaard");
    }

    public boolean isChanged(){
        return this.changed;
    }

    protected void notifyChange(){
        this.changed = true;
    }
}
