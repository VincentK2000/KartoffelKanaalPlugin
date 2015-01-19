package KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag;

import KartoffelKanaalPlugin.plugin.CommandSyntaxException;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import org.bukkit.command.CommandSender;

import java.util.Calendar;

public class CommandsAutoAntilag {
	public static void executeAutoAntilagCommand(Person executor, CommandSender sender, String[] args){
		if(sender == null || args == null)return;
		if(!(sender.isOp() || (executor != null && executor.getSpelerOptions().getSwitches((byte)18,(byte)0x80)))){
			sender.sendMessage("§4Je hebt geen toegang om het AutoAntilag-commando te gebruiken");
			return;
		}
		if(Main.aa == null || Main.aa.preventAction()){
			sender.sendMessage("§4AutoAntilag is niet beschikbaar");
			return;
		}

		if(args.length == 0){
			sender.sendMessage("§e------ AutoAntilag Info ------");

			if(Main.aa.firstThread == null) {
				sender.sendMessage("§e    Eerste AutoAntilagLoop is niet aangemaakt");
			}else{
				sender.sendMessage("§e    Eerste AutoAntilagLoop:");
				sender.sendMessage("§e        Status: " + (Main.aa.firstThread.isRunning()?"actief":"inactief"));
				if(Main.aa.firstThread.isRunning()) {
					sender.sendMessage("§e        Timeout = " + (Main.aa.firstThread.getTimeout() / 60000) + " min. (" + Main.aa.firstThread.getTimeout() + " ms)");
					long timeLeft = Main.aa.firstThread.getEndtime() - System.currentTimeMillis();
					sender.sendMessage("§e        Volg. uitv. over ongeveer " + (timeLeft / 60000) + " min. en " + ((timeLeft % 60000) / 1000) + " sec.");
					if(Main.sm != null){
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(Main.sm.convertToClientTime(Main.aa.firstThread.getEndtime()));
						sender.sendMessage("§e        Volg. uitv. op ongeveer " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
					}
				}
			}

			if(Main.aa.secondThread == null) {
				sender.sendMessage("§e    Tweede AutoAntilagLoop is niet aangemaakt");
			}else{
				sender.sendMessage("§e    Tweede AutoAntilagLoop:");
				sender.sendMessage("§e        Status: " + (Main.aa.secondThread.isRunning()?"actief":"inactief"));
				if(Main.aa.secondThread.isRunning()) {
					sender.sendMessage("§e        Timeout = " + (Main.aa.secondThread.getTimeout() / 60000) + " min. (" + Main.aa.secondThread.getTimeout() + " ms)");
					long timeLeft = Main.aa.secondThread.getEndtime() - System.currentTimeMillis();
					sender.sendMessage("§e        Volg. uitv. over ongeveer " + (timeLeft / 60000) + " min. en " + ((timeLeft % 60000) / 1000) + " sec.");
					if(Main.sm != null){
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(Main.sm.convertToClientTime(Main.aa.secondThread.getEndtime()));
						sender.sendMessage("§e        Volg. uitv. op ongeveer " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
					}
				}
			}
			sender.sendMessage("§e------------------------------");
		}else{
			try {
				if(args.length < 2)throw new CommandSyntaxException();
				int newTimeout;
				try {
					newTimeout = Integer.parseInt(args[0]);
				} catch (Exception e) {
					throw new CommandSyntaxException();
				}
				if(args[1].equals("*")){
					if(args.length != 2)throw new CommandSyntaxException();
					Main.aa.firstThread.setTimeout(newTimeout, sender);
					Main.aa.secondThread.setTimeout(newTimeout, sender);
				}else if(args[1].equalsIgnoreCase("eerste")){
					if(args.length != 2)throw new CommandSyntaxException();
					Main.aa.firstThread.setTimeout(newTimeout, sender);
				}else if(args[1].equalsIgnoreCase("tweede")){
					if(args.length != 2)throw new CommandSyntaxException();
					Main.aa.secondThread.setTimeout(newTimeout, sender);
				}else{
					String cmd;
					{
						StringBuilder sb = new StringBuilder();
						for(int i = 1; i < args.length - 1; i++){
							sb.append(args[i]);
							sb.append(' ');
						}
						sb.append(args[args.length - 1]);
						cmd = sb.toString();
					}
					boolean foundInFirst = Main.aa.firstThread != null && Main.aa.firstThread.containsCommand(cmd);
					boolean foundInSecond = Main.aa.secondThread != null && Main.aa.secondThread.containsCommand(cmd);
					if(foundInFirst && foundInSecond){
						sender.sendMessage("§4Het commando is in beide loops gevonden, specifiëer met \"eerste\", \"tweede\" of -indien u ze allebei wilt veranderen- \"*\"");
					}else if(foundInFirst){
						Main.aa.firstThread.setTimeout(newTimeout, sender);
					}else if(foundInSecond){
						Main.aa.secondThread.setTimeout(newTimeout, sender);
					}else{
						sender.sendMessage("§4Het commando \"" + cmd + "\" is in geen enkele van de loops gevonden");
						sender.sendMessage("§eGebruik van het commando:");
						throw new CommandSyntaxException();
					}
				}
			}catch(CommandSyntaxException e){
				sender.sendMessage("§c/autoantilag [nieuwe timeout in min.] <eerste|tweede|*|commando>");
				sender.sendMessage("§eAan: §c/autoantilag <timeout> <eerste|tweede|*|commando>");
				sender.sendMessage("§eUit: §c/autoantilag 0 <eerste|tweede|*|commando>");
				return;
			}
		}
	}
}
