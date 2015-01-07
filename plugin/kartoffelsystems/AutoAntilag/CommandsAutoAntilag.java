package KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag;

import java.util.Calendar;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.SettingsManager;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class CommandsAutoAntilag {
	public static void executeAutoAntilagCommand(Person executor, CommandSender sender, String[] args){
		if(sender == null || args == null)return;
		if(!sender.isOp() || (executor != null && executor.getSpelerOptions().getOpStatus() < 2 && !executor.getSpelerOptions().getSwitches((byte)18,(byte)0x80))){
			return;
		}
		
		if(Main.aa == null){
			sender.sendMessage("§4AutoAntilag is niet bereikbaar");
			return;
		}
		if(sender.isOp() || (executor != null && (executor.getSpelerOptions().getOpStatus() >= 2 || executor.getSpelerOptions().getSwitches((byte)18,(byte)0x80)))){
			if(args.length == 0){
				sender.sendMessage("AutoAntilag = " + (Main.aa.isRunning()?"aan":"uit"));
				sender.sendMessage("Timeout = " + Main.aa.getTimeout() / 60000 + " min. (" + Main.aa.getTimeout() + " ms)");
				if(Main.aa.isRunning()){
					sender.sendMessage("Volgende uitv. over " + ((Main.aa.getEndtime() - System.currentTimeMillis()) / 60000) + " min. (geschat)");
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(Main.aa.getEndtime());
					sender.sendMessage("Volgende uitv. om " + c.get(Calendar.DAY_OF_MONTH) + ' ' + c.get(Calendar.LONG) + ' ' + c.get(Calendar.YEAR) + " | " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
				}
			}else if(args.length == 1){
				if(args[0].equalsIgnoreCase("help")){
					sender.sendMessage("§c/autoantilag §f: Geeft info over de status van auto antilag");
					sender.sendMessage("§c/autoantilag <aan|uit|0> §f: Verander de status van auto antilag");
					sender.sendMessage("§c/autoantilag <aantal minuten> §f: Verander de timeout voor het nogmaals wordt uitgevoerd");
				}else if(args[0].equalsIgnoreCase("aan")){
					SettingsManager.EnableAutoAntilag();
					if(Main.aa.isRunning()){
						sender.sendMessage("AutoAntilag is nu ingeschakeld");
					}else{
						sender.sendMessage("AutoAntilag kon kennelijk niet ingeschakeld worden");
					}
					sender.sendMessage("AutoAntilag is nu ingeschakeld");
				}else if(args[0].equalsIgnoreCase("uit")){
					SettingsManager.DisableAutoAntilag();
					if(!Main.aa.isRunning()){
						sender.sendMessage("AutoAntilag is nu uitgeschakeld");
					}else{
						sender.sendMessage("AutoAntilag kon kennelijk niet uitgeschakeld worden");
					}
				}else{
					int i;
					try{
						i = Integer.parseInt(args[0]);
					}catch(NumberFormatException e){
						sender.sendMessage("Onjuiste parameter, probeer §c/autoantilag help§f");
						return;
					}
					Main.aa.setTimeout(i, sender);
				}
			}
		}else{
			sender.sendMessage("§4Je hebt geen toegang om het AutoAntilag-commando te gebruiken");
		}
	}
}
