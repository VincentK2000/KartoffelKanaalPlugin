package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.KartoffelService;
import sun.rmi.runtime.Log;

import java.util.logging.Logger;

/**
 * Created by Vincent on 10-Jan-15.
 */
public class BuildToolsService extends KartoffelService {
    protected BuilderHelmetCentrum buildHelmCentr;

    public BuildToolsService(){
        super("BuildTools");
    }

    public void initialize(){
        if(this.initialized)return;

        if(this.running){
            Logger.getLogger("Minecraft").info("[KKP] Kan de BuildTools niet initializen als die aan staat");
            return;
        }
        this.initialized = true;
    }

    @Override
    protected void _enableCore() throws Exception {
        if(!this.initialized)throw new Exception("BuildTools is niet initialized");
        try{
            buildHelmCentr = new BuilderHelmetCentrum();
            buildHelmCentr.lo
        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] Kon het BuilderHelmetCentrum niet opstarten:");
            e.printStackTrace();
        }
    }

    @Override
    protected void _disableCore() throws Exception {

    }
}
