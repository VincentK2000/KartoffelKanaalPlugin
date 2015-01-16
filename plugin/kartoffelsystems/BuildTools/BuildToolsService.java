package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.KartoffelService;

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
            buildHelmCentr = new BuilderHelmetCentrum(this);
            buildHelmCentr.loadBuilderHelmets();
        }catch(Exception e){
            Logger.getLogger("Minecraft").warning("[KKP] Kon het BuilderHelmetCentrum niet opstarten:");
            e.printStackTrace();
        }
    }

    @Override
    protected void _disableCore() throws Exception {
        try{
            this.buildHelmCentr.SaveBlocking(true);
        }catch(Exception ex){
            Logger.getLogger("Minecraft").warning("[KKP] Kon BuilderHelmetsFile niet bewaren:");
            ex.printStackTrace();
        }
    }

    public static String parseArrayToString(String[] list,  String separator){
        if(list == null || list.length == 0 || separator == null)return "";
        StringBuilder sb = new StringBuilder(20);
        for(int i = 0; i < list.length - 1; i++){
            String s = list[i];
            if(s == null || s.length() == 0)continue;
            sb.append(separator);
        }
        String last = list[list.length - 1];
        if(last != null){
            sb.append(last);
        }
        return sb.toString();
    }
}
