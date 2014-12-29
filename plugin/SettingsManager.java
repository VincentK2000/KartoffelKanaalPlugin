package KartoffelKanaalPlugin.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag.AutoAntilag;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.PlayerManager;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem.Pulser;

public class SettingsManager implements Runnable {
	protected boolean operationMode;
	protected boolean changed = false;
	
	protected RenewableFile res;
	
	private boolean startAutoAntilag = false;
	private boolean startPlayerSystem = false;
	private boolean startPulserSystem = false;
	
	public short firstNotifChangeMask;
	public short secondNotifChangeMask;
	

	//Header (16 byte's)
	//2 byte's: [short] SettingsIndeling VersionNummer
	//2 byte's: [short] SettingsEdition Nummer
	//12 byte's: leeg
	
	//8 byte's: [long] from-time daily diamond
	
	//8 byte's: [long] ijktijd op server
	//8 byte's: [long] ijktijd volgens speler
	
	//4 byte's: [int] PulserInterval
	//4 byte's: [int] AutoAntilagInterval
	
	//2 byte's: Services die moeten starten
	//	1 bit: startAutoAntilag
	//	1 bit: startPlayerSystem
	//	1 bit: startPulserSystem
	//	13 bits: leeg
	
	private static final byte[] defaultSettings = new byte[]{//EXCLUSIEF HEADER!!!		
		0,0,1,74,-94,-54,-80,0,
		
		0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,
		
		0,0x09,0x27,(byte) 0xC0,
		0,0x36,(byte) 0xEE,(byte) 0x80,
		
		(byte) 0xFF, (byte) 0xFF
	};
	protected long dailydiamonddate = 0L;
	//private int PulserInterval = 600000;
	//private int AutoAntilagInterval = 3600000;
	
	private static final byte VersionA = 0x00;
	private static final byte VersionB = 0x00;
	
	private boolean saveOnExit = true;
	private Thread saveThread;
	
	public SettingsManager(String res, String folderPath){
		if(folderPath == null || res == null){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: De resource of het folderPath is null");
			this.operationMode = true;
			return;
		}
		try {
			this.res = new RenewableFile(folderPath, 3, "SettingsBestand", Main.plugin.keypaths[3], VersionA, VersionB);
		} catch (Exception e) {
			this.operationMode = true;
			Logger.getLogger("Minecraft").warning("[KKP] De SettingsBestanden manager (de RenewableFile) kon zich niet initializeren: ");
			e.printStackTrace();
			
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager is nu in operationMode");
			return;
		}//SecureBestand(new File(Main.plugin.getDataFolderPath() + res), false);
		this.loadSettings();
	}

	public void loadSettings(){
		if(this.operationMode || this.res == null)return;
		
		SecureBestand sb = this.res.getResource();
		if(sb == null){
			this.saveDefaultSettings();
			sb = this.res.getResource();
		}

		if(sb == null){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Omdat de resource null was en saveDefaultSettings() kennelijk ook niet echt veel hielp, is het inladen van de SettingsManager onderbroken (A)");
			return;
		}
		
		if(!sb.sessionSys.acquireAccess()){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager:  Omdat het SessionSystem toegang verbood, is het inladen van de de SettingsManager onderbroken");
			return;
		}
		File f = sb.getFile();
		FileInputStream fis = null;
		try{
			if(f == null || !f.exists() || !f.isFile()){
				this.saveDefaultSettings();
				sb.sessionSys.releaseAccess();
				
				sb = (this.res == null?null:this.res.getResource());
				if(sb == null){
					Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Omdat de resource null was en saveDefaultSettings() kennelijk ook niet echt veel hielp, is het inladen van de SettingsManager onderbroken (B)");
					return;
				}
				if(!sb.sessionSys.acquireAccess()){
					throw new Exception("Access was denied by the SessionSystem");
				}
				
				f = sb.getFile();
				if(!f.exists()){
					sb.sessionSys.releaseAccess();
					Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Omdat de resource niet bestaat (\"" + f.getAbsolutePath() + "\") was en saveDefaultSettings() kennelijk ook niet echt veel hielp, is het inladen van de SettingsManager onderbroken (C)");
					return;
				}
			}
			fis = new FileInputStream(f);
			byte[] header = new byte[16];
			fis.read(header);
			if(header[0] == VersionA && header[1] == VersionB){
				this.saveOnExit = true;
			}else{
				this.operationMode = true;
				this.saveOnExit = false;
				Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Bestandsversie is niet ondersteund");
				Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Hierdoor zullen de standaard-stellingen worden toegepast");
				sb.sessionSys.releaseAccess();
				try{
					fis.close();
				}catch(Exception e){}
				return;
			}
			
			{//----DailyDiaStartTime----\\
				byte[] dailydiabytes = new byte[8];
				fis.read(dailydiabytes);
				long dailydia = 
						( ((long)dailydiabytes[0] & 0xFF) << 56) |
						( ((long)dailydiabytes[1] & 0xFF) << 48) |
						( ((long)dailydiabytes[2] & 0xFF) << 40) |
						( ((long)dailydiabytes[3] & 0xFF) << 32) |
						( ((long)dailydiabytes[4] & 0xFF) << 24) |
						( ((long)dailydiabytes[5] & 0xFF) << 16) |
						( ((long)dailydiabytes[6] & 0xFF) <<  8) |
						( ((long)dailydiabytes[7] & 0xFF)      );
				
				
				this.dailydiamonddate = dailydia;
			}
			
			fis.skip(24);
			
			{//----ServiceStartStates----\\
				byte[] startServices = new byte[2];
				fis.read(startServices);
				this.startAutoAntilag = (startServices[0] & 0x80) == 0x80;
				this.startPlayerSystem = (startServices[0] & 0x40) == 0x40;
				this.startPulserSystem = (startServices[0] & 0x20) == 0x20;
			}
			
			{//----PulserNotifChangeMasks----\\
				byte[] mask = new byte[2];
				fis.read(mask);
				short m = (short) (((short)mask[0] & 0xFF) << 8 | ((short)mask[1] & 0xFF));
				this.firstNotifChangeMask = m;
				
				mask = new byte[2];
				fis.read(mask);
				m = (short) (((short)mask[0] & 0xFF) << 8 | ((short)mask[1] & 0xFF));
				this.secondNotifChangeMask = m;
			}
			
			
		}catch(Exception ex){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon SettingsBestand niet laden (" + ex + ")");
		}
		try{
			fis.close();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon de FileInputStream om het SettingsBestand in te laden niet sluiten (" + e + ")");
		}
		sb.sessionSys.releaseAccess();
		
		boolean prevChanged = this.changed;
		if(Main.aa != null)Main.aa.setTimeout(this.loadAutoAntilagIntervalFromFile());
		
		if(Main.pulser != null)Main.pulser.setTimeout(this.loadPulserIntervalFromFile());
		this.changed = prevChanged;
	}
	protected void saveDefaultSettings(){
		if(this.saveThread != null && this.saveThread.isAlive()){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kan DefaultSettings niet bewaren aangezien iets anders al aan het bewaren is");
			return;
		}
		this.saveThread = Thread.currentThread();
		Logger.getLogger("Minecraft").info("[KKP] SettingsManager: Standaard Settings bewaren...");
		if(this.res == null){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kan Standaard Settings niet bewaren omdat de KartoffelFile null is");
			return;
		}
		int fEdition = this.res.getNewFileVersion();
		File f = this.res.acquireWriteFile(fEdition, "Default");
		FileOutputStream fos = null;
		try{
			if(f.exists())f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon Bestand om DefaultSettings naartoe te schrijven niet aanmaken (of oude verwijderen): " + e);
			return;
		}
		try {
			fos = new FileOutputStream(f);
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] Kon OutputStream naar het Settings-bestand niet openen (" + e.getMessage() + " (" + e + "))");
			return;
		}

		try{
			//Header
			fos.write(SettingsManager.VersionA);
			fos.write(SettingsManager.VersionB);
			
			fos.write((fEdition >>> 8) & 0xFF);
			fos.write( fEdition        & 0xFF);
			
			fos.write(new byte[12]);
			
			
			//Data
			fos.write(SettingsManager.defaultSettings);
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon het Default Settingsbestand niet schrijven naar normale Settingsbestand: " + e.getMessage() + " (" + e + ")");
		}

		try {
			fos.close();
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] Kon de Outputstream niet sluiten bij bewaren van de Default Settings (" + e.getMessage() + " (" + e + ")), hierdoor kan mogelijke corruptie bij bestanden voorkomen");
		}
		this.saveThread = null;
		if(this.res != null && this.res.getResource() != null)this.res.getResource().markForBackup();
		this.res.writeFileFinished(fEdition);
		this.changed = false;
		Logger.getLogger("Minecraft").info("[KKP] SettingsManager: Standaard Settings bewaard");
	}
	
	public long getClientAddition(){
		if(this.res == null)return 0;
		long serverSync = 0;
		long clientSync = 0;
		
		SecureBestand sb = this.res.getResource();
		if(sb == null){
			this.saveDefaultSettings();
			sb = this.res.getResource();
		}

		if(sb == null)return 0;
		
		if(!sb.sessionSys.acquireAccess()){
			return 0;
		}
		File f = sb.getFile();
		FileInputStream fis = null;
		try{
			if(f == null || !f.exists() || !f.isFile()){
				this.saveDefaultSettings();
				sb.sessionSys.releaseAccess();
				
				sb = this.res.getResource();
				if(sb == null){
					return 0;
				}
				if(!sb.sessionSys.acquireAccess()){
					return 0;
				}
				
				fis = null;
				f = sb.getFile();
				if(!f.exists()){
					return 0;
				}
			}
			fis = new FileInputStream(f);
			byte[] header = new byte[16];
			fis.read(header);
			if(header[0] == VersionA && header[1] == VersionB){
				this.saveOnExit = true;
			}else{
				this.operationMode = true;
				this.saveOnExit = false;
				fis.close();
				return 0;
			}
			
			fis.skip(8);
			
			byte[] t = new byte[16];
			fis.read(t);
			
			serverSync = 0;
			serverSync |= (t[ 0] << 56);
			serverSync |= (t[ 1] << 48);
			serverSync |= (t[ 2] << 40);
			serverSync |= (t[ 3] << 32);
			serverSync |= (t[ 4] << 24);
			serverSync |= (t[ 5] << 16);
			serverSync |= (t[ 6] <<  8);
			serverSync |= (t[ 7]);

			clientSync = 0;
			clientSync |= (t[ 8] << 56);
			clientSync |= (t[ 9] << 48);
			clientSync |= (t[10] << 40);
			clientSync |= (t[11] << 32);
			clientSync |= (t[12] << 24);
			clientSync |= (t[13] << 16);
			clientSync |= (t[14] <<  8);
			clientSync |= (t[15]);
		
		}catch(Exception ex){}
		try{
			fis.close();
		}catch(Exception e){}
		sb.sessionSys.releaseAccess();
		
		return clientSync - serverSync;
	}
	
	public long convertToClientTime(long in){
		return in + this.getClientAddition();
	}
	public long convertToServerTime(long in){
		return in - this.getClientAddition();
	}
	
	public short getDailyDiaDay(){
		Calendar c = Calendar.getInstance();
		return (short)((c.getTimeInMillis() - dailydiamonddate) / 86400000);
	}
	
	public void setDailyDiaDay(short s){
		long addition = this.getClientAddition();
		long t = System.currentTimeMillis() + addition;
		
		long d = t / 86400000;
		d -= s;
		
		t = d * 86400000;
		
		this.dailydiamonddate = t - addition;
		this.changed = true;
	}
	
	public void Disable(){
		if(saveOnExit && this.changed){
			if(this.saveThread != null && this.saveThread.isAlive()){
				try {
					for(int i = 0; i < 30 && (this.saveThread != null && this.saveThread.isAlive()); i++){
						this.saveThread.join(500);
					}
					if(this.saveThread != null && this.saveThread.isAlive()){
						Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Kon de Thread die bezig was met bewaren niet stoppen. Er zal geprobeerd worden de \"bezige\" Thread te negeren en opnieuw te beginnen");
						Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Contacteer de Developer over dit probleem");
						this.saveThread.interrupt();
						this.saveThread = null;
						this.saveBlocking();
					}
				} catch (InterruptedException e) {}
			}else{
				//saveThread = Thread.currentThread();
				this.saveBlocking();;
			}
		}
	}
	
	protected void Save(){
		if(this.saveThread != null && this.saveThread.isAlive())return;
		this.saveThread = new Thread(this);
		this.saveThread.run();
	}

	@Override
	public void run() {
		this.saveBlocking();
	}
	
	protected void saveBlocking(){
		if(this.saveThread != null && this.saveThread.isAlive()){
			Logger.getLogger("Minecraft").warning("[KKP] Kon SettingsBestand niet bewaren omdat er al iets het aan het bewaren is");
			return;
		}
		this.saveThread = Thread.currentThread();
		if(this.res == null){
			Logger.getLogger("Minecraft").warning("[KKP] Kon het SettingsBestand niet bewaren omdat de KartoffelFile null was");
		}
		
		//Passieve Settings Inladen
		SecureBestand res = null;
		byte[] ijktijd = new byte[16];
		byte[] pulserInterval = new byte[]{0,0,-22,96};
		byte[] autoAntilagInterval = new byte[]{0,0x36,(byte) 0xEE,(byte) 0x80};
		try{
			res = this.res.getResource();
			if(res == null){
				throw new NullPointerException("De resource is null");
			}
			if(!res.sessionSys.acquireAccess()){
				throw new Exception("Kon geen toegang krijgen voor het sessionSystem van het leesbestand");
			}
			File old = res.getFile();
			if(old == null){
				this.saveDefaultSettings();
				res = this.res.getResource();
				if(!res.sessionSys.acquireAccess()){
					throw new Exception("Kon geen toegang krijgen voor het sessionSystem van het leesbestand");
				}
				old = res.getFile();
			}
			
			boolean loadPassive = old != null;
			
			if(loadPassive){
				FileInputStream fis = null;
			
				try{
					fis = new FileInputStream(old);
				}catch(Exception e){
					loadPassive = false;
					Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: FileInputStream openen naar Settings-bestand voor bewaren mislukt: " + e.getMessage() + "(" + e + ")");
				}
				
				if(loadPassive){
					try{
						if(fis.available() < 24){
							fis.close();
							throw new Exception("De SettingsFile was te kort om de passive settings in te laden");
						}
						fis.skip(24);
						if(fis.available() < 16){
							fis.close();
							throw new Exception("De SettingsFile was te kort om de passive settings in te laden");
						}
						fis.read(ijktijd);
						
						if(fis.available() < 4){
							fis.close();
							throw new Exception("De SettingsFile was te kort om de passive settings in te laden");
						}
						fis.read(pulserInterval);
						
						if(fis.available() < 4){
							fis.close();
							throw new Exception("De SettingsFile was te kort om de passive settings in te laden");
						}
						fis.read(autoAntilagInterval);
					}catch(Exception e){
						Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon de passieve settings van het oude Settings-bestand niet inladen voor bewaren vanwege een fout: " + e.getMessage() + "(" + e + ")");
					}
					try{
						fis.close();
					}catch(Exception e){
						Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon de FileInputStream niet sluiten bij het bewaren van de Settings (" + e.getMessage() + " (" + e + "))");
					}
				}
			}
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon oude Settings niet laden: " + e);
		}
		try{
			res.sessionSys.releaseAccess();
		}catch(Exception e){}
		//Passieve Settings Ingeladen
		
		//Settings Updaten
		if(Main.pulser != null && Main.pulser.getTimeout() >= 30000){
			int interval = Main.pulser.getTimeout();
			pulserInterval[0] = (byte) ((interval >>> 24) & 0xFF);
			pulserInterval[1] = (byte) ((interval >>> 16) & 0xFF);
			pulserInterval[2] = (byte) ((interval >>>  8) & 0xFF);
			pulserInterval[3] = (byte) ( interval         & 0xFF);
		}
		
		if(Main.aa != null && Main.aa.getTimeout() >= 30000){
			int interval = Main.aa.getTimeout();
			autoAntilagInterval[0] = (byte) ((interval >>> 24) & 0xFF);
			autoAntilagInterval[1] = (byte) ((interval >>> 16) & 0xFF);
			autoAntilagInterval[2] = (byte) ((interval >>>  8) & 0xFF);
			autoAntilagInterval[3] = (byte) ( interval         & 0xFF);
		}
		
		//Nieuwe Settings Schrijven
		FileOutputStream fos;
		int fEdition = this.res.getNewFileVersion();
		try{
			File f = this.res.acquireWriteFile(fEdition, "");
			f.createNewFile();
			f.mkdirs();
			fos = new FileOutputStream(f);
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: FileOutputStream openen naar Settings-bestand voor bewaren mislukt: " + e.getMessage() + "(" + e + ")");
			this.saveThread = null;
			return;
		}
		try {
			//Header
			fos.write(SettingsManager.VersionA);
			fos.write(SettingsManager.VersionB);
			
			fos.write((fEdition >>> 8) & 0xFF);
			fos.write( fEdition        & 0xFF);
			fos.write(new byte[12]);
			
			{//-----DailyDiaStartTime----\\
				byte[] startTime = new byte[]{
						(byte) ((this.dailydiamonddate >>> 56) & 0xFF),
						(byte) ((this.dailydiamonddate >>> 48) & 0xFF),
						(byte) ((this.dailydiamonddate >>> 40) & 0xFF),
						(byte) ((this.dailydiamonddate >>> 32) & 0xFF),
						(byte) ((this.dailydiamonddate >>> 24) & 0xFF),
						(byte) ((this.dailydiamonddate >>> 16) & 0xFF),
						(byte) ((this.dailydiamonddate >>>  8) & 0xFF),
						(byte) ( this.dailydiamonddate         & 0xFF)
				};
				fos.write(startTime);
			}
			//IJktijd
			fos.write(ijktijd);
			
			//PulserInterval
			fos.write(pulserInterval);
			
			//PulserInterval
			fos.write(autoAntilagInterval);
			
			{//----Services Staten----\\
				byte[] services = new byte[2];
				if(this.startAutoAntilag)services[0] |= 0x80;
				if(this.startPlayerSystem)services[0] |= 0x40;
				if(this.startPulserSystem)services[0] |= 0x20;
				fos.write(services);
			}
			
			{//----NotifChangeMask----\\
				byte[] mask = new byte[2];
				mask[0] = (byte) ((this.firstNotifChangeMask >>> 8) & 0xFF);
				mask[1] = (byte) ((this.firstNotifChangeMask      ) & 0xFF);
				fos.write(mask);
				
				mask[0] = (byte) ((this.secondNotifChangeMask >>> 8) & 0xFF);
				mask[1] = (byte) ((this.secondNotifChangeMask      ) & 0xFF);
				fos.write(mask);
			}
			
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon de Settings niet naar bestand schrijven");
		}
		
		
		try{
			fos.close();
		}catch(Exception e){}
		this.saveThread = null;
		this.changed = false;
		this.res.writeFileFinished(fEdition);
	}
	
	public int loadPulserIntervalFromFile(){
		if(this.res == null)return 600000;
		SecureBestand res = this.res.getResource();
		if(res == null)return 600000;
		if(!res.sessionSys.acquireAccess()){
			return 600000;
		}
		File f = res.getFile();
		if(f == null){
			res.sessionSys.releaseAccess();
			this.saveDefaultSettings();
			res = this.res.getResource();
			if(res == null)return 600000;
			if(!res.sessionSys.acquireAccess()){
				return 600000;
			}
			f = res.getFile();
		}
		
		if(f == null || !f.exists()){
			res.sessionSys.releaseAccess();
			return 600000;
		}
		
		FileInputStream fis;
		try{
			fis = new FileInputStream(f);
		}catch(Exception e){
			res.sessionSys.releaseAccess();
			return 600000;
		}
		int ans;
		try{
			fis.skip(40);
			ans = (fis.read() & 0xFF) << 24 | (fis.read() & 0xFF) << 16 | (fis.read() & 0xFF) << 8 | (fis.read() & 0xFF);
		}catch(Exception e){
			ans = 600000;
		}
		try{
			fis.close();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon de FileInputStream niet sluiten bij het bewaren van de Settings (" + e.getMessage() + " (" + e + "))");
		}
		res.sessionSys.releaseAccess();
		return ans;
	}
	
	public void loadStartServices(){
		Logger l = Logger.getLogger("Minecraft");
		
		if(this.startPulserSystem){
			//----PULSER----\\
			SettingsManager.EnablePulserSystem();
			//----PULSER----\\
		}else{
			l.info("[KKP] De Pulser wordt niet opgestart omdat dat zo is aangegeven");
		}
		
		l.info("");
	
		if(this.startPlayerSystem){
			//----PLAYER MANAGER----\\
			SettingsManager.EnablePlayerSystem();
			//----PLAYER MANAGER----\\
		}else{
			l.info("[KKP] De PlayerManager wordt niet opgestart omdat dat zo is aangegeven");
		}
			
		l.info("");
	
		if(this.startAutoAntilag){
			//----AUTOANTILAG----\\
			SettingsManager.EnableAutoAntilag();
			//----AUTOANTILAG----\\
		}else{
			l.info("[KKP] De AutoAntilag wordt niet opgestart omdat dat zo is aangegeven");
		}
	}
	
	public int loadAutoAntilagIntervalFromFile(){
		SecureBestand res = this.res.getResource();
		if(res == null)return 600000;
		if(!res.sessionSys.acquireAccess()){
			return 600000;
		}
		File f = res.getFile();
		if(f == null){
			res.sessionSys.releaseAccess();
			this.saveDefaultSettings();
			res = this.res.getResource();
			if(res == null)return 600000;
			if(!res.sessionSys.acquireAccess()){
				return 600000;
			}
			f = res.getFile();
		}
		
		if(f == null || !f.exists()){
			res.sessionSys.releaseAccess();
			return 600000;
		}
		
		FileInputStream fis;
		try{
			fis = new FileInputStream(f);
		}catch(Exception e){
			res.sessionSys.releaseAccess();
			return 600000;
		}
		int ans;
		try{
			fis.skip(44);
			ans = (fis.read() & 0xFF) << 24 | (fis.read() & 0xFF) << 16 | (fis.read() & 0xFF) << 8 | (fis.read() & 0xFF);
		}catch(Exception e){
			ans = 600000;
		}
		try{
			fis.close();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] SettingsManager: Kon de FileInputStream niet sluiten bij het bewaren van de Settings (" + e.getMessage() + " (" + e + "))");
		}
		res.sessionSys.releaseAccess();
		return ans;
	}
	
	/*public File acquireWriteFile(short version){
		String versionPart = String.valueOf(version);
		while(versionPart.length() < 5){
			versionPart = "0" + versionPart;
		}
		this.write = this.mainfolder + "SettingsBestand" + versionPart + ".bin";
		return new File(Main.plugin.getDataFolderPath() + this.write);
	}
	protected void writeFileFinished(){
		if(this.write == null)return;
		File newFile = new File(Main.plugin.getDataFolderPath() + this.write);
		FileInputStream fis = null;
		/*try {
			checkFileExists(newFile);
		}catch(Exception ex){
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Playermanager: Kon niet controleren of Spelerbestand bestaat of nieuwe maken");
		}*//*
		try{
			SecureBestand old = this.resource;
			fis = new FileInputStream(newFile);
	
			if(fis.read() == VersionA && fis.read() == VersionB){
				if(old != null && !old.fileEquals(newFile))old.markForDelete();
			}else{
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] SettingsManager: Bestandsversie is niet ondersteunt");
			}
			this.edition = (short) (((fis.read() & 0xFF) << 8) | (fis.read() & 0xFF));
			
			SecureBestand s = new SecureBestand(newFile, false);
			this.resource = s;
			
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] SettingsManager was niet in staat een nieuw bestand te controleren of te markeren als het hoofdbestand: " + e);
		}
		try{
			Main.plugin.keypaths[3] = this.write;
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Kon het resourcepath in het linkingfile niet veranderen");
		}
		try{
			fis.close();
		}catch(Exception e){}
		
	}*/
	
	public static String getTimeRelation(long l){
		StringBuilder sb = new StringBuilder();
		
		long timeDifference = l - System.currentTimeMillis();
		boolean isTimePast = timeDifference < 0;
		if(isTimePast)timeDifference = -timeDifference;
		long t = timeDifference;
		
		if(!isTimePast)sb.append("over ");
		
		sb.append(t / 604800000);//1000 * 60 * 60 * 24 * 7
		sb.append(" weken, ");
		t %= 604800000;
		
		sb.append(t / 86400000 );//1000 * 60 * 60 * 24
		sb.append(" dagen, ");
		t %= 86400000;
		
		sb.append(t / 3600000  );//1000 * 60 * 60
		sb.append(" uren, ");
		t %= 3600000;
		
		sb.append(t / 60000    );//1000 * 60
		sb.append(" minuten en ");
		t %= 60000;
		
		sb.append(t / 1000     );//1000
		sb.append(" seconden");
		t %= 1000;
		
		if(isTimePast)sb.append(" geleden");
		
		return sb.toString();
	}
	
	public static long getMillisValue(String type) throws Exception {
		type = type.toLowerCase();
		switch(type){
			case "week":
			case "weken":
			case "weeks":
				return 604800000;
			case "dag":
			case "dagen":
			case "day":
			case "days":
				return 86400000;
			case "uur":
			case "uren":
			case "hour":
			case "hours":
				return 3600000;
			case "minuut":
			case "minuten":
			case "minute":
			case "minutes":
				return 60000;
			case "seconde":
			case "secondes":
			case "second":
			case "seconds":
				return 1000;		
		}
		throw new Exception("Onbekende tijdsaanduiding: \"" + type + "\"");
	}
	public long getServerTimeFromArgs(String modus, CommandSender a, String[] args, boolean useRawTime, boolean leanToEnd) throws Exception{
		modus = modus.toLowerCase();
		if(modus.equals("over")){
			if(args.length > 2){
				if((args.length - 2) % 2 == 1){
					a.sendMessage("§4Oneven aantal parameters. Er kan dus niet voor alles een type zijn gespecifiëerd");
					return -1L;
				}
				long newValue = System.currentTimeMillis();
				for(int i = 2; i < args.length - 1; i+=2){
					args[i] = args[i].toLowerCase();
					long multip = SettingsManager.getMillisValue(args[i + 1]);
					int value;
					try{
						value = Integer.parseInt(args[i]);
					}catch(Exception e){
						a.sendMessage("§4Oncorrecte waarde voor \"" + args[i + 1] + "\": " + args[i]);
						return -1;
					}
					newValue += multip * value;
				}
				return newValue;
			}else{
				a.sendMessage("§cover <tijdsaanduidingen ...>§e bv.: §cover 3 dagen 9 uren 2 weken 44 minuten");
				return -1L;
			}
		}else if(modus.equals("absoluut")){
			String datumAanduiding = null;
			String tijdsAanduiding = null;
			
			if(args.length == 3){
				if(args[2].contains("-") || args[2].contains("/")){
					datumAanduiding = args[2];
				}else if(args[2].contains(":")){
					tijdsAanduiding = args[2];							
				}else{
					a.sendMessage("§4Oncorrecte datum- of tijdsaanduiding. Gebruik een \"-\" of een \"/\" om duidelijk te maken dat het gaat om een datum en een \":\" voor tijden");
					return -1L;
				}
			}else if(args.length == 4){
				if(args[2].contains("-") || args[2].contains("/")){
					datumAanduiding = args[2];
					tijdsAanduiding = args[3];
				}else if(args[2].contains(":")){
					tijdsAanduiding = args[2];
					datumAanduiding = args[3];
				}else{
					a.sendMessage("§4Oncorrecte datum- of tijdsaanduiding. Gebruik een \"-\" of een \"/\" om duidelijk te maken dat het gaat om een datum en een \":\" voor tijden");
					return -1L;
				}
			}else{
				a.sendMessage("§cabsoluut DD/MM/JJJJ UU:MM:SS§e (een van de 2 kan weggelaten worden en jaar en seconden zijn niet nodig)");
				return -1L;
			}
			
			long newValue = 0;
			if(datumAanduiding == null || datumAanduiding.length() == 0){
				Calendar c = Calendar.getInstance();
				if(!useRawTime)c.setTimeInMillis(Main.sm.convertToClientTime(System.currentTimeMillis()));
				c.set(Calendar.MILLISECOND, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.HOUR_OF_DAY, 0);
				newValue += c.getTimeInMillis();
			}else{
				datumAanduiding = datumAanduiding.replace('-', '/');
				String[] parts = datumAanduiding.split("/");
				if(parts.length < 2 || parts.length > 3){
					a.sendMessage("§4De datum moet DD/MM of DD/MM/JJJJ zijn.");
					return -1L;
				}
				int day;
				try{
					day = Integer.parseInt(parts[0]);
				}catch(NumberFormatException e){
					a.sendMessage("§4De dag moet een nummer zijn. Input = \"" + parts[0] + "\"");
					return -1L;
				}
				
				int month;
				try{
					month = Integer.parseInt(parts[1]);
				}catch(NumberFormatException e){
					a.sendMessage("§4De maand moet een nummer zijn. Input = \"" + parts[0] + "\"");
					return -1L;
				}
				
				int year = Calendar.getInstance().get(Calendar.YEAR);
				if(parts.length == 3){
					try{
						year = Integer.parseInt(parts[2]);
					}catch(NumberFormatException e){
						a.sendMessage("§4Het jaar moet een nummer zijn. Input = \"" + parts[0] + "\"");
						return -1L;
					}
				}
				
				if(day < 1 || day > 31){
					a.sendMessage("§4De dag moet minimum 1 en maximum 31 zijn.");
					return -1L;
				}
				if(month < 1 || month > 12){
					a.sendMessage("§4De maand moet minimum 1 en maximum 12 zijn.");
					return -1L;
				}
				if(year < 2014 || year > 2040){
					a.sendMessage("§4Het jaar moet minimum 2014 en maximum 2040 zijn.");
					return -1L;
				}						
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(0);
				c.set(year, month - 1/*Maanden lopen van 0 tot 11*/, day);
				newValue += c.getTimeInMillis();
			}
			
			if(tijdsAanduiding == null || tijdsAanduiding.length() == 0){
				newValue += leanToEnd?86399999:0;
			}else{
				String[] parts = tijdsAanduiding.split(":");
				if(parts.length < 2 || parts.length > 3){
					a.sendMessage("§4De datum moet UU/MM of UU/MM/SS zijn.");
					return -1L;
				}
				int hour;
				try{
					hour = Integer.parseInt(parts[0]);
				}catch(NumberFormatException e){
					a.sendMessage("§4Het uur moet een nummer zijn. Input = \"" + parts[0] + "\"");
					return -1L;
				}
				
				int minute;
				try{
					minute = Integer.parseInt(parts[1]);
				}catch(NumberFormatException e){
					a.sendMessage("§4De minuut moet een nummer zijn. Input = \"" + parts[0] + "\"");
					return -1L;
				}
				
				int second = leanToEnd?59:0;
				if(parts.length == 3){
					try{
						second = Integer.parseInt(parts[2]);
					}catch(NumberFormatException e){
						a.sendMessage("§4De seconde moet een nummer zijn. Input = \"" + parts[0] + "\"");
						return -1L;
					}
				}
				
				if(hour < 0 || hour > 23){
					a.sendMessage("§4Het uur moet minimum 0 en maximum 23 zijn.");
					return -1L;
				}
				if(minute < 0 || minute > 59){
					a.sendMessage("§4De minuut moet minimum 0 en maximum 59 zijn.");
					return -1L;
				}
				if(second < 0 || second > 59){
					a.sendMessage("§4De seconde moet minimum 0 en maximum 59 zijn.");
					return -1L;
				}
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(0);
				c.set(Calendar.HOUR_OF_DAY, hour);
				c.set(Calendar.MINUTE, minute);
				c.set(Calendar.SECOND, second);
				newValue += c.getTimeInMillis();
			}
			return useRawTime?newValue:this.convertToServerTime(newValue);
		}else{
			a.sendMessage("§4Onbekende modus. Mogelijke modussen: \"absoluut\" en \"over\"");
			return -1L;
		}
	}
	
	public static void EnableAutoAntilag(){
		if(Main.aa == null || !Main.aa.isUsable())Main.aa = new AutoAntilag();
		Main.aa.initialize(Main.sm == null?60000:Main.sm.loadAutoAntilagIntervalFromFile());
		Main.aa._Enable();
		if(Main.sm != null)Main.sm.startAutoAntilag = true;
	}
	
	public static void DisableAutoAntilag(){
		if(Main.aa == null)return;
		Main.aa._Disable();
		if(Main.sm != null)Main.sm.startAutoAntilag = false;
	}
	
	public static void EnablePlayerSystem(){
		if(Main.pm == null || !Main.pm.isUsable())Main.pm = new PlayerManager();
		Main.pm.initialize(Main.plugin.keypaths[0], Main.plugin.keypaths[1]);
		Main.pm._Enable();
		if(Main.sm != null)Main.sm.startPlayerSystem = true;
	}
	
	public static void DisablePlayerSystem(){
		if(Main.pm == null)return;
		Main.pm._Disable();
		if(Main.sm != null)Main.sm.startPlayerSystem = false;
	}
	
	public static void EnablePulserSystem(){
		if(Main.pulser == null || !Main.pulser.isUsable())Main.pulser = new Pulser();
		Main.pulser.initialize((Main.sm == null?600000:Main.sm.loadPulserIntervalFromFile()));
		Main.pulser._Enable();
		if(Main.sm != null)Main.sm.startPulserSystem = true;
	}
	
	public static void DisablePulserSystem(){
		if(Main.pulser == null)return;
		Main.pulser._Disable();
		if(Main.sm != null)Main.sm.startPulserSystem = false;
	}
	
	
	public boolean getStartAutoAntilag(){
		return this.startAutoAntilag;
	}
	
	public boolean getStartPlayerSystem(){
		return this.startPlayerSystem;
	}
	
	public boolean getStartPulserSystem(){
		return this.startPulserSystem;
	}
	
	public void setStartAutoAntilag(boolean v){
		this.startAutoAntilag = v;
		this.changed = true;
	}
	
	public void setStartPlayerSystem(boolean v){
		this.startPlayerSystem = v;
		this.changed = true;
	}
	
	public void setStartPulserSystem(boolean v){
		this.startPulserSystem = v;
		this.changed = true;
	}
	
	public void notifyChange(){
		this.changed = true;
	}
}
