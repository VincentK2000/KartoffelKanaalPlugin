package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.Main;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandsPlayerSystem {
	public static void executeRankCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args){
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		//Mag crashen als er informatie fout is want dan moet het een code-fout zijn	
		if(args.length < 1 || args.length > 2){
			a.sendMessage("§c/rank <speler> [nieuwe rank]");
			return;
		}
		if(executor.getSpelerOptions().getRank() < 1){
			a.sendMessage("§4Je moet minimum de rank \"Player\" hebben om dit commando te gebruiken");
			return;
		}
		Person p;
		if(executor.getSpelerOptions().getRank() >= 70){
			p = Main.pm.getPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in alle spelers");
				return;
			}
		}else{
			p = Main.pm.getLoadedPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in de ingeladen spelers");
				return;
			}
		}
		p.sessionSys.acquireAccess();
		
		if(args.length == 1){			
			byte r = p.getSpelerOptions().getRank();
			String prefix = Rank.getRankPrefix(r);
			a.sendMessage("§eDe rank van " + args[0] + " is " + Rank.getRankDisplay(r) + ((prefix == null || prefix.length() == 0)?"":" en de Rank-Prefix is " + prefix));
		}else if(args.length == 2){
			boolean silent = (executor.getSpelerOptions().getPermissionLevel() > p.getSpelerOptions().getPermissionLevel() &&
					executor.getSpelerOptions().getRank() >= p.getSpelerOptions().getRank() && attribSys.hasAttrib("silent"));
			byte r = Rank.getRank(args[1]);
			p.getSpelerOptions().setRank(r, executor.getSpelerOptions(), a, !silent);
		}
		
		p.sessionSys.releaseAccess();
		
	}
	
	public static void executeDonateurRankCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args){
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		if(args.length < 1 || args.length > 2){
			a.sendMessage("§c/donateurrank <speler> [nieuwe donateurrank]");
			return;
		}
		
		if(executor.getSpelerOptions().getRank() < 1){
			a.sendMessage("§4Je moet minimum de rank \"Player\" hebben om dit commando te gebruiken");
			return;
		}
		
		Person p;
		if(executor.getSpelerOptions().getRank() >= 70){
			p = Main.pm.getPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in alle spelers");
				return;
			}
		}else{
			p = Main.pm.getLoadedPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in de ingeladen spelers");
				return;
			}
		}
		p.sessionSys.acquireAccess();
		
		if(args.length == 1){
			byte r = p.getSpelerOptions().getDonatorRank();
			if(r == -127){
				a.sendMessage("§eDe speler \"" + args[0] + "\" heeft geen donateurrank");
			}else{
				a.sendMessage("§eDe donateurrank van \"" + args[0] + "\" is " + Rank.getRankDisplay(r));
			}
		}else if(args.length == 2){
			boolean silent = (executor.getSpelerOptions().getPermissionLevel() > p.getSpelerOptions().getPermissionLevel() &&
					executor.getSpelerOptions().getRank() >= p.getSpelerOptions().getRank() && attribSys.hasAttrib("silent"));
			byte r = Rank.getRank(args[1]);
			p.getSpelerOptions().setDonatorRank(r, executor.getSpelerOptions(), a, !silent);
		}
		
		p.sessionSys.releaseAccess();
	}
	
	public static void executePermissionCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args){
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		if(args.length < 1 || args.length > 3){
			a.sendMessage("§c/permission <§ospeler§c> [§opermission§c] [§onieuwe waarde:§caan|uit|dynamic]");
			return;
		}
		SpelerOptions exe = executor.getSpelerOptions();
		
		if(executor.getSpelerOptions().getRank() < 1){
			a.sendMessage("§4Je moet minimum de rank \"Player\" hebben om dit commando te gebruiken");
			return;
		}
		
		Person p;
		if(executor.getSpelerOptions().getRank() >= 70){
			p = Main.pm.getPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in alle spelers");
				return;
			}
		}else{
			p = Main.pm.getLoadedPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in de ingeladen spelers");
				return;
			}
		}
		p.sessionSys.acquireAccess();
		
		if(args.length == 1){
			a.sendMessage("§4Permissions in een lijst weergeven is nog niet beschikbaar, gebruik §c/permission <§ospeler§c> [permission]§4 om individueel permissions te bekijken");
		}else if(args.length == 2){
			byte adress = SpelerOptions.getPermissionAdress(args[1]);
			if(adress == 0x7F){
				a.sendMessage("§4Onbekende permission (\"" + args[1] + "\")");
				p.sessionSys.releaseAccess();
				return;
			}
			if(p.getSpelerOptions() != exe && exe.getPermissionLevel() < SpelerOptions.getPermissionLevelRequired(adress, 0)){
				a.sendMessage("§4Je hebt geen toegang om deze permission te bekijken");
				p.sessionSys.releaseAccess();
				return;
			}
			a.sendMessage("§eDe permission \"" + args[1] + "\" van \"" + args[0] + "\" is " + (p.getSpelerOptions().getSwitch(adress, false)?"§2Aan":"§4Uit") + "§6 en is " + (p.getSpelerOptions().getSwitch(adress, true)?"Static":"Dynamic"));
		}else if(args.length == 3){
			byte adress = SpelerOptions.getPermissionAdress(args[1]);
			if(adress == 0x7F){
				a.sendMessage("§4Onbekende permission (\"" + args[1] + "\")");
				p.sessionSys.releaseAccess();
				return;
			}
			boolean on;
			boolean isStatic;
			args[2] = args[2].toLowerCase();
			if(args[2].equals("aan") || args[2].equals("1") || args[2].equals("on") || args[2].equals("+")){
				on = true;
				isStatic = true;
			}else if(args[2].equals("uit") || args[2].equals("0") || args[2].equals("off") || args[2].equals("-")){
				on = false;
				isStatic = true;
			}else if(args[2].equals("dynamic") || args[2].equals("d")){
				on = false;
				isStatic = false;
			}else{
				a.sendMessage("§c/permission <§ospeler§c> [§opermission§c] [§onieuwe waarde§c]");
				a.sendMessage("§cDe nieuwe waarde kan zijn: §2\"aan\", \"1\", \"on\", \"+\", §4\"uit\", \"0\", \"off\", \"-\", §1\"dynamic\", \"D\"");
				p.sessionSys.releaseAccess();
				return;
			}
			boolean silent = (executor.getSpelerOptions().getPermissionLevel() > p.getSpelerOptions().getPermissionLevel() &&
					executor.getSpelerOptions().getRank() >= p.getSpelerOptions().getRank() && attribSys.hasAttrib("silent"));
			p.getSpelerOptions().setPermission(adress, on, isStatic, executor.getSpelerOptions(), a, !silent);
		}
		
		p.sessionSys.releaseAccess();
	}
	
	public static void executeOptionCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args){
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		if(args.length < 1 || args.length > 3){
			a.sendMessage("§c/option [§ospeler§c] [§ooption§r§c] [§onieuwe waarde:§caan|uit]");
			return;
		}
		SpelerOptions exe = executor.getSpelerOptions();
		
		if(executor.getSpelerOptions().getRank() < 1){
			a.sendMessage("§4Je moet minimum de rank \"Player\" hebben om dit commando te gebruiken");
			return;
		}
		
		
		Person p;
		if(executor.getSpelerOptions().getRank() >= 70){
			p = Main.pm.getPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in alle spelers");
				return;
			}
		}else{
			p = Main.pm.getLoadedPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in de ingeladen spelers");
				return;
			}
		}
		p.sessionSys.acquireAccess();
		
		if(args.length == 1){
			a.sendMessage("§4Options in een lijst weergeven is nog niet beschikbaar, gebruik §c/option <§ospeler§c> [option]§4 om individueel options te bekijken");
		}else if(args.length == 2){
			byte adress = SpelerOptions.getOptionAdress(args[1]);
			if(adress == 0x7F){
				a.sendMessage("§4Onbekende option (\"" + args[1] + "\")");
				p.sessionSys.releaseAccess();
				return;
			}
			if(p.getSpelerOptions() != exe && exe.getPermissionLevel() < SpelerOptions.getPermissionLevelRequired(adress, 0)){
				a.sendMessage("§4Je hebt geen toegang om deze option te bekijken");
				p.sessionSys.releaseAccess();
				return;
			}
			a.sendMessage("§6De option \"" + args[1] + "\" van \"" + args[0] + "\" is " + (p.getSpelerOptions().getSwitch(adress, false)?"§2Aan":"§4Uit"));
		}else if(args.length == 3){
			byte adress = SpelerOptions.getOptionAdress(args[1]);
			if(adress == 0x7F){
				a.sendMessage("§4Onbekende option (\"" + args[1] + "\")");
				p.sessionSys.releaseAccess();
				return;
			}
			boolean on;
			args[2] = args[2].toLowerCase();
			if(args[2].equals("aan") || args[2].equals("1") || args[2].equals("on") || args[2].equals("+")){
				on = true;
			}else if(args[2].equals("uit") || args[2].equals("0") || args[2].equals("off") || args[2].equals("-")){
				on = false;
			}else{
				a.sendMessage("§c/option set <speler> <option> <nieuwe waarde>");
				a.sendMessage("§cDe nieuwe waarde kan zijn: §2\"aan\", \"1\", \"on\", \"+\", §4\"uit\", \"0\", \"off\", \"-\"");
				p.sessionSys.releaseAccess();
				return;
			}
			boolean silent = (executor.getSpelerOptions().getPermissionLevel() > p.getSpelerOptions().getPermissionLevel() &&
					executor.getSpelerOptions().getRank() >= p.getSpelerOptions().getRank() && attribSys.hasAttrib("silent"));
			p.getSpelerOptions().setOption(adress, on, executor.getSpelerOptions(), a, !silent);
		}
		
		p.sessionSys.releaseAccess();
	}
	
	public static void executeProfileCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args){
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		if(args.length == 0){
			a.sendMessage("§c/profile <permission|option|refresh|rank|donateurrank|permissionlevel> <...>");
			return;
		}
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		args[0] = args[0].toLowerCase();
		if(args[0].equals("permission") || args[0].equals("perm")){
			CommandsPlayerSystem.executePermissionCommand(executor, a, attribSys, newArgs);
		}else if(args[0].equals("option")){
			CommandsPlayerSystem.executeOptionCommand(executor, a, attribSys, newArgs);
		}else if(args[0].equals("rank")){
			CommandsPlayerSystem.executeRankCommand(executor, a, attribSys, newArgs);
		}else if(args[0].equals("donateurrank") || args[0].equals("d-rank")){
			CommandsPlayerSystem.executeDonateurRankCommand(executor, a, attribSys, newArgs);
		}else if(args[0].equals("refresh")){
			CommandsPlayerSystem.executeRefreshCommand(executor, a, newArgs);
		}else if(args[0].equals("permissionlevel")){
			CommandsPlayerSystem.executePermissionLevelCommand(executor, a, newArgs);
		}else if(args[0].equals("info")){
			CommandsPlayerSystem.executeProfileInfoCommand(executor, a, attribSys, newArgs);
		}else{
			a.sendMessage("§c/profile <permission|option|refresh|rank|donateurrank|permissionlevel> <...>");
		}
	}
	
	public static void executeRefreshCommand(Person executor, CommandSender a, String[] args){
		if(a == null)return;
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		Person affected = null;
		if(args.length == 0){
			if(executor == null){
				a.sendMessage("§6Het ziet ernaar uit dat je profiel niet meegegeven is naar dit deel van het commando, even bezig met dat te fixen...");
				if(a instanceof Player){
					a.sendMessage("§6Maar eerst even de login-procedure doen voor het geval je niet ingeladen bent...");
					Main.pm.loadPersonOnline((Player)a, true);
				}
				executor = Main.pm.getLoadedPlayer((Player)a);
				if(executor == null){
					a.sendMessage("§eDe PlayerManager kan je profiel helaas niet geven, sorry =/");
					a.sendMessage("§4Commando geannuleerd");
					return;
				}else{
					a.sendMessage("§6Ah! Daar ben je! §2:)");
				}
			}
			affected = executor;
		}else if(args.length == 1){
			if(executor.getSpelerOptions().getRank() >= 70){
				affected = Main.pm.getPlayer(args[0]);
				if(affected == null){
					a.sendMessage("§4De speler is niet gevonden in alle spelers");
					return;
				}
			}else{
				a.sendMessage("§4Om het profiel van iemand anders te refreshen moet je Admin zijn, als je je eigen profiel wou refreshen, gebruik dan §c/profile refresh");
			}
		}else{
			a.sendMessage("§c/profile refresh [speler]");
		}
		affected.sessionSys.acquireAccess();
		
		if(executor != affected && executor.getSpelerOptions().getOpStatus() < 2){
			a.sendMessage("§4Je moet Operator zijn om dit commando te gebruiken voor andere spelers");
			affected.sessionSys.releaseAccess();
			return;
		}
		
		affected.getSpelerOptions().refreshRank();
		
		if(executor == affected){
			a.sendMessage("§eJe profiel is refreshed");
		}else{
			a.sendMessage("§eJe hebt het profiel van \"" + args[1] + "\" refreshed");
		}
		
		affected.sessionSys.releaseAccess();
	}
	
	public static void executePermissionLevelCommand(Person executor, CommandSender a, String[] args){
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		if(executor.getSpelerOptions().getOpStatus() < 2){
			a.sendMessage("§4Je moet Operator zijn om dit commando te kunnen gebruiken");
			return;
		}
		if(args.length < 1 || args.length > 2){
			a.sendMessage("§c/profile permissionlevel <speler> [nieuw PermissionLevel]");
			return;
		}
		
		Person p;
		if(executor.getSpelerOptions().getRank() >= 70){
			p = Main.pm.getPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in alle spelers");
				return;
			}
		}else{
			p = Main.pm.getLoadedPlayer(args[0]);
			if(p == null){
				a.sendMessage("§4De speler is niet gevonden in de ingeladen spelers");
				return;
			}
		}
		p.sessionSys.acquireAccess();
		
		if(args.length == 1){
			byte level = p.getSpelerOptions().getPermissionLevel();
			a.sendMessage("§eHet PermissionLevel van \"" + args[0] + "\" is " + level);
		}else if(args.length == 2){
			if(executor.getSpelerOptions().getOpStatus() < 3){
				a.sendMessage("§4Enkel de Console mag PermissionLevels veranderen");
				return;
			}
			byte level;
			try{
				level = Byte.parseByte(args[1]);
			}catch(Exception e){
				a.sendMessage("§4Het nieuwe PermissionLevel moet een nummer zijn die minimum 0 en maximum 14 is");
				return;
			}
			if(level < 0 || level > 14){
				a.sendMessage("§4Het nieuwe PermissionLevel moet minimum 0 en maximum 14 zijn");
				return;
			}
			p.getSpelerOptions().setPermissionLevel(level);
			a.sendMessage("§eHet PermissionLevel van " + args[0] + " is nu " + p.getSpelerOptions().getPermissionLevel());
		}
		p.sessionSys.releaseAccess();
	}
	
	public static void executeDflyCommand(Person executor, CommandSender sender, String[] args){
		if(Main.pm == null || Main.pm.preventAction()){
			sender.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		if(!(sender instanceof Player)){
			sender.sendMessage("§4Dit commando is enkel beschikbaar voor spelers");
			return;
		}
		if(executor.getSpelerOptions().getSwitch((byte) 0x54, false)){
			if(args.length > 1){
				sender.sendMessage("§c/dfly [aan|uit|info]");
				return;
			}
			Player pl = (Player) sender;
			if(args.length == 0){
				if(pl.getAllowFlight()){
					pl.setAllowFlight(false);
					sender.sendMessage("Je fly is nu §4uit");
				}else{
					pl.setAllowFlight(true);
					sender.sendMessage("Je fly is nu §2aan");
				}
				return;
			}
			args[0] = args[0].toLowerCase();
			
			if(args[0].equals("on") || args[0].equals("aan")){
				pl.setAllowFlight(true);
				sender.sendMessage("§eJe fly is nu §2aan");
			}else if(args[0].equals("off") || args[0].equals("uit")){
				pl.setAllowFlight(false);
				sender.sendMessage("§eJe fly is nu §4uit");
			}else if(args[0].equals("info")){
				sender.sendMessage("§eJe fly is " + (pl.getAllowFlight()?"aan":"uit"));
			}else{
				sender.sendMessage("§c/dfly [aan|uit|info]");
				return;
			}
		}else{
			sender.sendMessage("§4Je hebt geen toegang tot dit commando");
			return;
		}
	}
	
	public static void executeProfileInfoCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args){
		if(a == null || executor == null)return;
		if(args == null || args.length == 0){
			executor.printInfo(a);
		}else{
			if(executor.getSpelerOptions().getOpStatus() < 2){
				a.sendMessage("§4Je hebt geen toegang om het profiel van iemand anders te bekijken. Als je je eigen wilt bekijken, gebruik dan §c/profile info");
				return;
			}
			PersonSession ps = new PersonSession(Main.pm);
			if(!ps.loadAnyPlayer(args[0]) || ps.getPerson() == null){
				a.sendMessage("§4De speler is niet gevonden");
				return;
			}
			ps.getPerson().printInfo(a);
			ps.unlock();
		}
	}
	
	public static void executePlayerManagerCommand(CommandSender sender, Player[] online, String[] args){
		
	}
	
	public static void executeDonateurCommand(CommandSender sender, String[] args){
		/*int index = -1;
		if(args.length == 1){
			if(args.length > 2){
				sender.sendMessage("§c/donateur [pagina|\"overview\"|\"info\"|\"Kartoffel\"|\"KartoffelVIP\"|\"KartoffelLord\"|\"KartoffelGod\"");
				return true;
			}
			String param = args[0].toLowerCase();
			if(param.equals("overview") || param.equals("0")){
				index = 0;
			}else if(param.equals("info") || param.equals("1")){
				index = 1;
			}else if(param.equals("kartoffel") || param.equals("2")){
				index = 2;
			}else if(param.equals("kartoffelvip") || param.equals("3")){
				index = 3;
			}else if(param.equals("kartoffellord") || param.equals("4")){
				index = 4;
			}else if(param.equals("kartoffelgod") || param.equals("5")){
				index = 5;
			}else{
				sender.sendMessage("§c/donateur [pagina(0-5)|\"info\"|\"Kartoffel\"|\"KartoffelVIP\"|\"KartoffelLord\"|\"KartoffelGod\"");
				return true;
			}
		}else{
			index = 0;
		}
		
		sender.sendMessage("§6-----Donatie Help-screen (pagina " + index + "/5)-----");
		if(sender instanceof Player){
			((Player) sender).sendRawMessage("{color:yellow,text:\"[Doneer nu]\",clickEvent:{action:open_url,value:\"https://www.serverbuilds.nl/serverlist/view/5676\"}}");
		}else{
			sender.sendMessage("§6Doneer via https://www.serverbuilds.nl/serverlist/view/5676");
		}
		if(index == 0){
			sender.sendMessage("§1§nOverview");//t
			if(sender instanceof Player){
				Player pl = (Player) sender;
				//1
				pl.sendRawMessage("{text:\"[Overzicht]\",color:gray,hoverEvent:{action:show_text,value:\"Deze pagina\"}}");
				//2
				pl.sendRawMessage("{text:\"[Info]\",color:dark_red,hoverEvent:{action:show_text,value:\"Klik\"},clickEvent:{action:run_command,value:\"/donateur 1\"}}");
				//3
				pl.sendRawMessage("{text:\"[Kartoffel]\",color:dark_red,hoverEvent:{action:show_text,value:\"Klik\"},clickEvent:{action:run_command,value:\"/donateur 2\"}}");
				//4
				pl.sendRawMessage("{text:\"[KartoffelVIP]\",color:dark_red,hoverEvent:{action:show_text,value:\"Klik\"},clickEvent:{action:run_command,value:\"/donateur 3\"}}");
				//5
				pl.sendRawMessage("{text:\"[KartoffelLord]\",color:dark_red,hoverEvent:{action:show_text,value:\"Klik\"},clickEvent:{action:run_command,value:\"/donateur 4\"}}");
				//6
				pl.sendRawMessage("{text:\"[KartoffelGod]\",color:dark_red,hoverEvent:{action:show_text,value:\"Klik\"},clickEvent:{action:run_command,value:\"/donateur 5\"}}");
				pl.sendMessage("");//7
				pl.sendMessage("");//8
			}else{
				sender.sendMessage("§c/donateur [hoofdstuk]");//1
				sender.sendMessage("§7[Overzicht]");//2
				sender.sendMessage("§4[Info]");//3
				sender.sendMessage("§4[Kartoffel]");//4
				sender.sendMessage("§4[KartoffelVIP]");//5
				sender.sendMessage("§4[KartoffelLord]");//6
				sender.sendMessage("§4[KartoffelGod]");//7
				sender.sendMessage("");//8
			}
		}else if(index == 1){
			sender.sendMessage("§1§nInfo");//t
			
			sender.sendMessage("§4§nWaarom doneren?");//1
			sender.sendMessage("§aDoneren helpt de server ten eerste enorm, als dank en als extra krijg je ook coole stuff :) (zie volgende pagina)");//2-3
			sender.sendMessage("§aBedenk dat Thomas deze server mogelijk maakt met zijn eigen zakgeld...");//4-5
			sender.sendMessage("§4§nHoe donneren?");//6
			sender.sendMessage("§aGa naar §1§nhttps://www.serverbuilds.nl/serverlist/view/5676§r (klik op link!) en volg de instructies");//7-8
		}else if(index == 2){
			sender.sendMessage("§1§nKartoffel ($1,50) §d[Kartoffel]");//t
			sender.sendMessage("§9- 3 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- Hat-commando §6(Essentials)");//2
			sender.sendMessage("§9- Workbench-commando §6(Essentials)");//3
			sender.sendMessage("§9- Geen §c/tpa§f en §c/tpaccept§f-timeout §6(Essentials)");//4
			sender.sendMessage("§9- Nog meer extaatjes coming §6(KartoffelKanaalMod)");//5
			sender.sendMessage("");//6
			sender.sendMessage("");//7
			sender.sendMessage("");//8
			//sender.sendMessage("§9- Geavanceerd gevecht hosten in de Arena §6(KartoffelKanaalMod)");
		}else if(index == 3){
			sender.sendMessage("§1Kartoffel VIP ($5,00) §2[KartoffelVIP]");//t
			sender.sendMessage("§9- 5 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- Nick-commando §6(Essentials)");//2
			sender.sendMessage("§9- Nickname kleuren §6(Essentials)");//3
			sender.sendMessage("§9- Heal-commando §6(Essentials)");//4
			sender.sendMessage("");//5
			sender.sendMessage("");//6
			sender.sendMessage("");//7
			sender.sendMessage("");//8
		}else if(index == 4){
			sender.sendMessage("§1Kartoffel Lord ($8,00) §7[KartoffelLord]");//t
			sender.sendMessage("§9- 7 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- 2 gratis diamonds per dag §6(KartoffelKanaalMod)");//2
			sender.sendMessage("§9- Top-commando §6(Essentials)");//3
			sender.sendMessage("");//4
			sender.sendMessage("");//5
			sender.sendMessage("");//6
			sender.sendMessage("");//7
			sender.sendMessage("");//8
		}else if(index == 5){
			sender.sendMessage("§1Kartoffel God ($15,00) §9[KartoffelGod]");//t
			sender.sendMessage("§9- 10 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- 3 gratis diamonds per dag §6(KartoffelKanaalMod)");//2
			sender.sendMessage("§9- Weather-commando's §6(Bukkit & Essentials)");//3
			sender.sendMessage("§9- Time-commando's §6(Bukkit & Essentials)");//4
			sender.sendMessage("§9- Enderchest-commando §6(Essentials)");//5
			sender.sendMessage("");//6
			sender.sendMessage("");//7
			sender.sendMessage("");//8
		}
		if(sender instanceof Player){
			Player pl = (Player) sender;
			pl.sendRawMessage("[{text:\"[Vorige]\",color:" + ((index > 0)?("yellow,clickEvent:{action:run_command,value:\"/donateur " + (index - 1) + "\"}"):"gray") + "}," + 
			"{text:\" \"},{text:\"[Volgende]\",color:" + ((index < 5)?("yellow,clickEvent:{action:run_command,value:\"/donateur " + (index + 1) + "\"}"):"gray") + "}]");
		}else{
			if(index < 5){
				sender.sendMessage("§eGa naar de volgende pagina met §c/donateurrank " + (index + 1));
			}else{
				sender.sendMessage("§eDit is de laatste pagina");
			}
		}
		*/
		Player pl = (sender instanceof Player)?(Player) sender: null;
		int index = 0;
		if(args.length == 1){
			if(args.length > 2){
				sender.sendMessage("§c/donateur [pagina|\"overview\"|\"info\"|\"Kartoffel\"|\"KartoffelVIP\"|\"KartoffelLord\"|\"KartoffelGod\"");
				return;
			}
			String param = args[0].toLowerCase();
			if(param.equals("overview") || param.equals("0")){
				index = 0;
			}else if(param.equals("info") || param.equals("1")){
				index = 1;
			}else if(param.equals("kartoffel") || param.equals("2")){
				index = 2;
			}else if(param.equals("kartoffelvip") || param.equals("3")){
				index = 3;
			}else if(param.equals("kartoffellord") || param.equals("4")){
				index = 4;
			}else if(param.equals("kartoffelgod") || param.equals("5")){
				index = 5;
			}else{
				sender.sendMessage("§c/donateur [pagina(0-5)|\"info\"|\"Kartoffel\"|\"KartoffelVIP\"|\"KartoffelLord\"|\"KartoffelGod\"");
				return;
			}
		}
		
		sender.sendMessage("§6-------- Donatie Help-Screen (Pagina " + index + "/5) --------");
		if(pl == null){
			sender.sendMessage("§6Doneer via https://www.serverbuilds.nl/serverlist/view/5676");
		}else{
			Main.plugin.sendRawMessage(pl.getName(),"{color:yellow,text:\"[Doneer nu]\",clickEvent:{action:open_url,value:\"https://www.serverbuilds.nl/serverlist/view/5676\"}}");
		}
		if(index == 0){
			sender.sendMessage("§b§lOverview");//t
			if(pl != null){
				Server se = Main.plugin.getServer();
				CommandSender c = se.getConsoleSender();
				String base = "tellraw " + pl.getName() + " ";
				////1
				//se.dispatchCommand(c, base + "{text:\"[Overzicht]\",color:gray,hoverEvent:{action:show_text,value:\"Deze pagina\"}}");
				//2
				se.dispatchCommand(c, base + "{text:\"[Info]\",color:yellow,hoverEvent:{action:show_text,value:\"Klik om naar deze pagina te gaan\"},clickEvent:{action:run_command,value:\"/donateur 1\"}}");
				//3
				se.dispatchCommand(c, base + "{text:\"[Kartoffel]\",color:light_purple,hoverEvent:{action:show_text,value:\"Klik om naar deze pagina te gaan\"},clickEvent:{action:run_command,value:\"/donateur 2\"}}");
				//4
				se.dispatchCommand(c, base + "{text:\"[KartoffelVIP]\",color:dark_green,hoverEvent:{action:show_text,value:\"Klik om naar deze pagina te gaan\"},clickEvent:{action:run_command,value:\"/donateur 3\"}}");
				//5
				se.dispatchCommand(c, base + "{text:\"[KartoffelLord]\",color:gold,hoverEvent:{action:show_text,value:\"Klik om naar deze pagina te gaan\"},clickEvent:{action:run_command,value:\"/donateur 4\"}}");
				//6
				se.dispatchCommand(c, base + "{text:\"[KartoffelGod]\",color:blue,hoverEvent:{action:show_text,value:\"Klik om naar deze pagina te gaan\"},clickEvent:{action:run_command,value:\"/donateur 5\"}}");
				pl.sendMessage("");//7
				pl.sendMessage("");//8
				pl.sendMessage("");//1
			}else{
				sender.sendMessage("§c/donateur [hoofdstuk]");//1
				//sender.sendMessage("§7[Overzicht]");//2
				sender.sendMessage("§4[Info]");//3
				sender.sendMessage("§d[Kartoffel]");//4
				sender.sendMessage("§2[KartoffelVIP]");//5
				sender.sendMessage("§6[KartoffelLord]");//6
				sender.sendMessage("§9[KartoffelGod]");//7
				sender.sendMessage("");//8
				sender.sendMessage("");//2
			}
		}else if(index == 1){
			sender.sendMessage("§b§lInfo");//t
			sender.sendMessage("§2§n- Waarom doneren?");//1
			sender.sendMessage("§aDoneren helpt de server ten eerste enorm, als dank en als extra krijg je ook coole stuff :) (zie volgende pagina)");//2-3
			sender.sendMessage("§aBedenk dat Thomas deze server mogelijk maakt met zijn eigen zakgeld...");//4-5
			sender.sendMessage("§2§n- Hoe doneren?");//6
			sender.sendMessage("§aGa naar §bhttps://www.serverbuilds.nl/serverlist/view/5676 §a(klik op link!) en volg de instructies");//7-8
		}else if(index == 2){
			sender.sendMessage("§b§lKartoffel (1,50 euro)§d [Kartoffel]");//t
			sender.sendMessage("§9- 3 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- Hat-commando §6(Essentials)");//2
			sender.sendMessage("§9- Workbench-commando §6(Essentials)");//3
			sender.sendMessage("§9- Geen §c/tpa§9 en §c/tpaccept§9-timeout §6(Essentials)");//4
			sender.sendMessage("");//5
			sender.sendMessage("");//6
			sender.sendMessage("");//7
			sender.sendMessage("");//8
			//sender.sendMessage("§9- Geavanceerd gevecht hosten in de Arena §6(KartoffelKanaalMod)");
		}else if(index == 3){
			sender.sendMessage("§b§lKartoffel VIP (5,00 euro)§2 [KartoffelVIP]");//t
			sender.sendMessage("§9- 5 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- Nick-commando §6(Essentials)");//2
			sender.sendMessage("§9- Nickname kleuren §6(Essentials)");//3
			sender.sendMessage("§9- Heal-commando §6(Essentials)");//4
			if(pl == null){
				sender.sendMessage("§9- Alles van Kartoffel");//5
			}else{
				Main.plugin.sendRawMessage(pl.getName(), "{text:\"- Alles van \",color:blue,clickEvent:{action:run_command,value:\"/donateur 2\"},extra:[{text:\"[Kartoffel]\",color:light_purple}]}");
			}
			sender.sendMessage("");//6
			sender.sendMessage("");//7
			sender.sendMessage("");//8
		}else if(index == 4){
			sender.sendMessage("§b§lKartoffel Lord (8,00 euro)§6 [KartoffelLord]");//t
			sender.sendMessage("§9- 7 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- 2 gratis diamonds per dag (in tot.) §6(KartoffelKanaalPlugin)");//2
			sender.sendMessage("§9- Top-commando §6(Essentials)");//3
			if(pl == null){
				sender.sendMessage("§9- Alles van KartoffelVIP");//5
			}else{
				Main.plugin.sendRawMessage(pl.getName(), "{text:\"- Alles van \",color:blue,clickEvent:{action:run_command,value:\"/donateur 3\"},extra:[{text:\"[KartoffelVIP]\",color:dark_green}]}");
			}
			sender.sendMessage("");//5
			sender.sendMessage("");//6
			sender.sendMessage("");//7
			sender.sendMessage("");//8
		}else if(index == 5){
			sender.sendMessage("§b§lKartoffel God (15,00 euro)§9 [KartoffelGod]");//t
			sender.sendMessage("§9- 10 homes (in totaal) §6(Essentials)");//1
			sender.sendMessage("§9- 3 gratis diamonds per dag (in tot.) §6(KartoffelKanaalPlugin)");//2
			sender.sendMessage("§9- Weather-commando's §6(Bukkit & Essentials)");//3
			sender.sendMessage("§9- Time-commando's §6(Bukkit & Essentials)");//4
			sender.sendMessage("§9- Enderchest-commando §6(Essentials)");//5
			sender.sendMessage("§9- Toegang tot het §c/dly§9-commando §6(KartoffelKanaalPlugin");//6
			if(pl == null){
				sender.sendMessage("§9- Alles van KartoffelLord");//7
			}else{
				Main.plugin.sendRawMessage(pl.getName(), "{text:\"- Alles van \",color:blue,clickEvent:{action:run_command,value:\"/donateur 4\"},extra:[{text:\"[KartoffelLord]\",color:gold}]}");
			}
			sender.sendMessage("");//8
		}
		if(pl != null){
			//ChatColor.
			Main.plugin.sendRawMessage(pl.getName(), "[" +
					"{text:\"-------- \",color:gold}," +
					"{text:\"[Vorige]\",color:" + ((index > 0)?("yellow,clickEvent:{action:run_command,value:\"/donateur " + (index - 1) + "\"}"):"gray") + "}," +
					"\" | \"," + 
					"{text:\"[Overzicht]\",color:gold,clickEvent:{action:run_command,value:\"/donateur 0\"}}," +
					"\" | \"," +
					"{text:\"[Volgende]\",color:" + ((index < 5)?("yellow,clickEvent:{action:run_command,value:\"/donateur " + (index + 1) + "\"}"):"gray") + "}," +
					"{text:\" --------\",color:gold}]");
		}else{
			if(index < 5){
				sender.sendMessage("§eGa naar de volgende pagina met §c/donataur " + (index + 1));
			}else{
				sender.sendMessage("§eDit is de laatste pagina");
			}
		}
	}
}
