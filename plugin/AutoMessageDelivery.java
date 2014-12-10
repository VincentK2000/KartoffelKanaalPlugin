package KartoffelKanaalPlugin.plugin;

public class AutoMessageDelivery{}

/*package me.vincentk.kartoffelkanaalmod.Standard;

import org.bukkit.entity.Player;
import java.util.ArrayList;

public class AutoMessageDelivery {
	private static ArrayList<IMessageDeliverService> deliveradresses = new ArrayList<IMessageDeliverService>();
	protected static String[] geenNotificatie = new String[]{"marss121", "laurenswolfert", "miqel98", "JellevdAakster"};

	
	public void AddListener(IMessageDeliverService a){
		if(deliveradresses.contains(a)){
			return;
		}
		deliveradresses.add(a);
	}
	protected static void Start(){
		for(int i = 0; i < deliveradresses.size(); i ++){
			deliveradresses.get(i).ActivateService();
		}
	}
	protected static void Stop(){
		for(int i = 0; i < deliveradresses.size(); i ++){
			deliveradresses.get(i).DeactivateService();
		}
	}
	public void RemoveListener(IMessageDeliverService a){
		if(deliveradresses.contains(a)){
			deliveradresses.remove(a);
		}
	}
	public static void sendBroadcastMessage(String message){
		for(Player a:Main.plugin.getServer().getOnlinePlayers()){
			if(geenNotificatie(a.getName()))continue;
			a.sendMessage(message);
		}
	}
	public static void playerLoggedIn(Player p){
		if(!geenNotificatie(p.getName())/* || enabled*//*){
			Start();
		}
	}
	
	public static void playerLoggedOut(Player p){
		checkConditions(p);
	}
	public static void checkConditions(Player without){
		if(numberOfAvailablePlayers(without) > 0/* && enabled*//*){
			Start();
		}else{
			Stop();
		}
	}
	
	public static boolean geenNotificatie(String name){
		if(geenNotificatie == null)return false;
		for(int i = 0; i < geenNotificatie.length; i++){
			if(geenNotificatie[i].equals(name))return true;
		}
		return false;
	}
	
	public static int numberOfAvailablePlayers(Player without){
		int aantalSpelers = Main.plugin.getServer().getOnlinePlayers().length;
		if(without != null){
			aantalSpelers--;
		}
		for(Player p : Main.plugin.getServer().getOnlinePlayers()){
			if(p == without)continue;
			if(geenNotificatie(p.getName())){
				aantalSpelers--;
			}
		}
		return aantalSpelers;
	}
}*/
