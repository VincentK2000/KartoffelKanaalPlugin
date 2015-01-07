package KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.SettingsManager;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.KartoffelService;

public class AutoAntilag extends KartoffelService implements Runnable{
	private Thread t = null;
	private long endtime = 0;
	private int timeout = -1;
	
	public AutoAntilag(){
		super("AutoAntilag");
	}
	
	public void initialize(int timeout){
		if(this.initialized)return;
		
		if(!this.isUsable()){
			Logger.getLogger("Minecraft").info("[KKP] AutoAntilag kan niet initialized worden omdat die onbruikbaar is geworden");
			return;
		}
		if(this.running){
			Logger.getLogger("Minecraft").info("[KKP] Kan AutoAntilag niet initializen als die aan staat");
			return;
		}
		if(t != null && t.isAlive())t.interrupt();
		this.t = null;
		this.timeout = timeout;
		this.endtime = 0;
		this.initialized = true;
	}
	
	public int getTimeout(){
		return this.timeout;
	}

	public void setTimeout(int newTimeout){
		this.timeout = (newTimeout < 30000)?30000:newTimeout;
		if(Main.sm != null)Main.sm.notifyChange();
	}

	public void setTimeout(int minutesTimeout, CommandSender cs){
		if(minutesTimeout < 1){
			SettingsManager.DisableAutoAntilag();
			cs.sendMessage("Automatische antilag is nu uitgeschakeld");
			return;
		}
		this.timeout = minutesTimeout * 60000;	
		Logger.getLogger("Minecraft").info("[KKP] De AutoAntilag timeout is veranderd naar " + minutesTimeout + " minuten (" + timeout + " milliseconden)");
		if(!(cs instanceof ConsoleCommandSender)){
			cs.sendMessage("§eDe AutoAntilag timeout is veranderend naar " + minutesTimeout + " minuten (" + timeout + " milliseconden)");
		}
		if(this.t != null && this.t.isAlive()){
			Logger.getLogger("Minecraft").info("[KKP] AutoAntilag wordt herstart vanwege veranderingen aan de timeout...");
			this.t.interrupt();
			this.t = new Thread(this);
			t.start();
		}else{
			cs.sendMessage("§4NOTE: AutoAntilag staat niet aan!");
		}
	}

	public long getEndtime(){
		return this.endtime;
	}

	public boolean isRunning(){
		return this.running;
	}

	public void run(){
		if(this.timeout < 30000)return;
		int failure = 0;
		Server s = Main.plugin.getServer();
		try{
			Logger.getLogger("Minecraft").info("[KKP] De AutoAntilag-loop is gestart");
			while(this.running){
				endtime = System.currentTimeMillis() + timeout;
				Thread.sleep(timeout);
				if(this.preventAction())return;
				//Logger.getLogger("Minecraft").info("[KKP] AutoAntilag wordt uitgevoerd...");
				try{
					s.dispatchCommand(s.getConsoleSender(), "nolagg gc");
				}catch(Throwable e){
					Logger.getLogger("Minecraft").warning("[KKP] Kon AutoAntilag niet uitvoeren: " + e);
					if(++failure >= 10){
						Logger.getLogger("Minecraft").warning("[KKP] AutoAntilag: Het is 10 keren niet gelukt de Antilag-Commando's uit te voeren. AutoAntilag gaat uit");
						this.DisableCrash(new Exception("Kon de Antilag-Commando's niet uit voeren. De \"Caused by\" error verwijst naar de " + failure + "ste error.", e));
					}
				}
			}
		}catch(Throwable e){
			Logger.getLogger("Minecraft").info("[KKP] De AutoAntilag-loop is gestopt");
			return;
		}
	}

	protected void _enableCore(){
		if(this.t == null || !this.t.isAlive()){
			this.t = new Thread(this);
			this.t.start();
		}
	}
	
	@Override
	protected void _disableCore() throws Exception {		
		if(this.t == null || !this.t.isAlive())return;
		t.interrupt();
	}
}
