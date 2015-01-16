package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import KartoffelKanaalPlugin.plugin.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Vincent on 10-Jan-15.
 */
public class BuilderHelmet {
    private int ID;

    private String worldName;
    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

    private UUID[] uuids;

    private short accesses = 0x0000;
    private long expireTime = -1;
    //1 bit: BuilderCrea allowed
    //15 bits: ~leeg~

    protected BuilderHelmet(String worldName, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, short accesses, UUID[] peopleWithAccess) throws NullPointerException {
        this.worldName = worldName;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.accesses = accesses;
        this.uuids = peopleWithAccess;
        if(this.uuids == null)this.uuids = new UUID[0];
        this.ID = Main.bt.buildHelmCentr.getAvailableHelmetID();
        Main.bt.buildHelmCentr.registeredBuilderHelmets[this.ID] = this;
    }

    public boolean isInRegion(String worldName, int x, int y, int z){
        if(this.worldName == null || worldName == null)return false;
        return this.worldName.equals(worldName) && x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public int getID(){
        return this.ID;
    }

    public short getAccesses(){
        return this.accesses;
    }
    public boolean hasAccessToBuilderCrea(){return !this.isExpired() && ((accesses & 0x8000) == 0x8000);}
    protected void setAccessToBuilderCrea(boolean v){if(v)accesses |= 0x8000 else accesses &= 0x7FFF;}

    protected boolean isExpired(){
        return this.expireTime > 0 && System.currentTimeMillis() > this.expireTime;
    }
    public void addAccessor(UUID id){
        if(id == null)return;;
        for(int i = 0; i < this.uuids.length; i++){
            if(this.uuids[i] != null && this.uuids[i].equals(id))return;
        }
        UUID[] newUUIDs = new UUID[this.uuids.length + 1];
        System.arraycopy(this.uuids, 0, newUUIDs, 0, this.uuids.length);
        newUUIDs[newUUIDs.length - 1] = id;
        this.uuids = newUUIDs;
    }
    public void addAccessor(String name) throws Exception {
        if(name == null || name.length() == 0)return;
        if(Main.pm == null || Main.pm.preventAction())throw new Exception("Kan geen persoon toevoegen via naam zolang de PlayerManager niet aan staat");
        KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person p = Main.pm.getPlayer(name);
        if(p == null)throw new Exception("Persoon niet gevonden");
        this.addAccessor(p.getUUID());
    }

    public void updateWithWorldEditSelection(Player p) throws Exception{
        if(p == null)throw new NullPointerException("De meegegeven speler voor de region is null");
        com.sk89q.worldedit.LocalSession localSession = com.sk89q.worldedit.WorldEdit.getInstance().getSession(p.getName());
        com.sk89q.worldedit.regions.Region selection = localSession.getSelection(localSession.getSelectionWorld());
        this.worldName = selection.getWorld().getName();
        this.minX = selection.getMinimumPoint().getBlockX();
        this.minY = selection.getMinimumPoint().getBlockY();
        this.minZ = selection.getMinimumPoint().getBlockZ();

        this.maxX = selection.getMaximumPoint().getBlockX();
        this.maxY = selection.getMaximumPoint().getBlockY();
        this.maxZ = selection.getMaximumPoint().getBlockZ();
    }

    public ItemStack updateItemInformation(ItemStack s){
        if (s == null || s.getType() != org.bukkit.Material.LEATHER_HELMET || !s.hasItemMeta()){
            s = new ItemStack(Material.LEATHER_HELMET, 1);
        }

        LeatherArmorMeta leatherArmorMeta;
        try {
            leatherArmorMeta = (LeatherArmorMeta)s.getItemMeta();
        }catch(Exception e){
            return s;
        }
        leatherArmorMeta.setDisplayName("§2BuilderHelmet");
        leatherArmorMeta.setColor(org.bukkit.Color.fromRGB(0xFEFE00 | ID));

        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§eKKP BuildTools.BuilderHelmet");
        lore.add("Bouwterrein:");
        lore.add("    Wereld: " + this.worldName);
        lore.add("    MinCoords: " + this.minX + ", " + this.minY + ", " + this.minZ);
        lore.add("    MaxCoords: " + this.maxX + ", " + this.maxY + ", " + this.maxZ);
        String[] playerNames = new String[this.uuids.length];
        org.bukkit.Server server = Main.plugin.getServer();
        for(int i = 0; i < this.uuids.length; i++){
            try {
                playerNames[i] = this.uuids[i].toString();
            }catch(Exception e){
                playerNames[i] = "";
            }
        }
        lore.add("Uitverleend aan:");
        lore.add(BuildToolsService.parseArrayToString(playerNames, ", "));
        lore.add("Toegang:");
        if(this.hasAccessToBuilderCrea())lore.add("    - BuilderCrea");
        lore.add("Geldigheid: " + (this.isExpired()?"§4Verlopen":((this.expireTime <= 0)?"§2Geen automatisch verloping":("§6Verloopt op " + (new Date(this.expireTime).toString()))));

        leatherArmorMeta.setLore(lore);

        s.setItemMeta(leatherArmorMeta);
        return s;
    }


    public static BuilderHelmet createCreaBuilderHelmetFromWorldEditSelection(Player p, UUID[] accessors) throws Exception{
        if(p == null)throw new NullPointerException("De meegegeven speler voor de region is null");
        com.sk89q.worldedit.LocalSession localSession = com.sk89q.worldedit.WorldEdit.getInstance().getSession(p.getName());
        com.sk89q.worldedit.regions.Region selection = localSession.getSelection(localSession.getSelectionWorld());
        String name = selection.getWorld().getName();
        int minX = selection.getMinimumPoint().getBlockX();
        int minY = selection.getMinimumPoint().getBlockY();
        int minZ = selection.getMinimumPoint().getBlockZ();

        int maxX = selection.getMaximumPoint().getBlockX();
        int maxY = selection.getMaximumPoint().getBlockY();
        int maxZ = selection.getMaximumPoint().getBlockZ();

        return new BuilderHelmet(name, minX, minY, minZ, maxX, maxY, maxZ, (short)0x8000,accessors);
    }
}
