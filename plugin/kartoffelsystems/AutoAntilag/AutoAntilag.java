package KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag;

import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.KartoffelService;

import java.util.logging.Logger;

public class AutoAntilag extends KartoffelService implements Runnable {
	public AutoAntilagLoop firstThread = null;
	public AutoAntilagLoop secondThread = null;
	
	public AutoAntilag(){
		super("AutoAntilag");
	}
	
	public void initialize(){
		if(this.initialized)return;
		if(!this.isUsable()){
			Logger.getLogger("Minecraft").info("[KKP] AutoAntilag kan niet initialized worden omdat die onbruikbaar is");
			return;
		}
		if(this.running){
			Logger.getLogger("Minecraft").info("[KKP] Kan AutoAntilag niet initializen als die aan staat");
			return;
		}
		this.initialized = true;
	}

	public void startThreads(){
		Logger.getLogger("Minecraft").info("[KKP] AutoAntilag-Loops starten...");

		if (this.firstThread == null) this.firstThread = new AutoAntilagLoop(this, "Eerste AutoAntilag-Loop (/lagg gc)", new String[]{"lagg gc"}, Main.sm.loadFirstAutoAntilagIntervalFromFile());
		if (this.secondThread == null) this.secondThread = new AutoAntilagLoop(this, "Tweede AutoAntilag-Loop (/lagg unloadchunks)", new String[]{"lagg unloadchunks"}, Main.sm.loadSecondAutoAntilagIntervalFromFile());

		this.firstThread._start();

		if(this.firstThread.getTimeout() >= 30000 && this.secondThread.getTimeout() >= 30000) {
			Logger.getLogger("Minecraft").info("[KKP] '5 seconden'-timeout voordat de tweede AutoAntilag-Loop gestart wordt...");
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				Logger.getLogger("Minecraft").info("[KKP] Opstarten van de AutoAntilag-Loops gestopt tijdens '5 seconden'-timeout");
				return;
			}
		}

		this.secondThread._start();

		Logger.getLogger("Minecraft").info("[KKP] AutoAntilag-Loops gestart");
	}

	public void stopThreads(){
		if(this.firstThread != null)this.firstThread._stop();
		if(this.secondThread != null)this.secondThread._stop();
		Logger.getLogger("Minecraft").info("[KKP] AutoAntilag-Loops gestopt");
	}

	@Override
	public void run(){
		for(int i = 0; i < 200; i++){
			if(this.isServiceRunning())break;
			try {
				Thread.sleep(50L);
			}catch(Exception e){}
		}

		try {
			this.startThreads();
		}catch (Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon de AutoAntilag-Loops niet starten: " + e.getMessage());
		}
	}

	@Override
	protected void _enableCore(){(new Thread(this)).start();}
	
	@Override
	protected void _disableCore() throws Exception { this.stopThreads(); }
}
