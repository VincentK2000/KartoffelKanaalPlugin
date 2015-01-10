package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import com.sun.javafx.scene.layout.region.BackgroundImage;

import java.awt.image.ShortLookupTable;

/**
 * Created by Vincent on 10-Jan-15.
 */
public abstract class BuilderHelmet {
    private int ID;

    private String worldName;
    private int minX;
    private byte minY;
    private int minZ;

    private int maxX;
    private byte maxY;
    private int maxZ;

    private short accesses = 0x00;

    protected BuilderHelmet(int ID, String worldName, int minX, byte minY, int minZ, int maxX, byte maxY, int maxZ, short accesses){
        this.ID = ID;
        this.worldName = worldName;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.accesses = accesses;
    }

    public int getID(){
        return this.ID;
    }

    public short getAccesses(){
        return this.accesses;
    }

    public short updateItemInformation(){

    }
}
