package KartoffelKanaalPlugin.plugin.kartoffelsystems;

public final class SessionSystem {
	private final ISessionSystemListener listener;
	private Thread[] accessors;
	//private boolean acceptNewAccessors = true;
	
	public SessionSystem(ISessionSystemListener listener, int capacity){
		if(capacity <  0)capacity = 10;
		if(capacity > 50)capacity = 50;
		this.accessors = new Thread[capacity];
		this.listener = listener;
	}
	
	public boolean isReleased(){
		if(accessors == null)return true;
		for(int i = 0; i < accessors.length; i++){
			if(accessors[i] != null && accessors[i].isAlive())return false;
		}
		return true;
	}
	
	public boolean setDenyNew(){
		//this.acceptNewAccessors = false;
		return true;
	}
	
	public boolean acquireAccess(){
		//if(!this.acceptNewAccessors)return false;
		Thread t = Thread.currentThread();
		if(this.accessors == null)this.accessors = new Thread[10];
		for(int i = 0; i < this.accessors.length; i++){
			if(this.accessors[i] == t){
				//System.out.println("SessionSystem: Thread \"" + t.toString() + "\" heeft al toegang op index " + i);
				return true;
			}
		}
		for(int i = 0; i < this.accessors.length; i++){
			if(this.accessors[i] == null || !this.accessors[i].isAlive()){
				this.accessors[i] = t;
				if(this.listener != null){
					try{
						this.listener.onAccessReceived(t);
					}catch(Exception e){
						//System.out.println("SessionSystem: Kon onAccessReceived-Event niet uitvoeren:");
						e.printStackTrace();
					}
				}
				//System.out.println("SessionSystem: Thread \"" + t.toString() + "\" heeft toegang gekregen. Index in accessors = " + i);
				return true;
			}
		}
		//System.out.println("SessionSystem: Accessorlist vergroten van " + this.accessors.length + " naar " + this.accessors.length + 5 + "...");
		Thread[] newAccessorsList = new Thread[this.accessors.length + 5];
		System.arraycopy(this.accessors, 0, newAccessorsList, 0, this.accessors.length);
		newAccessorsList[this.accessors.length] = t;
		this.accessors = newAccessorsList;
		if(this.listener != null){
			try{
				this.listener.onAccessReceived(t);
			}catch(Exception e){
				//System.out.println("SessionSystem: Kon onAccessReceived-Event niet uitvoeren:");
				e.printStackTrace();
			}
		}
		//System.out.println("SessionSystem: Thread \"" + t.toString() + "\" heeft toegang gekregen in een nieuw gegenereede array");
		return true;
	}
	
	public void releaseAccess(){
		if(this.accessors == null)return;
		//System.out.println("SessionSystem: Access releasing of Thread \"" + Thread.currentThread().toString() + "\"...");
		boolean active = false;
		for(int i = 0; i < this.accessors.length; i++){
			if(this.accessors[i] == Thread.currentThread()){
				//System.out.println("SessionSystem: Access released of Thread \"" + Thread.currentThread().toString() + "\" op index " + i);
				this.accessors[i] = null;
			}else if(this.accessors[i] != null && this.accessors[i].isAlive()){
				active = true;
			}
		}
		if(!active && this.listener != null){
			try{
				this.listener.onAccessReleased();
				//System.out.println("SessionSystem: onAccessReleased-Event uitgevoerd");
			}catch(Exception e){
				//System.out.println("SessionSystem: Kon onAccessReleased-Event niet uitvoeren:");
				e.printStackTrace();
			}
		}
		this.cleanUpEmpty();
	}
	
	public void cleanUpEmpty(){
		if(this.accessors == null)return;
		int latestActiveIndex = 0;
		boolean prevPlace;
		boolean changed = true;
		while(changed){
			changed = false;
			prevPlace = false;
			latestActiveIndex = (this.accessors.length < 4)?this.accessors.length - 1:4;
			for(int i = 0; i < this.accessors.length; i++){
				if(this.accessors[i] != null && this.accessors[i].isAlive()){
					if(i > latestActiveIndex)latestActiveIndex = i;
					if(prevPlace)changed = true;
				}else if(i < this.accessors.length - 1){
					if(this.accessors[i + 1] != null && this.accessors[i + 1].isAlive()){
						this.accessors[i] = this.accessors[i + 1];
					}else{
						prevPlace = true;
					}
				}
			}
		}
		if(this.accessors.length - (latestActiveIndex + 1) >= 10){
			Thread[] newAccessorsList = new Thread[latestActiveIndex + 1 + 5];
			//System.out.println("SessionSystem: cleanUpEmpty, verkleining van accessorsList: van " + this.accessors.length + " naar " + newAccessorsList.length);
			System.arraycopy(this.accessors, 0, newAccessorsList, 0, latestActiveIndex + 1);
			this.accessors = newAccessorsList;
		}else{
			//System.out.println("SessionSystem: cleanUpEmpty, geen verkleining van accessorsList");
		}
	}
	
	public boolean hasThreadAccess(Thread t){
		if(this.accessors == null)return false;
		for(int i = 0; i < this.accessors.length; i++){
			if(this.accessors[i] == t)return true;
		}
		return false;
	}
}
