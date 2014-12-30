package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

public class AutoSaver implements Runnable{
	private PlayerManager pm;
	private Thread t;
	private static final int autoSaveInterval = 120000;
	
	private static final short maximumPassiveLoaded = 10;
	
	public AutoSaver(PlayerManager pm){
		this.pm = pm;
	}
	
	
	@Override
	public void run(){
		if(this.t != Thread.currentThread())return;
		try{
			while(true){
				if(this.pm == null || this.pm.loadedPlayers == null || this.pm.loadedPlayers.size() == 0)return;
				Person c = null;
				int passiveAmount = 0;
				
				for(int i = (this.pm.loadedPlayers.size() - 1); i >= 0; i--){
					c = this.pm.loadedPlayers.get(i);
					if(c == null){
						this.pm.loadedPlayers.remove(i);
						i++;
						continue;
					}
					if(c.getKartoffelID() < 0){
						continue;
					}
					if(c.checkCorrectAutoSaveConditions() || (c.isChanged() && (c.useFinished() || (!c.isCurrentlyUsed() && (++passiveAmount > AutoSaver.maximumPassiveLoaded))))){
						this.pm.saver.add(c);
					}else if(c.useFinished() || (!c.isCurrentlyUsed() && (++passiveAmount > AutoSaver.maximumPassiveLoaded))){
						this.pm.unloadPlayer(c);
					}
				}
				Thread.sleep(AutoSaver.autoSaveInterval);
			}
		}catch(Exception e){}
		
	}


	public void start(){
		if(this.t != null && this.t.isAlive())return;
		this.t = new Thread(this);
		this.t.start();
	}
	
	public void stop(){
		if(this.t != null && this.t.isAlive()){
			this.t.interrupt();
		}
	}
	
	public void checkConditions(){
		if(this.pm == null || this.pm.loadedPlayers == null || this.pm.loadedPlayers.size() == 0){
			this.stop();
		}else{
			for(int i = 0; i < this.pm.loadedPlayers.size(); i++){
				if(this.pm.loadedPlayers.get(i) != null && this.pm.loadedPlayers.get(i).getKartoffelID() >= 0){
					this.start();
					return;
				}
			}
		}
	}
}
