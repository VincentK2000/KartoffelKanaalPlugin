package KartoffelKanaalPlugin.plugin;

public class ServiceStateHandler {
/*	public boolean running = false;
	protected Exception stopException = null;
	protected long time;
	
	String serviceName;
	
	public ServiceStateHandler(String serviceName){
		this.serviceName = serviceName;
	}
	
	public void stopNormal(){
		if(!running)return;
		this.running = false;
		this.stopException = null;
		Calendar c = Calendar.getInstance();
		this.time = c.getTimeInMillis();
	}

	public boolean stopSystemError(Exception e){
		if(!running && this.stopException != null){
			this.logWarningMessage("Het systeem verzond een nieuwe stopRequest, maar het systeem was al gestopt. Binnengekomen stopRequest: " + e);
			return false;
		}
		this.running = false;
		Calendar c = Calendar.getInstance();
		this.time = c.getTimeInMillis();
		this.stopException = e;
		this.logWarningMessage("De service is gestopt: " + e);
		return true;
	}
	
	protected Exception getLastStopException(){
		return this.stopException;
	}
	
	protected void printStopCrash(){
		if(this.stopException == null){
			this.logInfoMessage("De laatste keer dat de service stopte, was dat niet vanwege een fout.");
		}else{
			this.logInfoMessage("De laatste keer dat de service stopte, was vanwege deze fout:");
			this.stopException.printStackTrace();
		}
	}
	
	protected void printStopCrash(CommandSender a, int elementsAmount, boolean latestLast){
		if(this.stopException == null){
			this.logInfoMessage("De laatste keer dat de service stopte, was dat niet vanwege een fout.");
		}else{
			this.logInfoMessage("De laatste keer dat de service stopte, was vanwege deze fout:");
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
	
	@Override
	public String toString(){
		if(this.running){
			return "De service is actief.";
		}else{
			Date a = new Date(this.time);
			return "De service is gestopt. Reden = \"" + (this.stopException == null?"Normaal gestopt, waarschijnlijk door een gebruiker":this.stopException) + "\", Tijd van het uitschakelen (of crash als beschikbaar): " + a.toString() + " (" + this.time + ")"; 
		}
	}
	
	private void logWarningMessage(String msg){
		if(this.serviceName == null)this.serviceName = "Onbekend";
		Logger.getLogger("Minecraft").warning("[KKP] " + this.serviceName + ": " + msg);
	}
	
	private void logInfoMessage(String msg){
		if(this.serviceName == null)this.serviceName = "Onbekend";
		Logger.getLogger("Minecraft").info("[KKP] " + this.serviceName + ": " + msg);
	}*/
}
