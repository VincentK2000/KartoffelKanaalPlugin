package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import KartoffelKanaalPlugin.plugin.SecureBestand;


public final class PersonSaveFormat implements Runnable {
	public final short dest;
	public final byte[] data;
	//private ThreadChecker tc;
	private PlayerManager filedirector;
	private SaveQueue parent;
	protected byte triesLeft = 5;
	private long snapshotTime = 0;
	
	public String specifier = "???";
	protected Person original;
	protected Exception error;
	
	public PersonSaveFormat(Person p) throws Exception{
		if(p == null)throw new Exception("Person to convert in PersonSaveFormat is null");
		this.dest = Short.valueOf(p.getKartoffelID());
		
		this.snapshotTime = System.currentTimeMillis();
		this.data = p.getSaveArray();
		
		this.original = p;
		this.specifier = "[Naam = " + String.valueOf((this.original == null)?"null":this.original.name) + " | UUID = " + ((this.original==null)?"null":(this.original.UniqueID==null)?"null":this.original.UniqueID.toString()) + ']';
	}
	
	public static final boolean isCorrect(PersonSaveFormat a){
		return (a != null && a.dest >= 0 && a.data != null && a.data.length == 64);
	}
	
	protected final void Configure(PlayerManager filedirector, /*ThreadChecker tc,*/ SaveQueue s){
		this.filedirector = filedirector;
		this.parent = s;
		//this.tc = tc;
	}
	
	@Override
	public void run(){
		this.Save();
	}
	protected final void Save(){
		if(filedirector == null){
			this.setStatus(false, new NullPointerException("Filedirector bij PSF niet gegeven"));
			return;
		}
		if(!PersonSaveFormat.isCorrect(this)){
			this.setStatus(false, new Exception("De gegevens in de PSF zijn incorrect"));
			return;
		}
		if(triesLeft < 5){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Er wordt geprobeerd de speler " + this.specifier + " te bewaren.");
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Attempts resterend (van vijf): " + triesLeft);
		}
		if(triesLeft == 0){
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] De triesLeft van " + this.specifier + " zijn 0 wat betekent dat de KartoffelKanaalPlugin er niet in geslaagd is deze persoon te bewaren");
			return;
		}
		if(triesLeft < 3){
			try{
				this._SaveRecoverable();
			}catch(Exception e){
				this.setStatus(false, e);
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Speler " + this.specifier + " bewaren in recover-bestand mislukt");
				return;
			}
		}else{
			try{
				this._SaveFull(/*triesLeft < 5*/true);
			}catch(Exception e){
				this.setStatus(false, e);
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Speler " + this.specifier + " normaal bewaren mislukt");
				return;
			}
		}
		this.setStatus(true, null);
	}
	private void _SaveFull(boolean statusmessage) throws Exception{
		if(statusmessage)Logger.getLogger("Minecraft").info("[KKP] Speler " + this.specifier + " normaal bewaren...");
		if(this.filedirector == null || this.filedirector.res == null){
			throw new Exception("Filedirector of resource is null");
		}
		
		SecureBestand readBestand = this.filedirector.res.getResource();
		if(readBestand == null){
			this.filedirector.loadSpelerBestand();
			
			readBestand = filedirector.res.getResource();
			if(readBestand == null)throw new Exception("Resource is null (A)");
		}
		
		readBestand.sessionSys.acquireAccess();
		File read = readBestand.getFile();
		
		if(read == null || !read.exists() || !read.isFile()){
			readBestand.sessionSys.releaseAccess();
			
			this.filedirector.loadSpelerBestand();
			
			readBestand = this.filedirector.res.getResource();
			if(readBestand == null){
				throw new Exception("Resource is null (B)");
			}
			readBestand.sessionSys.acquireAccess();
			read = readBestand.getFile();
			if(read == null || !read.exists() || !read.isFile()){
				readBestand.sessionSys.releaseAccess();
				throw new Exception("readFile is null (C)");
			}
		}
		
		int newEdition = this.filedirector.res.getNewFileVersion();
		File f = this.filedirector.res.acquireWriteFile(newEdition, "");
		while(f.exists()){
			newEdition = this.filedirector.res.getNewFileVersion();
			f = this.filedirector.res.acquireWriteFile(newEdition, "");
		}
		f.getParentFile().mkdirs();
		f.createNewFile();

		FileOutputStream os = new FileOutputStream(f);
		FileInputStream is = new FileInputStream(read);
		/*{
			byte[] header = new byte[16];
			is.read(header);
			os.write(header);
		}*/
		is.skip(16);
		byte[] header = new byte[16];
		header[0] = PlayerManager.VersionA;
		header[1] = PlayerManager.VersionB;
		
		header[2] = (byte) ((newEdition >>> 24) & 0xFF);
		header[3] = (byte) ((newEdition >>> 16) & 0xFF);
		header[4] = (byte) ((newEdition >>>  8) & 0xFF);
		header[5] = (byte) ((newEdition       ) & 0xFF);
		
		os.write(header);
		
		int a = this.dest * 64;
		
		//Data Kopi§ren
		byte[] buffer = new byte[256];
		while(a > 255 && is.available() > 255){
			is.read(buffer);
			os.write(buffer);
			a -= 256;
		}
		
		buffer = new byte[64];
		while(a > 63 && is.available() > 63){
			is.read(buffer);
			os.write(buffer);
			a -= 64;
		}
		
		while(a > 0 && is.available() > 0){
			os.write(is.read());
			a--;
		}
		
		//Tijdelijk lege data invullen voor als die nog niet is geschreven (bij nieuwe toekenning kan het zijn dat een speler met een hoger destination nummer bewaard wordt voor een lagere omdat de lagere nog nooit bewaard is
		buffer = new byte[256];
		while(a > 255){
			os.write(buffer);
			a -= 256;
		}
		
		buffer = new byte[64];
		while(a > 63){
			os.write(buffer);
			a -= 64;
		}
		
		while(a > 0){
			os.write(0);
			a--;
		}
		
		//Nieuwe data aanbrengen
		is.skip(64);
		os.write(data);
		
		//Data kopi§ren
		buffer = new byte[256];
		while(is.available() > 255){
			is.read(buffer);
			os.write(buffer);
		}
		
		buffer = new byte[64];
		while(is.available() > 63){
			is.read(buffer);
			os.write(buffer);
		}
		while(is.available() > 0){
			os.write(is.read());
		}
		
		try{
			os.close();
		}catch(Exception e){}
		
		try{
			is.close();
		}catch(Exception e){}
		readBestand.sessionSys.releaseAccess();
		this.filedirector.res.writeFileFinished(newEdition);
		if(this.original != null){
			this.original.onSaveComplete(this.snapshotTime);
		}
		if(statusmessage)Logger.getLogger("Minecraft").info("[KKP] Speler " + this.specifier + " normaal bewaren geslaagd");
	}
	
	private final void _SaveRecoverable() throws Exception{
		Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Speler " + this.specifier + " bewaren in recover-file...");
		
		String write = filedirector.folderpath + "spelerbestandrecover.bin";
		File f = new File(write);
		if(!f.exists())f.createNewFile();
		FileOutputStream fos = new FileOutputStream(write, true);
		fos.write(this.data);
		fos.close();
		Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Speler " + this.specifier + " bewaren in recover-file geslaagd");
	}
	
	private final void setStatus(boolean success, Exception e){
		if(this.triesLeft == -100)return;
		if(success){
			this.error = null;
			this.triesLeft = -100;
		}else{
			this.error = e;
			--this.triesLeft;
			if(this.triesLeft > 0){
				this.parent.add(this);
			}else{
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Kon de speler " + this.specifier + " niet bewaren. Sorry =/");
			}
		}
	}
	
	protected Person getOriginal(){
		return this.original;
	}
	
	public Exception getError(){
		return ((triesLeft==-100)?null:this.error);
	}
	
	@Override
	public String toString(){
		return this.specifier;
	}
}
