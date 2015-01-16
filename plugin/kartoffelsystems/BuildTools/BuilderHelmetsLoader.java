package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import KartoffelKanaalPlugin.plugin.SecureBestand;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

/**
 * Created by Vincent on 10-Jan-15.
 */
public class BuilderHelmetsLoader {
    BuilderHelmetCentrum parent;

    protected BuilderHelmetsLoader(BuilderHelmetCentrum parent){
        this.parent = parent;
        if(parent == null)throw new IllegalArgumentException("BuilderHelmetCentrum is null");
    }

    protected void loadFile() {
        //System.out.println("Pulser File laden...");
        if (this.parent == null || this.parent.res == null || this.parent.res.getResource() == null)
            throw new IllegalArgumentException("BuilderHelmetCentrum of de resource daarvan is null");
        SecureBestand res = this.parent.res.getResource();
        res.sessionSys.acquireAccess();

        File f = null;
        try{
            f = res.getFile();
        }catch(Exception e){
            if(this.parent.res != null && this.parent.res.getResource() != null)this.parent.res.getResource().markForBackup();
            Logger.getLogger("Minecraft").warning("[KKP] Kon BuilderHelmetsFile niet krijgen van de res");
            res.sessionSys.releaseAccess();
            return;
        }
        if(f == null || !f.exists()){
            Logger.getLogger("Minecraft").warning("[KKP] De BuilderHelmetsFile lijkt niet te bestaan...");
            this.parent.notifyChange();
            res.sessionSys.releaseAccess();
            return;
        }

        FileInputStream fis;
        try{
            fis = new FileInputStream(f);
        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] Kon de InputStream naar het BuilderHelmetsFile niet openen in de BuilderHelmetsLoader");
            res.sessionSys.releaseAccess();
            return;
        }
        boolean closed = false;
        try{
            if(fis.available() < 16){
                fis.close();
                closed = true;
                throw new Exception("Er was onvoldoende data om de header in te lezen");
            }
            byte[] header = new byte[16];
            fis.read(header);
            parent.header = header;
            if(header[0] != BuilderHelmetCentrum.VersionA || header[1] != BuilderHelmetCentrum.VersionB){
                Logger.getLogger("Minecraft").warning("[KKP] De version van de gegeven BuilderHelmetsFile klopt niet. Veranderingen zullen niet worden aangebracht in de BuilderHelmetsFile");
                fis.close();
                closed = true;
                throw new Exception("De versie is niet ondersteund");
            }



        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] Kon het BuilderHelmetsFile niet inladen: " + e);
        }


        try{
            fis.close();
        }catch(Exception e){
            if(!closed){
                Logger.getLogger("Minecraft").warning("[KKP] Kon de FileInputStream niet sluiten, hierdoor kan mogelijk corruptie voorkomen");
            }
        }
        res.sessionSys.releaseAccess();
    }

}
