package KartoffelKanaalPlugin.plugin;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandsKartoffel {
	public static void executeKartoffelCommand(Person p, CommandSender a, String[] args){		
		if(!a.isOp() && !(a instanceof Player && Main.isDeveloper(((Player)a).getUniqueId()) && DebugTools.developermodus)){
			a.sendMessage("§4Je hebt geen permission om het Kartoffel-commando te gebruiken");
			return;
		}
		if(args.length == 0){
			a.sendMessage("§c/kartoffel <autoantilag|playersystem|pulsersystem>");
			return;
		}
		String search = args[0].toLowerCase();
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		if(search.equals("autoantilag")){
			CommandsKartoffel.executeKartoffelAutoAntilagCommand(p, a, newArgs);
		}else if(search.equals("playersystem")){
			CommandsKartoffel.executeKartoffelPlayerSystemCommand(p, a, newArgs);
		}else if(search.equals("pulsersystem")){
			CommandsKartoffel.executeKartoffelPulserSystemCommand(p, a, newArgs);
		}else if(search.equals("hallo")){
			if(a instanceof Player){
				Player pl = (Player) a;
				pl.sendMessage("\"Welkom A\"");
				pl.sendRawMessage("\"Welkom B\"");
				pl.sendMessage("Welkom C");
				pl.sendRawMessage("Welkom D");
			}
		}else if(search.equals("printloadedplayers")){
			if(Main.pm == null){
				a.sendMessage("§4PlayerManager is null");
				return;
			}
			Main.pm._debugPrintLoadedPlayers();
			if(!(a instanceof ConsoleCommandSender)){
				a.sendMessage("§eZie Console voor de debugPrint");
			}
		}else if(search.equals("printdatafield")){
			if(args.length != 2){
				p.sendMessage("§c/kartoffel printDataField <name>");
				return;
			}
			String dataFieldName = args[1].toLowerCase();
			if(dataFieldName.equals("timesticked")){
				Main.pulser.timesTicked._printDebug();
			}else{
				p.sendMessage("§4Unknown DataField");
				return;
			}
		}else if(search.equals("displayname")){
			Player pl;
			if(a instanceof Player){
				pl = (Player) a;
			}else{
				Player[] plList = Main.plugin.getServer().getOnlinePlayers().toArray(new Player[Main.plugin.getServer().getOnlinePlayers().size()]);
				if(plList == null || plList.length == 0){
					a.sendMessage("§4De PlayerList is null");
					return;
				}
				if(plList[0] == null){
					a.sendMessage("§4De eerste Player is null");
					return;
				}
				pl = plList[0];
			}
			if(args.length < 2){
				p.sendMessage("DisplayName: " + pl.getDisplayName());
			}else{
				StringBuilder sb = new StringBuilder();
				for(int i = 1; i < args.length; i++){
					sb.append(args[i]);
				}
				pl.setCustomName(sb.toString());
				pl.sendMessage("DisplayName = " + pl.getDisplayName());
			}
		}else{
			a.sendMessage("§c/kartoffel <autoantilag|playersystem|pulsersystem>");
		}
	}
	
	public static void executeKartoffelAutoAntilagCommand(Person p, CommandSender a, String[] args){
		
	}
	
	public static void executeKartoffelPlayerSystemCommand(Person p, CommandSender a, String[] args){
		
	}

	public static void executeKartoffelPulserSystemCommand(Person p, CommandSender a, String[] args){
		
	}
}
