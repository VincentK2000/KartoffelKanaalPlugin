package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.DataFieldShort;
import KartoffelKanaalPlugin.plugin.DebugTools;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.SpelerOptions;

public class CommandsPulser {
	public static PulserNotif activationComponent;
	
	public static void executePulserCommand(Person p, CommandSender a, AttribSystem attribSys, String[] args){
		if(Main.pulser == null){
			a.sendMessage("§4De pulser is momenteel niet beschikbaar");
		}
		
		if(!a.isOp() && !(a instanceof Player && Main.isDeveloper(((Player)a).getUniqueId()) && DebugTools.developermodus)){
			a.sendMessage("§4Je hebt geen permission om het Pulser-commando te gebruiken");
			return;
		}
		if(args.length == 0){
			a.sendMessage("§c/pulser [start|stop|tick|msg] [...]");
			return;
		}
		String search = args[0].toLowerCase();
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		/*if(search.equals("notifications")){
			CommandsPulser.executePulserNotificationsCommand(p, a, newArgs);
		}else */if(search.equals("start")){
			CommandsPulser.executePulserStartCommand(p, a, newArgs);
		}else if(search.equals("stop")){
			CommandsPulser.executePulserStopCommand(p, a, newArgs);
		}else if(search.equals("tick")){
			CommandsPulser.executePulserTickCommand(p, a, newArgs);
		}else if(search.equals("msg")){
			CommandsPulser.executePulserMsgCommand(p, a, newArgs);
		}else if(search.equals("_activation")){
			if(activationComponent == null){
				a.sendMessage("§4ERROR: ActivationComponent is null!");
				return;
			}
			try {
				activationComponent.activate(a, attribSys);
			} catch (Exception e) {
				a.sendMessage("§4Fout: " + e.getMessage());
			}
		}else{
			a.sendMessage("§c/pulser [start|stop|tick|msg] [...]");
		}
	}
	public static void executePulserStartCommand(Person p, CommandSender a, String[] args){
		a.sendMessage("§4Commando nog niet actief");
	}
	
	public static void executePulserStopCommand(Person p, CommandSender a, String[] args){
		a.sendMessage("§4Commando nog niet actief");
	}
	
	public static void executePulserTickCommand(Person p, CommandSender a, String[] args){
		a.sendMessage("§4Commando nog niet actief");
	}
	
	public static void executePulserMsgCommand(Person p, CommandSender a, String[] args){
		a.sendMessage("§4Commando nog niet actief");
	}
	
	/*public static void executePulserNotificationsCommand(Person p, CommandSender a, String[] args){
		if(args.length == 0){
			a.sendMessage("§c/pulser notification <list|add|remove|info|edit|(de)activate> <...>");
			return;
		}
		String search = args[0].toLowerCase();
		if(search.equals("list")){
			if(args.length > 1){
				a.sendMessage("§c/pulser notification list");
				return;
			}
			
			
			
			
			
			
			return;
		}else if(search.equals("add")){
			CommandsPulser.executePulserNotificationAddCommand(p, a, args);
		}
				
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		if(search.equals("remove")){
			CommandsPulser.executePulserNotificationRemoveCommand(p, a, newArgs);
		/*}else if(search.equals("info")){
			CommandsPulser.executePulserNotificationInfoCommand(p, a, newArgs);
		}else if(search.equals("get")){
			CommandsPulser.executePulserNotificationGetCommand(p, a, newArgs);
		}else if(search.equals("set")){
			CommandsPulser.executePulserNotificationSetCommand(p, a, newArgs);
		}else if(search.equals("activate")){
			CommandsPulser.executePulserNotificationActivationCommand(p, a, newArgs, true);
		}else if(search.equals("deactivate")){
			CommandsPulser.executePulserNotificationActivationCommand(p, a, newArgs, false);*//*
		}else{
			a.sendMessage("§c/pulser notification <list|add|remove|info|edit|(de)activate> <...>");
		}
	}*/
	
	public static void executePulserNotifsCommand(Person p, CommandSender a, AttribSystem attribSys, String[] args){
		args[0] = args[0].toLowerCase();
		if(Main.pulser == null || Main.pulser.preventAction()){
			a.sendMessage("§4De Pulser is niet beschikbaar");
			return;
		}
		
		if(Main.pm == null || Main.pm.preventAction()){
			a.sendMessage("§4De PlayerManager is niet beschikbaar");
			return;
		}
		
		if(args.length < 1){
			a.sendMessage("§c/notifications <receive|§opath§c> <...>");
			return;
		}
		
		if(args[0].equals("receive")){
			if(args.length < 2){
				a.sendMessage("§c/notifications receive <notifID> [person] [§oon§r§c|§ooff§r§c]");
				return;
			}
			int notifID;
			try{
				notifID = Integer.parseInt(args[1]);
			}catch(Exception e){
				a.sendMessage("§4Het notifID is geen correct nummer");
				return;
			}
			
			DataFieldShort res = Main.pulser.receiveNotifications;
			if(res == null){
				a.sendMessage("§4De DataField voor de receiveNotifications is niet beschikbaar");
				return;
			}
			
			if(args.length < 3){
				executePulserNotifReceiveInfoCommand(p, a, p, notifID);
			}else{
				String s = args[2].toLowerCase();
				if(s.equals("on")){
					executePulserNotifReceiveChangeCommand(p, a, p, notifID, true);
				}else if(s.equals("off")){
					executePulserNotifReceiveChangeCommand(p, a, p, notifID, false);
				}else{
					if(!a.isOp()){
						a.sendMessage("§4Je hebt geen toegang om de notifReceivements van anderen te beheren");
						return;
					}
					
					if(args.length > 4){
						a.sendMessage("§c/notifications receive <notifID> [person] [§oon§r§c|§ooff§r§c]");
						return;
					}
					Person affected = Main.pm.getPlayer(args[2]);
					if(affected == null){
						a.sendMessage("§4De persoon is niet gevonden");
						return;
					}
					affected.sessionSys.acquireAccess();
					
					if(args.length == 3){
						executePulserNotifReceiveInfoCommand(p, a, affected, notifID);
					}else{
						s = args[3].toLowerCase();
						if(args[3].equals("on")){
							executePulserNotifReceiveChangeCommand(p, a, affected, notifID, true);
						}else if(args[3].equals("off")){
							executePulserNotifReceiveChangeCommand(p, a, affected, notifID, false);
						}else{
							a.sendMessage("§c/notifications receive <notifID> [person] [§oon§r§c|§ooff§r§c]");
						}
					}
					affected.sessionSys.releaseAccess();
				}
			}
			
		}else{
			/*int i;
			try{
				i = Integer.parseInt(args[0]);
			}catch(Exception e){
				a.sendMessage("§c/notifications <receive|§onotifID§c> <...>§e of incorrecte integer");
				return;
			}
			if(i < 0 || i >= Main.pulser.notifications.length){
				a.sendMessage("§4Correcte notifID's moeten minimum 0 en in dit geval maximum " + (Main.pulser.notifications.length - 1) + " zijn");
				return;
			}*/
			if(p.getSpelerOptions().getOpStatus() < 2){
				a.sendMessage("§4Je hebt geen toegang tot dit commando");
				return;
			}
			IObjectCommandHandable n;
			try{
				n = Pulser.getObjectCommandHandable(Main.pulser, args[0]);
			}catch(Exception e){
				a.sendMessage("§4Kon niks vinden op dat path: " + e.getMessage());
				return;
			}
			if(n == null){
				a.sendMessage("§4Er is niks gevonden op dat path");
				return;
			}
			if(args.length < 2){
				try{
					a.sendMessage("§eBeschikbare commando's: §c" + Pulser.formatListToString(n.autoCompleteObjectCommand(new String[]{""}, new ArrayList<String>(5)), "§e, §c"));
				}catch(Exception e){
					a.sendMessage("§4Kon beschikbare commando's niet weergeven: " + e.getMessage());
				}
				return;
			}
			
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			newArgs[0] = newArgs[0].toLowerCase();
			try{
				if(!n.handleObjectCommand(p, a, attribSys, newArgs)){
					a.sendMessage("§4Onbekend commando voor dat element");
				}
			}catch(Exception e){
				a.sendMessage("§4Fout: " + e.getMessage());
			}
		}
	}
	
	protected static void executePulserNotifReceiveInfoCommand(Person executor, CommandSender a, Person affected, int notifID){
		if(a == null)return;
		if(executor == null){
			a.sendMessage("§4De meegegeven executor is null, gelieve dit te contacteren aan de Developer");
		}
		if(affected == null){
			a.sendMessage("§4Error: De affected SpelerOptions is niet meegegeven, gelieve dit te contacteren aan de Developer");
			return;
		}
		if(Main.pulser == null || Main.pulser.preventAction()){
			a.sendMessage("§4De Pulser is niet beschikbaar");
		}
		if(notifID < -1/*Als het -1 is worden alle states weergeven*/ || notifID > 15){
			a.sendMessage("§4Oncorrect notifID, het moet minimum 0 en maximum 15 zijn");
			return;
		}
		SpelerOptions exe = executor.getSpelerOptions();
		SpelerOptions aff = affected.getSpelerOptions();
		if(!a.isOp() && exe != aff){
			a.sendMessage("§4Je hebt geen toegang om dit te bekijken");
			return;
		}
		DataFieldShort res = Main.pulser.receiveNotifications;
		if(res == null){
			a.sendMessage("§4De DataFieldShort voor de receiveNotifications is null");
			return;
		}
		short result = res.getValue(affected.getKartoffelID());
		if(notifID == -1){
			if(Main.pulser.notifications == null || Main.pulser.notifications.length == 0){
				a.sendMessage("§eEr zijn geen notificaties aanwezig");
			}
			a.sendMessage("§eDit zijn de states van de notificaties voor de persoon:");
			
			short m;
			for(int i = 0; i < Main.pulser.notifications.length && i < 16; i++){
				m = (short) (0x8000 >>> i);
				a.sendMessage("De state van notification " + i + ": " + (((result & m) == m)?"§2Aan":"§4Uit"));
			}
		}else{
			short notifMask = (short) (0x8000 >>> notifID);
			if(exe == aff){
				a.sendMessage("Je ontvangt de notification " + notifID + " " + (((result & notifMask) == notifMask)?"§2wel":"§4niet"));
			}else{
				a.sendMessage("De persoon ontvangt de notification " + notifID + " " + (((result & notifMask) == notifMask)?"§2wel":"§4niet"));
			}
		}		
	}
	
	protected static void executePulserNotifReceiveChangeCommand(Person executor, CommandSender a, Person affected, int notifID, boolean newValue){
		if(a == null)return;
		if(executor == null){
			a.sendMessage("§4De meegegeven executor is null, gelieve dit te contacteren aan de Developer");
		}
		if(affected == null){
			a.sendMessage("§4Error: De affected SpelerOptions is niet meegegeven, gelieve dit te contacteren aan de Developer");
			return;
		}
		if(Main.pulser == null || Main.pulser.preventAction()){
			a.sendMessage("§4De Pulser is niet beschikbaar");
		}
		if(notifID < 0 || notifID > 15){
			a.sendMessage("§4Oncorrect notifID, het moet minimum 0 en maximum 15 zijn");
			return;
		}
		SpelerOptions exe = executor.getSpelerOptions();
		SpelerOptions aff = affected.getSpelerOptions();
		
		DataFieldShort res = Main.pulser.receiveNotifications;
		if(res == null){
			a.sendMessage("§4De DataFieldShort voor de receiveNotifications is null");
			return;
		}
		short result = res.getValue(affected.getKartoffelID());
		short notifMask = (short) (0x8000 >>> notifID);
		if(exe == aff){
			if(!a.isOp() && !aff.canChangeNotifReceivement(notifID)){
				a.sendMessage("§4Je hebt geen toegang om die NotifReceivement te veranderen");
				return;
			}
			if(newValue){
				if(!aff.getSwitch((byte) 0x50, false))aff.setSwitch((byte) 0x50, true, false);
				
				if((result & notifMask) == notifMask){
					a.sendMessage("§eHet ontvangen van die notification voor jou stond al aan");
				}else{
					res.setValue(affected.getKartoffelID(), (short) (result | notifMask));
					a.sendMessage("§eVerandering uitgevoerd");
				}
			}else{
				if((result & notifMask) == 0x00){
					a.sendMessage("§eHet ontvangen van die notification voor jou stond al uit");
				}else{
					short abc = (short) (result & ~notifMask);
					res.setValue(affected.getKartoffelID(), abc);
					if(abc == 0x0000 && aff.getSwitch((byte) 0x50, false))aff.setSwitch((byte) 0x50, false, false);
					a.sendMessage("§eVerandering uitgevoerd");
				}
			}
		}else{
			if(!a.isOp()){
				a.sendMessage("§4Je hebt geen toegang om andermans NotifReceivements te veranderen");
				return;
			}
			if(newValue){
				if(!aff.getSwitch((byte) 0x50, false))aff.setSwitch((byte) 0x50, true, false);
				
				if((result & notifMask) == notifMask){
					a.sendMessage("§eHet ontvangen van die notification stond al aan voor die persoon");
				}else{
					res.setValue(affected.getKartoffelID(), (short) (result | notifMask));
					a.sendMessage("§eVerandering uitgevoerd");
				}
			}else{
				if((result & notifMask) == 0x00){
					a.sendMessage("§eHet ontvangen van die notification stond al uit voor die persoon");
				}else{
					short abc = (short) (result & ~notifMask);
					res.setValue(affected.getKartoffelID(), abc);
					if(abc == 0x0000 && aff.getSwitch((byte) 0x50, false))aff.setSwitch((byte) 0x50, false, false);
					a.sendMessage("§eVerandering uitgevoerd");
				}
			}
		}
	}
	
	protected static PulserNotif getPulserNotification(String s, CommandSender a){
		if(Main.pulser == null)return null;
		if(Main.pulser.notifications == null || Main.pulser.notifications.length == 0){
			a.sendMessage("§4De Advertisement Notifications lijst is leeg");
			return null;
		}
		int index;
		try{
			index = Integer.parseInt(s.substring(5));
		}catch(NumberFormatException e){
			a.sendMessage("§4Onjuiste Notification Adress \"" + s + "\". Juist Notification Adress: <adver|news>:<index>");
			return null;
		}
		if(index < 0 || Main.pulser.notifications.length >= index){
			a.sendMessage("§4Onjuiste index, de index moet minimum 0 zijn en maximum " + (Main.pulser.notifications.length - 1) + " zijn");
			return null;
		}
		if(Main.pulser.notifications[index] == null){
			a.sendMessage("§4De geselecteerde Notification is leeg");
		}
		return Main.pulser.notifications[index];
	}
	
	public static void executePulserNotificationAddCommand(Person p, CommandSender a, String[] args){
		
	}
	
	public static void executePulserNotificationRemoveCommand(Person p, CommandSender a, String[] args){
		if(args.length < 1){
			a.sendMessage("§4/pulser notification remove <notificationAdress>");
			return;
		}
		PulserNotif notification = CommandsPulser.getPulserNotification(args[0], a);
		if(notification == null){
			a.sendMessage("§c/pulser notification remove <notificationAdress>");
			return;
		}
	}
	
	/*public static void executePulserNotificationInfoCommand(Person p, CommandSender a, String[] args){
		if(args.length < 1){
			a.sendMessage("§4/pulser notification info <notificationAdress>");
			return;
		}
		PulserNotif notification = CommandsPulser.getPulserNotification(args[0], a);
		if(notification == null){
			a.sendMessage("§c/pulser notification info <notificationAdress>");
			return;
		}
		notification.executeInfoCommand(a, args);
	}
	
	public static void executePulserNotificationGetCommand(Person p, CommandSender a, String[] args){
		if(args.length < 1){
			a.sendMessage("§4/pulser notification get <notificationAdress> <...>");
			return;
		}
		PulserNotif notification = CommandsPulser.getPulserNotification(args[0], a);
		if(notification == null){
			a.sendMessage("§c/pulser notification get <notificationAdress> <...>");
			return;
		}
		notification.executeGetCommand(a, args);
	}
	
	public static void executePulserNotificationSetCommand(Person p, CommandSender a, String[] args){
		if(args.length < 1){
			a.sendMessage("§4/pulser notification set <notificationAdress> <...>");
			return;
		}
		PulserNotif notification = CommandsPulser.getPulserNotification(args[0], a);
		if(notification == null){
			a.sendMessage("§c/pulser notification set <notificationAdress> <...>");
			return;
		}

		notification.executeSetCommand(a, args);
	}

	
	public static void executePulserNotificationActivationCommand(Person p, CommandSender a, String[] args, boolean activate){
		String function = (activate)?"activate":"deactivate";
		if(args.length < 1){
			a.sendMessage("§4/pulser notification " + function + " <notificationAdress>");
			return;
		}
		PulserNotif notification = CommandsPulser.getPulserNotification(args[0], a);
		if(notification == null){
			a.sendMessage("§c/pulser notification " + function + " <notificationAdress>");
			return;
		}

		notification.executeActivationCommand(a, args);
	}*/
}
