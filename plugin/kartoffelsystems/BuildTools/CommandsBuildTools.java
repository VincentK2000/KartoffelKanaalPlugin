package KartoffelKanaalPlugin.plugin.kartoffelsystems.BuildTools;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Vincent on 11-Jan-15.
 */
public class CommandsBuildTools {


    public static void executeBuildHelmetCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args){
        if(!a.isOp()){
            a.sendMessage("§4Je hebt geen toegang om dit commando uit te voeren");
            return;
        }
        if(Main.bt == null || Main.bt.preventAction()){
            a.sendMessage("§4De BuildTools-service is niet beschikbaar");
            return;
        }
        if(Main.bt.buildHelmCentr == null){
            a.sendMessage("§4Het BuildHelmetCentrum is niet beschikbaar");
        }
        if(args.length == 0){
            a.sendMessage("§c/buildhelmet <info|define|redefine|remove|addowner|removeowner|clear> <...>");
            return;
        }
        args[0] = args[0].toLowerCase();
        if(args[0].equals("info")){
            if(args.length != 2){
                a.sendMessage("§c/buildhelmet info <hand|here|ID>");
                return;
            }

            BuilderHelmet helm = null;

            args[1] = args[1].toLowerCase();
            if(args[1].equals("hand")){
                Player pl;
                if(a instanceof Player){
                    pl = (Player) a;
                }else{
                    a.sendMessage("§4Je moet een Player zijn om deze functie uit te kunnen voeren.");
                    return;
                }
                int ID = Main.bt.buildHelmCentr.getBuilderHelmetID(pl.getItemInHand());
                if(ID < 0){
                    a.sendMessage("§4Het item in je hand is niet herkend als correcte BuildHelmet");
                    return;
                }
                a.sendMessage("§eID van de BuildHelmet volgens het item: " + ID);
                helm = Main.bt.buildHelmCentr.getBuilderHelmetByID(ID);
            }else if(args[1].equals("here")) {
                Player pl;
                if (a instanceof Player) {
                    pl = (Player) a;
                } else {
                    a.sendMessage("§4Je moet een Player zijn om deze functie uit te kunnen voeren.");
                    return;
                }
                BuilderHelmet[] arr = Main.bt.buildHelmCentr.registeredBuilderHelmets;
                String worldName = pl.getWorld().getName();
                int x = pl.getLocation().getBlockX();
                int y = pl.getLocation().getBlockY();
                int z = pl.getLocation().getBlockZ();
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] != null && arr[i].isInRegion(worldName, x, y, z)) {
                        helm = arr[i];
                        a.sendMessage("§eMomenteel is enkel het vinden van een BuildHelmets tegelijk ondersteund");
                        break;
                    }
                }
                a.sendMessage("§eKon geen BuildHelmet voor die locatie");
            }else{
                try{
                    int id = Integer.parseInt(args[1]);
                    helm = Main.bt.buildHelmCentr.registeredBuilderHelmets[id];
                }catch (Exception e){
                    a.sendMessage("§c/buildhelmet info <hand|here|ID>");
                    return;
                }
            }

            if(helm == null){
                a.sendMessage("§eKon geen informatie vinden");
            }else{
                a.sendMessage("§eID van de BuildHelmet: " + helm.getID());
                a.sendMessage("§4Tonen van verdere BuildHelmet informatie is nog niet ondersteund");
            }
        }else if(args[0].equals("define")){
            Player pl;
            if(a instanceof Player){
                pl = (Player) a;
            }else{
                a.sendMessage("§4Je moet een Player zijn om deze functie uit te kunnen voeren.");
                return;
            }
            if(args.length != 2){
                a.sendMessage("§c/buildhelmet define <type>");
                a.sendMessage("§eMogelijke type's: \"CreaBuild\"");
                return;
            }
            args[1] = args[1].toLowerCase();
            if(args[1].equals("creabuild")){
                BuilderHelmet helmet;
                try {
                    helmet = BuilderHelmet.createCreaBuilderHelmetFromWorldEditSelection(pl, null);
                    if(helmet == null)throw new NullPointerException("De beantwoordde BuildHelmet is leeg");
                }catch(Exception e){
                    a.sendMessage("§4Kon geen CreaBuildHelmet aanmaken: " + e.getMessage());
                    return;
                }
                a.sendMessage("§eEen BuildHelmet is aangemaakt a.d.v. de WorldEdit region met ID " + helmet.getID());
            }else{
                a.sendMessage("§c/buildhelmet define <type> [mensen met toegang]");
                a.sendMessage("§eMogelijke type's: \"CreaBuild\"");
            }
        }
    }
}
