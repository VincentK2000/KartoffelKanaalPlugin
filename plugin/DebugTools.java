package KartoffelKanaalPlugin.plugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.SpelerOptions;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem.Pulser;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem.PulserNotif;

public class DebugTools implements Listener {
	public static boolean developermodus = false;
	protected static boolean devadvancedchat = false;
	protected static final AdvancedChat ac = new AdvancedChat();
	
	protected DebugTools(){
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		if(devadvancedchat)Main.plugin.getServer().getPluginManager().registerEvents(DebugTools.ac, Main.plugin);
	}
	
	protected static void executeCommand(Player p, String command){
		if(p == null || command == null || command.length() == 0 || Main.plugin == null)return;
		if(command.startsWith("$"))command = command.substring(1);
		String[] input = command.split(" ");
		if(input[0].equals("dev")){
			DebugTools.executeDebugDEV(p, input);
		}else if(input[0].equals("devadvchat")){//devadvchat = developer advanced chat
			DebugTools.executeDebugADVCHAT(p, input);
		}else if(input[0].equals("perm")){
			DebugTools.executeDebugPERM(p, input);
		}else if(input[0].equals("markfree")){
			DebugTools.executeDebugMARKFREE(p, input);
		}else if(input[0].equals("lore")){
			DebugTools.executeDebugLORE(p, input);
		}else if(input[0].equals("pulser")){
			DebugTools.executeDebugPULSER(p, input);
		}else if(input[0].equals("settings")){
			DebugTools.executeDebugSETTINGS(p, input);
		}else if(input[0].equals("speleroptions")){
			DebugTools.executeDebugSPELEROPTIONS(p, input);
		}else{
			p.sendMessage("§4Onbekend Debug-Command");
		}
	}
	private static void executeDebugPERM(Player p, String[] input){
		if(input.length == 1){
			p.sendMessage("$perm <get|set|_set>");
		}else{
			if(input[1].equals("get")){
				if(input.length != 3){
					p.sendMessage("§c$perm get <player> <permission>");
				}
				Person person = Main.pm.getPlayer(input[2]);
				if(person == null){
					p.sendMessage("§4Speler niet gevonden");
					return;
				}
				
				SpelerOptions so = person.getSpelerOptions();
				byte adress = SpelerOptions.getPermissionAdress(input[3]);
				p.sendMessage("PermissionAdress: " + adress + " | PermissionName: " + SpelerOptions.getAdressName(adress, false));
				p.sendMessage("Playername: " + p.getName() + " | PlayerUUID: " + p.getUniqueId().toString());
				if(adress == 0x7F){
					p.sendMessage("§4Onbekende permission");
					return;
				}
				p.sendMessage("Get Value: " + so.getSwitch(adress, false) + " (" + (so.getSwitch(adress, true)?'S':'D') + ")");
				
			}else if(input[1].equals("set") || input[1].equals("_set") || input[1].equals("SET") || input[1].equals("_SET")){
				boolean update = input[1].equals("set");
				if(input.length != 6){
					p.sendMessage("$perm " + (update?"set":"_set") + " <player> <permission> <value> <static?>");
				}else{
					//if((input[4].equals("0") || input[4].equals("1") && (input[5].equals("0") || input[5].equals("1") || input[5].equals("S") || input[5].equals("D")))){
					Person person = Main.pm.getPlayer(input[2]);
					if(person == null){
						p.sendMessage("§4Speler niet gevonden");
						return;
					}
					SpelerOptions so = person.getSpelerOptions();
					byte adress = SpelerOptions.getPermissionAdress(input[3]);
					p.sendMessage("PermissionAdress: " + adress + " | PermissionName: " + SpelerOptions.getAdressName(adress, true));
					p.sendMessage("Playername: " + p.getName() + " | PlayerUUID: " + p.getUniqueId().toString());
					if(adress == 0x7F){
						p.sendMessage("§4Onbekende permission");
						return;
					}
					boolean on;
					boolean isStatic;
					if(input[4] == "1" || input[4] == "on" || input[4] == "+"){
						on = true;
					}else if(input[4] == "0" || input[4] == "off" || input[4] == "-"){
						on = false;
					}else{
						p.sendMessage("§4Invalid \"value\"-value");
						p.sendMessage("§c$perm " + (update?"set":"_set") + " <player> <permission> <value> <static?>");
						return;
					}
								
					if(input[5] == "1" || input[5] == "on" || input[5] == "+" || input[5] == "S" || input[5] == "static"){
						isStatic = true;
					}else if(input[5] == "0" || input[5] == "off" || input[5] == "-" || input[5] == "D" || input[5] == "dynamic"){
						isStatic = false;
					}else{
						p.sendMessage("§4Invalid \"isStatic\"-value");
						p.sendMessage("§c$perm " + (update?"set":"_set") + " <player> <permission> <value> <static?>");
						return;
					}
					so.setPermission(adress, on, isStatic, update);
					p.sendMessage("New Value: " + so.getSwitch(adress, false) + " (" + (so.getSwitch(adress, true)?'S':'D') + ")");
					
					//}else{
					//	e.getPlayer().sendMessage("Invalid argument");
					//	e.getPlayer().sendMessage("$perm " + (update?"set":"_set") + " <player> <permission> <value:0|1> <static?:0|1|D|S>");
					//}
				}
			}
		}
	}
	
	private static void executeDebugPULSER(Player p, String[] input){
		if(Main.pulser == null){
			p.sendMessage("De pulser is null");
		}
		if(input.length < 2){
			p.sendMessage("§c$pulser <tick|tickinterval|notification|on|off|loadNotifications|timesticked> <...>");
			return;
		}
		if(input[1].equals("tick")){
			if(input.length < 3){
				p.sendMessage("§c$pulser tick <set|execute|get> <...>");
				return;
			}
			if(input[2].equals("set")){
				if(input.length < 4){
					p.sendMessage("§c$pulser tick set <newvalue: [~]getal>");
					return;
				}
				if(input[3].length() == 0){
					p.sendMessage("§4Nieuwe value is leeg");
					return;
				}
				if(input[3].charAt(0) == '~'){
					if(input[3].length() == 1){
						p.sendMessage("§4Na de \"~\" dient er een relatief getal te worden geplaatst, je mag het getal hier niet weglaten zoals in Minecraft omdat het geen zin heeft een value te veranderen naar zichzelf");
						return;
					}
					int i;
					try{
						i = Integer.parseInt(input[3].substring(1));
					}catch(Exception e){
						p.sendMessage("§4Na de \"~\" dient er een relatief getal (signed int) te worden geplaatst");
						return;
					}
					Pulser.tickcount += i;
				}else{
					int i;
					try{
						i = Integer.parseInt(input[3]);
					}catch(Exception e){
						p.sendMessage("§4Onjuist getal");
						return;
					}
					Pulser.tickcount = i;
				}
				p.sendMessage("TickCount = " + Pulser.tickcount);
			}else if(input[2].equals("execute")){
				if(input.length < 4){
					Main.pulser.executeTick(++Pulser.tickcount);
				}else if(input.length == 4 || input.length == 5){
					boolean apply = false;
					if(input.length == 5){
						if(input[4].equals("-a") || input[4].equals("-apply")){
							apply = true;
						}else{
							p.sendMessage("$pulser tick execute [[~]tick] [-a[pply]]");;
						}
					}
					int tick;
					if(input[3].charAt(0) == '~'){
						if(input[3].length() == 1){
							p.sendMessage("§4Na de \"~\" dient er een relatief getal te worden geplaatst, je mag het getal hier niet weglaten zoals in Minecraft omdat het geen zin heeft een value te veranderen naar zichzelf");
							return;
						}
						try{
							tick = Integer.parseInt(input[3].substring(1));
						}catch(Exception e){
							p.sendMessage("§4Na de \"~\" dient er een relatief getal (signed int) te worden geplaatst");
							return;
						}
						tick += Pulser.tickcount;
					}else{
						try{
							tick = Integer.parseInt(input[3]);
						}catch(Exception e){
							p.sendMessage("§4Onjuist getal");
							return;
						}
					}
					if(apply)Pulser.tickcount = tick;
					Main.pulser.executeTick(tick);
				}else{
					p.sendMessage("$pulser tick execute [[~]tick] [-a[pply]]");
				}
			}else if(input[2].equals("get")){
				if(input.length > 2){
					p.sendMessage("§c$pulser get");
				}
				p.sendMessage("Tick: " + Pulser.tickcount);
			}
		}else if(input[1].equals("tickinterval")){
			if(input.length < 3){
				p.sendMessage("§c$pulser tickinterval <set|get> <...>");
				return;
			}
			if(input[2].equals("set")){
				int newInterval;
				int multiplier = 60000;
				if(input.length == 3 || input.length > 5){
					p.sendMessage("§4$pulser tickinterval set <newInterval> [ms|s|m|u]");
					return;
				}else if(input.length == 5){
					if(input[4].equals("ms")){
						multiplier = 1;
					}else if(input[4].equals("s")){
						multiplier = 1000;
					}else if(input[4].equals("m")){
						multiplier = 60000;
					}else if(input[4].equals("u")){
						multiplier = 3600000;
					}
				}
				double abc;
				try{
					abc = Double.parseDouble(input[3]);
				}catch(Exception e){
					p.sendMessage("§4Incorrecte Double: " + String.valueOf(input[3]));
					return;
				}
				newInterval = (int) (abc * multiplier);
				if(newInterval < 20000){
					p.sendMessage("De interval moet minimum 20 seconden zijn. De opgegeven is " + newInterval + " milliseconden");
					return;
				}
				Main.pulser.setTimeout(newInterval);
				int interval = Main.pulser.getTimeout();
				p.sendMessage("tickinterval = " + interval + "ms = " + (interval / 1000) + "s = " + (interval / 60000) + "m");
			}else if(input[2].equals("get")){
				if(input.length > 3){
					p.sendMessage("§c$pulser tickinterval get");
				}
				int interval = Main.pulser.getTimeout();
				p.sendMessage("tickinterval = " + interval + "ms = " + (interval / 1000) + "s = " + (interval / 60000) + "m");
			}
		}else if(input[1].equals("on")){
			SettingsManager.EnablePulserSystem();
			p.sendMessage("Pulser is ingeschakeld");
		}else if(input[1].equals("off")){
			SettingsManager.DisablePulserSystem();
			p.sendMessage("Pulser is uitgeschakeld");
		}else if(input[1].equals("loadNotifications")){
			Main.pulser.loadNotifications();
			p.sendMessage("Notifications loaded");
		}else if(input[1].equals("resetNotifications")){
			Main.pulser.notifications = new PulserNotif[]{Pulser.AbonneerNotification, Pulser.DoneerNotification/*, Pulser.TestNotification*/};
			p.sendMessage("Notifications reset");
		}else if(input[1].equals("timesticked")){
			if(Main.pulser == null || Main.pulser.timesTicked == null){
				p.sendMessage("§4TimesTicked DataField onbereikbaar");
				return;
			}
			if(input.length < 3){
				p.sendMessage("§4$pulser timesticked <kartoffelid> [newValue]");
				return;
			}
			short kartoffelid;
			try{
				kartoffelid = Short.parseShort(input[2]);
			}catch(Exception e){
				p.sendMessage("§4Oncorrecte short");
				return;
			}
			
			if(kartoffelid < 0 || kartoffelid >= 400){
				p.sendMessage("Oncorrect KartoffelID");
			}
			
			if(input.length >= 4){
				int newvalue;
				try{
					newvalue = Integer.parseInt(input[3]);
				}catch(Exception e){
					p.sendMessage("§4Oncorrecte integer");
					return;
				}
				Main.pulser.timesTicked.setValue(kartoffelid, newvalue);
				p.sendMessage("Veranderingen uitgevoerd, value van " + kartoffelid + " is nu " + Main.pulser.timesTicked.getValue(kartoffelid));
			}else{
				p.sendMessage("Value van " + kartoffelid + " is " + Main.pulser.timesTicked.getValue(kartoffelid));
			}
		}else{
			p.sendMessage("§c$pulser <on|off|tick|tickinterval|notification|loadNotifications|timesticked> <...>");
		}
	}
	
	private static void executeDebugLORE(Player p, String[] input){
		ItemStack is = p.getInventory().getItemInHand();
		if(is == null){
			p.sendMessage("§4Item in hand is null");
			return;
		}
		List<String> lore = is.getItemMeta().getLore();
		if(lore == null)lore = new ArrayList<String>();
		
		if(input.length < 2){
			p.sendMessage("$lore <set|del|add|+>");
		}else{
			if(input[1].equals("set")){
				if(input.length != 4){
					p.sendMessage("§c$lore set <number> <text...>");
					return;
				}
				int index;
				try{
					index = Integer.parseInt(input[2]);
				}catch(Exception ex){
					p.sendMessage("§4Incorrecte index: " + input[2]);
					return;
				}
				if(index < 0){
					p.sendMessage("§4Index is kleiner dan 0: " + index);
					return;
				}
				if(index > lore.size()){//de index mag wel 1 hoger zijn dan de lengte van de lijst
					p.sendMessage("§4Index is te hoog omdate de size van de lore " + lore.size() + " is");
					return;
				}
				String loretext = input[3];
				for(int i = 4; i < input.length; i++){
					loretext += " " + input[i];
				}
				loretext = AdvancedChat.verkleurUitgebreid(loretext);
				if(index == lore.size()){
					lore.add(loretext);
				}else{
					lore.set(index, loretext);
				}
				ItemMeta im = is.getItemMeta();
				im.setLore(lore);
				is.setItemMeta(im);
			}else if(input[1].equals("del")){
				if(input.length != 3){
					p.sendMessage("§c$lore del <number>");
					return;
				}
				int index;
				try{
					index = Integer.parseInt(input[2]);
				}catch(Exception ex){
					p.sendMessage("§4Incorrecte index: " + input[2]);
					return;
				}
				if(index < 0){
					p.sendMessage("§4Index is kleiner dan 0: " + index);
					return;
				}
				if(lore == null || index >= lore.size()){
					p.sendMessage("§4Index is te hoog omdat de size van de lore " + (lore==null?0:lore.size()) + " is");
					return;
				}
				lore.remove(index);
				
				ItemMeta im = is.getItemMeta();
				im.setLore(lore);
				is.setItemMeta(im);
			}else if(input[1].equals("add")){
				if(input.length != 4){
					p.sendMessage("§c$lore add <number> <text...>");
					return;
				}
				int index;
				try{
					index = Integer.parseInt(input[2]);
				}catch(Exception ex){
					p.sendMessage("§4Incorrecte index: " + input[2]);
					return;
				}
				if(index < 0){
					p.sendMessage("§4Index is kleiner dan 0: " + index);
					return;
				}
				if(index > lore.size()){
					p.sendMessage("§4Index is te hoog omdat de size van de lore " + (lore==null?0:lore.size()) + " is");
					return;
				}
				String loretext = input[3];
				for(int i = 4; i < input.length; i++){
					loretext += " " + input[i];
				}
				loretext = AdvancedChat.verkleurUitgebreid(loretext);
				
				if(index == lore.size()){
					lore.add(loretext);
				}else{
					lore.add(index, loretext);;
				}
				ItemMeta im = is.getItemMeta();
				im.setLore(lore);
				is.setItemMeta(im);
			}else if(input[1].equals("+")){
				if(input.length < 3){
					p.sendMessage("§c$lore + <text...>");
					return;
				}

				String loretext = input[2];
				for(int i = 3; i < input.length; i++){
					loretext += " " + input[i];
				}
				loretext = AdvancedChat.verkleurUitgebreid(loretext);
				
				lore.add(loretext);
				ItemMeta im = is.getItemMeta();
				im.setLore(lore);
				is.setItemMeta(im);
			}
		}
	}
	@SuppressWarnings("deprecation")
	private static void executeDebugMARKFREE(Player p, String[] input){
		AttribSystem attrSys = new AttribSystem();
		input = attrSys.initialize(input, 1);		
		Inventory inv = null;
		int amount = attrSys.getIntValue("n", 1);
		String container = attrSys.getStringValue("container", "inv");
		if(container.equals("inv")){
			inv = p.getInventory();
		}else if(container.equals("block")){
			BlockState b;
			try{
				b = p.getLastTwoTargetBlocks(null, 100).get(1).getState();
			}catch(Exception e){
				p.sendMessage("§4Kon TargetBlock niet allocaten");
				return;
			}
			if(b == null){
				p.sendMessage("§4De BlockState van de TargetBlock is null");
			}
			if(b instanceof InventoryHolder){
				inv = ((InventoryHolder)b).getInventory();
			}else{
				p.sendMessage("§4Het Block is geen InventoryHolder");
				return;
			}
		}else{
			p.sendMessage("§4Onbekend type voor het attribuut \"container\": \"" + container + "\", mogelijke waardes zijn \"inv\" en \"block\"");
		}
		
		if(inv == null){
			p.sendMessage("§4Inventory is null");
			return;
		}
		
		String marking = "Gratis voedsel";
		if(input.length > 1){
			marking = input[1];
			for(int i = 2; i < input.length; ++i){
				marking += " " + input[i];
			}
			marking = AdvancedChat.verkleurUitgebreid(marking);
		}
		String operation = attrSys.getStringValue("operation", "add");
		if(inv instanceof PlayerInventory && amount == 1){
			ItemStack is = ((PlayerInventory)inv).getItemInHand();
			if(is == null || is.getAmount() == 0){
				p.sendMessage("Item in hand is null");
				return;
			}
			p.sendMessage("ItemMeta is " + is.getItemMeta());
			
			if(operation.equals("add")){
				List<String> lore = (is.getItemMeta().hasLore()?is.getItemMeta().getLore():new ArrayList<String>(1));
				lore.add(marking);
				ItemMeta a = is.getItemMeta();
				a.setLore(lore);
				is.setItemMeta(a);				
			}else if(operation.equals("set")){
				List<String> lore = new ArrayList<String>(1);
				lore = new ArrayList<String>(1);
				lore.add(marking);
				ItemMeta a = is.getItemMeta();
				a.setLore(lore);
				is.setItemMeta(a);
			}else if(operation.equals("del")){
				if(is.getItemMeta().hasLore()){
					List<String> lore = is.getItemMeta().getLore();
					if(lore != null && lore.size() > 0){
						for(int i = 0; i < lore.size(); i++){
							if(lore.get(i).equals(marking)){
								lore.remove(i);
								i--;
							}
						}
						ItemMeta a = is.getItemMeta();
						a.setLore(lore);
						is.setItemMeta(a);
					}
				}
			}else if(operation.equals("clear")){
				ItemMeta a = is.getItemMeta();
				a.setLore(new ArrayList<String>(0));
				is.setItemMeta(a);
			}else{
				p.sendMessage("§De operation is onbekend");
				return;
			}
		}else{
			ItemStack[] contents = inv.getContents();
			if(amount == -1)amount = Integer.MAX_VALUE;
			for(int i = 0; amount > 0 && i < contents.length; i++){
				ItemStack is = contents[i];
				if(is == null || is.getAmount() == 0){
					continue;
				}
				if(operation.equals("add")){
					List<String> lore = (is.getItemMeta().hasLore()?is.getItemMeta().getLore():new ArrayList<String>(1));
					lore.add(marking);
					ItemMeta a = is.getItemMeta();
					a.setLore(lore);
					is.setItemMeta(a);				
				}else if(operation.equals("set")){
					List<String> lore = new ArrayList<String>(1);
					lore = new ArrayList<String>(1);
					lore.add(marking);
					ItemMeta a = is.getItemMeta();
					a.setLore(lore);
					is.setItemMeta(a);
				}else if(operation.equals("del")){
					if(is.getItemMeta().hasLore()){
						List<String> lore = is.getItemMeta().getLore();
						if(lore != null && lore.size() > 0){
							for(int a = 0; a < lore.size(); a++){
								if(lore.get(a).equals(marking)){
									lore.remove(a);
									a--;
								}
							}
							ItemMeta a = is.getItemMeta();
							a.setLore(lore);
							is.setItemMeta(a);
						}
					}
				}else if(operation.equals("clear")){
					ItemMeta a = is.getItemMeta();
					a.setLore(new ArrayList<String>(0));
					is.setItemMeta(a);
				}else{
					p.sendMessage("§De operation is onbekend");
					return;
				}
				amount--;
			}
		}
		
		p.sendMessage("Veranderingen uitgevoerd");
	}
	
	private static void executeDebugADVCHAT(Player p, String[] input){
		if(input.length == 1){
			p.sendMessage("devadvchat: " + devadvancedchat);
		}else if(input.length == 2){
			if(input[1].equals("on")){
				devadvancedchat = true;
				Main.plugin.getServer().getPluginManager().registerEvents(DebugTools.ac, Main.plugin);				
				p.sendMessage("devadvchat = " + devadvancedchat);
			}else if(input[1].equals("off")){
				devadvancedchat = false;
				p.sendMessage("devadvchat = " + devadvancedchat);
			}
		}
	}
		
	private static void executeDebugDEV(Player p, String[] input){
		if(input.length == 1){
			p.sendMessage("developermodus: " + developermodus);
		}else if(input.length == 2){
			if(input[1].equals("on")){
				Main.plugin.ConfigureDeveloperModus(p);
				developermodus = true;
			}else if(input[1].equals("off")){
				developermodus = false;
				Main.pm.unloadPlayer(Person.DEV);
			}
			p.sendMessage("developermodus = " + developermodus);
		}else{
			p.sendMessage("$dev ['on'|'off']");
		}
	}
	
	private static void executeDebugSETTINGS(Player p, String[] input){	
		if(Main.sm == null){
			p.sendMessage("§4ERROR: SettingsManager is null");
			return;
		}
		if(input.length < 2){
			p.sendMessage("§c$settings <save|saveDefault|load|changed|edition|dailydiatime|startAutoAntilag|startPlayerSystem|startPulserSystem>");
			return;
		}
		if(input[1].equals("save")){
			Main.sm.saveBlocking();
			p.sendMessage("De Settings zijn bewaard");
		}else if(input[1].equals("saveDefault")){
			Main.sm.saveDefaultSettings();
			p.sendMessage("De Standaard Settings zijn bewaard");
		}else if(input[1].equals("load")){
			Main.sm.loadSettings();
			p.sendMessage("De Settings zijn geladen");
		}else if(input[1].equals("changed")){
			if(input.length < 3){
				p.sendMessage("§c$settings changed <get|set>");
				return;
			}
			if(input[2].equals("get")){
				p.sendMessage("Changed van Settings: " + Main.sm.changed);
			}else if(input[2].equals("set")){
				if(input.length < 4){
					p.sendMessage("§c$settings changed set <true|false>");
					return;
				}
				
				if(input[3].equals("true")){
					Main.sm.changed = true;
				}else if(input[3].equals("false")){
					Main.sm.changed = false;
				}else{
					p.sendMessage("§c$settings changed set <true|false>");
				}
			}else{
				p.sendMessage("§c$settings changed <get|set>");
			}
		}else if(input[1].equals("startAutoAntilag")){
			if(input.length < 3){
				p.sendMessage("§c$settings startAutoAntilag <get|set>");
				return;
			}
			if(input[2].equals("get")){
				p.sendMessage("startAutoAntilag: " + Main.sm.getStartAutoAntilag());
			}else if(input[2].equals("set")){
				if(input.length < 4){
					p.sendMessage("§c$settings startAutoAntilag set <true|false>");
					return;
				}
				
				if(input[3].equals("true")){
					Main.sm.setStartAutoAntilag(true);
				}else if(input[3].equals("false")){
					Main.sm.setStartAutoAntilag(false);
				}else{
					p.sendMessage("§c$settings startAutoAntilag set <true|false>");
				}
			}else{
				p.sendMessage("§c$settings startAutoAntilag <get|set>");
			}
		}else if(input[1].equals("startPlayerSystem")){
			if(input.length < 3){
				p.sendMessage("§c$settings startPlayerSystem <get|set>");
				return;
			}
			if(input[2].equals("get")){
				p.sendMessage("startPlayerSystem: " + Main.sm.getStartPlayerSystem());
			}else if(input[2].equals("set")){
				if(input.length < 4){
					p.sendMessage("§c$settings startPlayerSystem set <true|false>");
					return;
				}
				
				if(input[3].equals("true")){
					Main.sm.setStartPlayerSystem(true);
				}else if(input[3].equals("false")){
					Main.sm.setStartPlayerSystem(false);
				}else{
					p.sendMessage("§c$settings startPlayerSystem set <true|false>");
				}
			}else{
				p.sendMessage("§c$settings startPlayerSystem <get|set>");
			}
		}else if(input[1].equals("startPulserSystem")){
			if(input.length < 3){
				p.sendMessage("§c$settings startPulserSystem <get|set>");
				return;
			}
			if(input[2].equals("get")){
				p.sendMessage("startPulserSystem: " + Main.sm.getStartPulserSystem());
			}else if(input[2].equals("set")){
				if(input.length < 4){
					p.sendMessage("§c$settings startPulserSystem set <true|false>");
					return;
				}
				
				if(input[3].equals("true")){
					Main.sm.setStartPulserSystem(true);
				}else if(input[3].equals("false")){
					Main.sm.setStartPulserSystem(false);
				}else{
					p.sendMessage("§c$settings startPulserSystem set <true|false>");
				}
			}else{
				p.sendMessage("§c$settings startPulserSystem <get|set>");
			}
		}else if(input[1].equals("edition")){
			if(input.length < 3){
				p.sendMessage("§c$settings edition <get|set>");
				return;
			}
			if(input[2].equals("get")){
				if(Main.sm.res == null){
					p.sendMessage("De KartoffelFile is null");
				}else{
					SecureBestand sb = Main.sm.res.getResource();
					if(sb == null){
						p.sendMessage("De res in de KartoffelFile is null");
					}else{
						p.sendMessage("Edition van Settings: " + sb.getEdition());
					}
					p.sendMessage("LastEdition: " + Main.sm.res.lastEdition);
				}
			/*}else if(input[2].equals("set")){
				if(input.length < 4){
					p.sendMessage("§c$settings edition set <newValue>");
					return;
				}
				short newValue;
				try{
					newValue = Short.parseShort(input[3]);
				}catch(Exception e){
					p.sendMessage("§4Invalid Short");
					return;
				}
				Main.sm.edition = newValue;*/
			}else{
				p.sendMessage("§c$settings edition <get>");
			}
		}else if(input[1].equals("dailydiatime")){
			if(input.length < 3){
				p.sendMessage("§c$settings dailydiatime <getLong|getDate|getToday|setToday>");
				return;
			}
			
			if(input[2].equals("getLong")){
				p.sendMessage("DailyDia StartTime: " + Main.sm.dailydiamonddate);
			}else if(input[2].equals("getDate")){
				Date a = new Date(Main.sm.dailydiamonddate);
				p.sendMessage("DailyDia StartTime: " + a.toString());
			}else if(input[2].equals("getToday")){
				p.sendMessage("Vandaag is de " + Main.sm.getDailyDiaDay() + "th DailyDiaDay");
			}else if(input[2].equals("setToday")){
				if(input.length < 4){
					p.sendMessage("§c$settings dailydiatime setToday <short>");
					return;
				}
				
				short a;
				try{
					a = Short.parseShort(input[3]);
				}catch(Exception e){
					p.sendMessage("§4Invalid Short");
					return;
				}
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				
				Main.sm.setDailyDiaDay(a);
				
				p.sendMessage("DailyDia time is veranderd");
			}else{
				p.sendMessage("§c$settings dailydiatime <getLong|getDate|getToday|setToday>");
			}
		}else{
			p.sendMessage("§c$settings <save|changed|edition|dailydiatime|startAutoAntilag|startPlayerSystem|startPulserSystem>");
			return;
		}
	}
	
	private static void executeDebugSPELEROPTIONS(Player p, String[] input){
		
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e){
		if(Main.isDeveloper(e.getPlayer().getUniqueId())){
			if(e.getMessage() != null && e.getMessage().length() > 0 && e.getMessage().charAt(0) == '$'){
				DebugTools.executeCommand(e.getPlayer(), e.getMessage());
				e.getPlayer().sendMessage("DebugCommand verwerkt");
				e.setMessage("");
				e.setCancelled(true);
			}
		}
	}
	

	
}
