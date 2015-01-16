package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem.Pulser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

/**
 * Created by Vincent on 10-Jan-15.
 */
public class BuilderHelmetsSaver implements Runnable{
    private BuilderHelmetCentrum parent;
    private Thread saveThread;

    protected BuilderHelmetsSaver(BuilderHelmetCentrum parent){
        this.parent = parent;
    }

    @Override
    public void run(){
        this.SaveBlocking();
    }

    protected void Save(){
        if(this.parent == null)return;
        Thread a = new Thread(this);
        a.start();
    }

    protected void SaveBlocking(){
        if(this.parent == null || this.parent.res == null)return;
        if(this.saveThread != null && this.saveThread.isAlive())return;
        long time = System.currentTimeMillis();//Tijd wordt vroeg opgeschreven om zeker te zijn
        this.saveThread = Thread.currentThread();
        int edition = this.parent.res.getNewFileVersion();
        File f;
        try{
            f = this.parent.res.acquireWriteFile(edition, "");
            f.createNewFile();
            f.mkdirs();
        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] BuilderHelmetsSaver: Kon nieuw bestand voor bewaren niet aanmaken (" + (e == null?"null":e) + ")");
            return;
        }

        FileOutputStream fos;
        try{
            fos = new FileOutputStream(f);
        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] BuilderHelmetsSaver: Kan FileOutputStream niet openen voor BuilderHelmetsFile");
            return;
        }
        try{
            {
                byte[] header = this.parent.header;
                if(header == null || header.length != 16){
                    header = new byte[16];
                }
                header[0] = Pulser.VersionA;
                header[1] = Pulser.VersionB;

                header[2] = (byte) ((edition >>> 24) & 0xFF);
                header[3] = (byte) ((edition >>> 16) & 0xFF);
                header[4] = (byte) ((edition >>>  8) & 0xFF);
                header[5] = (byte) ( edition         & 0xFF);
                fos.write(header);
            }







            this.parent.res.writeFileFinished(edition);
        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] Kon de BuilderHelmetsFile niet bewaren (" + (e == null?"null":e) + ")");
            e.printStackTrace();
        }

        try{
            fos.close();
        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] Kon een FileOutputStream bij de BuilderHelmetsSaver niet sluiten");
        }
        this.parent.changed = false;
        //if(this.parent.lastSaveTime < time)this.parent.lastSaveTime = time;

        //System.out.println("BuilderHelmetsFile bewaard");
    }

}
