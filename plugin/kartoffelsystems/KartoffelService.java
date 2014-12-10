package KartoffelKanaalPlugin.plugin.kartoffelsystems;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

public abstract class KartoffelService{
	public boolean running = false;
	protected Exception stopException = null;
	protected long stopTime;
	protected boolean initialized = false;
	private boolean _cleanState = true;
	
	protected final ReentrantLock changingState = new ReentrantLock();
	protected boolean isEnabling;
	
	protected final String serviceName;
	
	protected KartoffelService(String serviceName){
		this.serviceName = (serviceName == null)?"Onbekend":serviceName;
	}
	
	public boolean preventAction() {
		return !this.running && this._cleanState;
	}
	
	public boolean isRunning(){
		return this.running;
	}
	
	public void _Enable(){
		if(!this.initialized){
			Logger.getLogger("Minecraft").warning("[KKP] Kan de service " + this.serviceName + " niet aanzetten als die nog niet initialized is");
		}
		
		if(this.running){
			Logger.getLogger("Minecraft").info("[KKP] De service " + this.serviceName + " kon niet worden opgestart aangezien die al opgestart is.");
			return;
		}
		
		long startTime = System.currentTimeMillis();
		
		if(!this._cleanState){
			Logger.getLogger("Minecraft").warning("[KKP] Kan de service " + this.serviceName + " niet opstarten omdat de huidige instance gecrasht is, er moet een nieuwe instance van worden gemaakt");
			return;
		}
		this.stopException = null;
		
		Logger.getLogger("Minecraft").info("[KKP] Enabling " + this.serviceName + "...");
		Thread t = null;
		try{
			/*boolean illegalStarted = true;
			try{
				for(int i = 0; i < 60; i++){
					if(this.changingState.tryLock(1, TimeUnit.SECONDS)){
						illegalStarted = false;
						break;
					}
				}
			}catch(Exception e){}
			if(illegalStarted)Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + " is aan het opstarten zonder toestemming, waarschijnlijk omdat de toestemming niet verkregen kon worden binnen de 60 seconden");*/
			if(!this.changingState.tryLock(5000L, TimeUnit.SECONDS)){
				Logger.getLogger("Minecraft").info("[KKP] Kan " + this.serviceName + " niet inschakelen omdat de service al bezig zou zijn met inschakelen of uitschakelen");
			}
			
			this.isEnabling = true;
			t = new Thread(new KartoffelServiceEnabler(this));
			t.start();
			
			for(int i = 0; i < 190; i++){
				t.join(100);
				if(!t.isAlive()){
					break;
				}
			}
			
			if(!this._cleanState){
				try{
					this.changingState.unlock();
				}catch(Exception exception){}
				return;
			}
			
			if(t.isAlive()){
				int seconds = 19;
				while(seconds < 180){
					t.join(1000);
					if(!t.isAlive()){
						break;
					}		
					seconds++;
					
					if((seconds % 10) == 0){
						Logger.getLogger("Minecraft").warning("[KKP] De service " + this.serviceName + " is al " + seconds + " bezig, de operatie zal geforceerd stoppen binnen de " + (180 - seconds) + " seconden");
					}
				}
				
				if(seconds >= 180){
					Logger.getLogger("Minecraft").warning("[KKP] De service " + this.serviceName + " is al 3 minuten bezig geweest met zichzelf aan te zetten. Deze operatie zal geannuleerd worden en de service zal gecrasht gestopt worden.");
					if(t != null && t.isAlive())t.interrupt();
					this.running = false;
					this._cleanState = false;
					this.stopException = new Exception("De service kon niet opgestart worden doordat het opstarten te lang duurde");
					try{
						this.changingState.unlock();
					}catch(Exception exception){}
					
					return;
				}
			}
			
		}catch(Exception e){
			if(t != null && t.isAlive())t.interrupt();
			this.running = false;
			this._cleanState = false;
			this.stopException = new Exception("Kon de service niet correct opstarten", e);
			Logger.getLogger("Minecraft").warning("[KKP] Kon " + this.serviceName + " niet correct opstarten: " + e);
			Logger.getLogger("Minecraft").warning("[KKP] StackTrace van " + this.serviceName + ":");
			e.printStackTrace();
			
			try{
				this.changingState.unlock();
			}catch(Exception exception){}
			
			return;
		}
		
		if(t != null && t.isAlive())t.interrupt();
		
		try{
			this.changingState.unlock();
		}catch(Exception exception){}
		
		if(!this._cleanState)return;
		this.running = true;
		Logger.getLogger("Minecraft").info("[KKP] Enabled " + this.serviceName + " in " + (System.currentTimeMillis() - startTime) + " milliseconden");
	}
	
	/*private void EnableSys(){
		Logger.getLogger("Minecraft").info("[KKP] Enabling " + this.serviceName + "...");
		this.serviceStateHandler.running = true;
		try{
			this._enableCore();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + " kon niet worden opgestart");
			try{
				this.serviceStateHandler.stopSystemError(e);
			}catch(Exception ex){
				Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + " kon niet de nodige maatregelen nemen nu de PlayerManager niet kon opstarten");
			}
		}
		try{
			this.changingState.unlock();
		}catch(Exception e){}
		Logger.getLogger("Minecraft").info("[KKP] Enabled " + this.serviceName);
	}*/
	
	public void _Disable(){
		if(!this.running){
			Logger.getLogger("Minecraft").info("[KKP] De service " + this.serviceName + " kon niet worden afgesloten aangezien die al afgesloten is.");
			return;
		}
		
		long startTime = System.currentTimeMillis();
		
		if(!this._cleanState){
			Logger.getLogger("Minecraft").warning("[KKP] De service " + this.serviceName + " hoort niet opgestart te zijn wegens een gecrashte state, maar het afsluiten wordt toch voortgezet");
		}
		this.stopException = null;
		this.running = false;
		
		Logger.getLogger("Minecraft").info("[KKP] Disabling " + this.serviceName + "...");
		Thread t = null;
		try{
			/*boolean illegalStarted = true;
			try{
				for(int i = 0; i < 60; i++){
					if(this.changingState.tryLock(1, TimeUnit.SECONDS)){
						illegalStarted = false;
						break;
					}
				}
			}catch(Exception e){}
			if(illegalStarted)Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + " is aan het opstarten zonder toestemming, waarschijnlijk omdat de toestemming niet verkregen kon worden binnen de 60 seconden");*/
			if(!this.changingState.tryLock(5000L, TimeUnit.SECONDS)){
				Logger.getLogger("Minecraft").info("[KKP] Kon " + this.serviceName + " niet uitschakelen omdat de service al bezig zou zijn met inschakelen of uitschakelen");
			}
			
			this.isEnabling = false;
			t = new Thread(new KartoffelServiceDisabler(this));
			t.start();
			
			for(int i = 0; i < 190; i++){
				t.join(100);
				if(!t.isAlive()){
					break;
				}
			}
			
			if(!this._cleanState){
				try{
					this.changingState.unlock();
				}catch(Exception exception){}
				return;
			}
			
			if(t.isAlive()){
				int seconds = 19;
				while(seconds < 180){
					t.join(1000);
					if(!t.isAlive()){
						break;
					}		
					seconds++;
					
					if((seconds % 10) == 0){
						Logger.getLogger("Minecraft").warning("[KKP] De service " + this.serviceName + " is al " + seconds + " bezig, de operatie zal geforceerd stoppen binnen de " + (180 - seconds) + " seconden");
					}
				}
				
				if(seconds >= 180){
					Logger.getLogger("Minecraft").warning("[KKP] De service " + this.serviceName + " is al 3 minuten bezig geweest met zichzelf aan te zetten. Deze operatie zal geannuleerd worden en de service zal gecrasht gestopt worden.");
					if(t != null && t.isAlive())t.interrupt();
					this.running = false;
					this._cleanState = false;
					this.stopException = new Exception("De service kon niet afgesloten worden doordat het afsluiten te lang duurde");
					try{
						this.changingState.unlock();
					}catch(Exception exception){}
					
					return;
				}
			}
			
		}catch(Exception e){
			if(t != null && t.isAlive())t.interrupt();
			this.running = false;
			this._cleanState = false;
			this.stopException = new Exception("Kon de service niet correct afsluiten", e);
			Logger.getLogger("Minecraft").warning("[KKP] Kon " + this.serviceName + " niet correct afsluiten: " + e);
			Logger.getLogger("Minecraft").warning("[KKP] StackTrace van " + this.serviceName + ":");
			e.printStackTrace();
			
			try{
				this.changingState.unlock();
			}catch(Exception exception){}
			
			return;
		}
		
		try{
			this.changingState.unlock();
		}catch(Exception exception){}
		
		if(t != null && t.isAlive())t.interrupt();
		if(!this._cleanState)return;
		this.running = false;
		this.stopTime = System.currentTimeMillis();
		Logger.getLogger("Minecraft").info("[KKP] Disabled " + this.serviceName + " in " + (System.currentTimeMillis() - startTime) + " milliseconden");
	}
	
	public void DisableCrash(Exception e){
		this._cleanState = false;
		this.running = false;
		if(this.stopException != null){
			Logger.getLogger("Minecraft").warning("[KKP] Het systeem verzond een nieuwe stopRequest, maar het systeem was al gestopt. Binnengekomen stopRequest: " + e);
			return;
		}
		this.stopTime = System.currentTimeMillis();
		this.stopException = e;
		if(this.stopException == null){
			Logger.getLogger("Minecraft").warning("[KKP] De service " + this.serviceName + " is gestopt vanwege een crash, de crashdata is niet meegegeven.");
		}else{
			Logger.getLogger("Minecraft").warning("[KKP] De service " + this.serviceName + " is gestopt vanwege een error: " + e);
			Logger.getLogger("Minecraft").warning("[KKP] Dit is de StackTrace waarom de service " + this.serviceName + " is gecrasht:");
			this.stopException.printStackTrace();
		}
		return;
	}
	
	/*private void DisableSys(){
		boolean illegalStarted = true;
		try{
			for(int i = 0; i < 60; i++){
				if(this.changingState.tryLock(1, TimeUnit.SECONDS)){
					illegalStarted = false;
					break;
				}
			}
		}catch(Exception e){}
		if(illegalStarted)Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + " is aan het afsluiten zonder toestemming, waarschijnlijk omdat de toestemming niet verkregen kon worden binnen de 60 seconden;");
		
		Logger.getLogger("Minecraft").info("[KKP] Disabling " + this.serviceName + "...");
		this.serviceStateHandler.running = false;
		try{
			this._disableCore();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + " kon niet worden opgestart");
			try{
				this.serviceStateHandler.stopSystemError(e);
			}catch(Throwable ex){
				Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + " kon niet de nodige maatregelen nemen nu de PlayerManager niet kon opstarten");
			}
		}
		try{
			this.changingState.unlock();
		}catch(Exception e){}
		Logger.getLogger("Minecraft").info("[KKP] Disabled " + this.serviceName);
	}*/
	
	protected abstract void _enableCore() throws Exception;
	protected abstract void _disableCore() throws Exception;
	
	@Override
	public String toString(){
		if(this.running){
			return "De service " + this.serviceName + " is actief.";
		}else{
			Date a = new Date(this.stopTime);
			return "De service " + this.serviceName + " is gestopt. Reden = \"" + (this.stopException == null?"Normaal gestopt, waarschijnlijk door een gebruiker":this.stopException) + "\", Tijd van het uitschakelen (of crash als beschikbaar): " + a.toString() + " (" + this.stopTime + ")"; 
		}
	}

	protected Exception getLastStopException(){
		return this.stopException;
	}


	protected void printStopCrash(){
		if(this.stopException == null){
			Logger.getLogger("Minecraft").info("[KKP] De laatste keer dat de service stopte, was dat niet vanwege een fout.");
		}else{
			Logger.getLogger("Minecraft").info("[KKP] De laatste keer dat de service stopte, was vanwege deze fout:");
			this.stopException.printStackTrace();
		}
	}
	
	protected void printStopCrash(CommandSender a, int elementsAmount, boolean latestLast){
		if(this.stopException == null){
			a.sendMessage("De laatste keer dat de service " + this.serviceName + " stopte, was dat niet vanwege een fout.");
		}else{
			a.sendMessage("De laatste keer dat de service " + this.serviceName + " stopte, was vanwege deze fout:");
			StackTraceElement[] elements = this.stopException.getStackTrace();
			if(elementsAmount > elements.length)elementsAmount = elements.length;
			
			if(latestLast){
				for(int i = elements.length - elementsAmount - 1; i >= 0; i++){
					a.sendMessage(elements[i].toString());
				}
			}else{
				for(int i = 0; i < elementsAmount; i++){
					a.sendMessage(elements[i].toString());
				}
			}
		}
	}
	
	public boolean isUsable(){
		if(this._cleanState)return true;
		this.running = false;
		return false;
	}
}
