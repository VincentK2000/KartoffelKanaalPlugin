package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import KartoffelKanaalPlugin.plugin.Main;


public class SaveQueue implements Runnable{
	private PersonSaveFormat[] list = new PersonSaveFormat[1];
	private Thread t;
	private PlayerManager parent;
	private boolean markNextAsBackup;
	private Lock lock = new ReentrantLock();
	private boolean stopping = false;
	
	public SaveQueue(PlayerManager parent){
		this.parent = parent;
	}
	
	protected void saveAndStop(Person[] p){
		if(stopping)return;
		stopping = true;
		if(p == null)return;
		Logger.getLogger("Minecraft").info("[KKP] SaveQueue: Spelersbestand bewaren...");
		try {
			this.lock.tryLock(10, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {}
		PersonSaveFormat[] psf = new PersonSaveFormat[p.length];
		for(int i = 0; i < p.length; i++){
			try{
				if(p[i].isChanged())psf[i] = new PersonSaveFormat(p[i]);
			}catch(Exception e){
				Logger.getLogger("Minecraft").warning("[KKP] SaveQueue: Kon een Person niet converten naar een PersonSaveFormat: " + e);
				e.printStackTrace();
			}
		}
		
		/*System.out.println("psf.length = " + psf.length);
		for(int i = 0; i < psf.length; i++){
			System.out.println("psf[" + i + "] = " + (psf[i] == null?"null":psf[i]));
		}*/
		
		if(psf.length > 0){
			int newlistjump;
			PersonSaveFormat[] newlist;
			if(list.length == 1 && list[0] == null){
				newlist = new PersonSaveFormat[psf.length];
				newlistjump = 0;
			}else{
				newlist = new PersonSaveFormat[list.length + psf.length];
				newlistjump = list.length;
			}
			for(int i = 0; i < newlistjump; i++){
				newlist[i] = list[i];
			}
			for(int i = 0; i < psf.length; i++){
				newlist[newlistjump++] = psf[i];//de increment van newlistjump moet pas na de opvraging van de value gebeuren
			}
			this.list = newlist;
			
			//System.out.println("De nieuwe lijst bevat " + newlist.length + " elementen");
		}
		
		try{
		this.lock.unlock();
		}catch(Exception e){}
		
		if(t != null && t.isAlive()){
			try {
				t.join();
			} catch (InterruptedException e) {
				Logger.getLogger("Minecraft").warning("[KKP] Bij het stoppen van de SaveQueue is het wachten op de saveThread verbroken door een InterruptedException: " + e.getMessage() + " (" + e + ")");
			}
		}else{
			t = Thread.currentThread();
			this.SaveList();
		}
		
		Logger.getLogger("Minecraft").info("[KKP] SaveQueue: Spelerbestand bewaard");
	}
	
	protected void add(Person[] p, boolean makeBackup){
		if(stopping)return;
		if(p == null)p = new Person[0];
		PersonSaveFormat[] psf = new PersonSaveFormat[p.length];
		for(int i = 0; i < p.length; i++){
			try{
				if(p[i].isChanged())psf[i] = new PersonSaveFormat(p[i]);
			}catch(Exception e){}
		}
		try {
			lock.tryLock(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
		
		if(psf.length > 0 && psf[0] != null){
			int newlistjump;
			PersonSaveFormat[] newlist;
			if(list.length == 1 && list[0] == null){
				newlist = new PersonSaveFormat[psf.length];
				newlistjump = 0;
			}else{
				newlist = new PersonSaveFormat[list.length + psf.length];
				newlistjump = list.length;
			}
			for(int i = 0; i < newlistjump; i++){
				newlist[i] = list[i];
			}
			for(int i = 0; i < psf.length; i++){
				newlist[newlistjump++] = psf[i];//de increment van newlistjump moet pas na de opvraging van de value gebeuren
			}
		}
		lock.unlock();
		if(makeBackup)this.markNextAsBackup = true;
		this.notifyNew();
	}

	protected void add(Person p){
		if(stopping)return;
		if(!p.isChanged())return;
		PersonSaveFormat psf;
		try{
			psf = new PersonSaveFormat(p);
		}catch(Exception e){return;}
		this.add(psf);
	}
	protected void add(PersonSaveFormat psf){
		if(stopping)return;
		if(PersonSaveFormat.isCorrect(psf)){
			lock.lock();
			if(list.length == 1 && list[0] == null){
				list[0] = psf;
			}else{
				PersonSaveFormat[] newlist = new PersonSaveFormat[list.length + 1];
				for(int i = 0; i < newlist.length; ++i){
					newlist[i] = list[i];
				}
				list = newlist;
			}
			lock.unlock();
			this.notifyNew();
		}else{
			if(psf != null)Logger.getLogger("Minecraft").warning("[KKP] Speler is niet juist doorgegeven aan SaveQue, mogelijk zijn er missende spelers");
		}
	}
	
	protected void notifyNew(){
		if(t != null && t.isAlive())return;
		t = new Thread(this);
		t.run();
	}
	/*@Override
	public void run() {
		if(list == null)return;
		if(list.length == 0)list = new PersonSaveFormat[1];
		if((list.length == 1) && (list[0] == null))return;
		UUID id;
		/*if(oldSB == null || oldSB.markedForDelete()){
			File fallbackfile = null;
			try {
				fallbackfile = File.createTempFile("spelerbestand" + (new Random()).nextInt(1000000), null);
				Files.copy(main.toPath(), fallbackfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Kan fallback niet maken");
			}
			this.oldSB = new Spelerbestand(fallbackfile, Main.plugin.pm);
		}*//*
		//Main.plugin.pm.setStableResource(this.oldSB);
		ThreadChecker tc;
		try{
			tc = new ThreadChecker(this, list[0]);
		}catch(Exception ex){
			if(list.length == 1){
				list[0] = null;
			}else{
				PersonSaveFormat[] newlist = new PersonSaveFormat[list.length - 1];
				for(int i = 0; i < newlist.length; ++i){
					newlist[i] = list[i + 1];
				}
				list = newlist;
				this.next(null);
			}
			return;
		}
		list[0].Configure(this.parent, tc);
		
		PersonSaveFormat[] newlist = new PersonSaveFormat[list.length - 1];
		for(int i = 0; i < newlist.length; ++i){
			newlist[i] = list[i + 1];
		}
		list = newlist;
		//list.remove(0);//zou normaal al moeten worden bewaard bij de ThreadChecker
		tc.run();
	}
	/**
	 * test test
	 * @param tc De ThreadChecker die klaar is
	 *//*
	protected void next(ThreadChecker tc){//Threads stapelen elkaar op, dus om de 4 moet die opnieuw beginnen
		//if(currentTc != null && currentTc.isAlive())currentTc.interrupt(); //mag niet zichzelf stoppen
		Thread a = new Thread(this);
		a.run();
		t = a;
	}*/
	@Override
	public void run(){
		this.SaveList();
	}
	
	public void SaveList(){
		if(this.parent == null)this.parent = Main.pm;
		//System.out.println(((this.list.length == 1 && this.list[0] == null)?0:this.list.length) + " persoon/personen bewaren");
		while(this.list.length > 0){
			if(PersonSaveFormat.isCorrect(this.list[0])){
				Exception ex = this.SaveItem();
				if(ex != null){
					Logger.getLogger("Minecraft").warning("[KKP] Bewaren van een persoon in het Spelerbestand is mislukt: " + ex.getMessage() + '(' + ex + ')');
				}
				if(stopping)this.parent.unloadPlayer(this.list[0].original);
			}else{
				if(this.list[0] != null && this.list[0].dest != -100)Logger.getLogger("Minecraft").warning("[KKP] Bij het bewaren van een PSF, is er een incorrecte PSF opgemerkt");
			}
			lock.lock();
			if(list.length == 1){
				list[0] = null;
				lock.unlock();
				break;
			}else{
				PersonSaveFormat[] newList = new PersonSaveFormat[list.length - 1];
				for(int i = 0; i < newList.length; i++){
					newList[i] = list[i + 1];
				}
				//System.arraycopy(this.list, 1, newList, 0, newList.length);
				this.list = newList;
			}
			lock.unlock();
		}
		if(this.markNextAsBackup && this.parent != null && this.parent.res != null && this.parent.res.getResource() != null){
			this.parent.res.getResource().markForBackup();
			this.markNextAsBackup = false;
		}
	}
	
	private Exception SaveItem(){
		if(list == null || list[0] == null)return null;
		//System.out.println("[KKP] PlayerManager: De persoon " + this.list[0].getOriginal().toString() + " wordt bewaard");
		this.list[0].Configure(this.parent, this);
		Thread t = new Thread(this.list[0]);
		t.start();
		try{
			for(int i = 0; i < 10; i++){
				Thread.sleep(500);
				if(!t.isAlive())return this.list[0].error;
			}
			Logger l = Logger.getLogger("Minecraft");
			l.warning("[KartoffelKanaalPlugin] Persoon " + this.list[0].specifier + " bewaren duurt al 5 seconden..." );
			for(int i = 0; i < 6; i++){
				Thread.sleep(500);
				if(!t.isAlive())return this.list[0].error;
			}
			l.warning("[KartoffelKanaalPlugin] Persoon " + this.list[0].specifier + " bewaren duurt al 8 seconden...");
			for(int i = 0; i < 2; i++){
				Thread.sleep(1000);
				if(!t.isAlive())return this.list[0].error;
			}
			l.warning("[KartoffelKanaalPlugin] Persoon " + this.list[0].specifier + " bewaren duurt al 10 seconden...");
			l.warning("[KartoffelKanaalPlugin] Persoon " + this.list[0].specifier + " bewaren zal automatisch gestopt worden over 5 seconden");
			for(int i = 0; i < 5; i++){
				Thread.sleep(1000);
				if(!t.isAlive())return this.list[0].error;
			}
			l.warning("[KartoffelKanaalPlugin] Kon de persoon " + this.list[0].specifier + " niet bewaren binnen de 15 seconden. Process wordt beëindigt");
			t.interrupt();
			return new Exception("Kon de persoon niet bewaren binnen de 15 seconden");
		}catch(InterruptedException ex){
			return new Exception("De controleur om de PSF te controleren in gestopt vanwege een InterruptedException: " + ex.getMessage() + '(' + ex + ')');
		}
	}

}
