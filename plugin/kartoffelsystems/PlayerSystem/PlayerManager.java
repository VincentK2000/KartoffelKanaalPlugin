package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import KartoffelKanaalPlugin.plugin.DataFieldShort;
import KartoffelKanaalPlugin.plugin.DebugTools;
import KartoffelKanaalPlugin.plugin.KartoffelFile;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.OverwritingFile;
import KartoffelKanaalPlugin.plugin.RenewableFile;
import KartoffelKanaalPlugin.plugin.SecureBestand;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.KartoffelService;

public class PlayerManager extends KartoffelService implements Listener{
	protected ArrayList<Person> loadedPlayers = new ArrayList<Person>();
	private ArrayList<Person> _online = new ArrayList<Person>();
	protected AutoSaver as = new AutoSaver(this);
	
	public String folderpath;//inclusief eind separatorChar
	
	protected static final byte VersionA = 0;
	protected static final byte VersionB = 0;
	protected SaveQueue saver = new SaveQueue(this);
	private short counter;
	protected Random keyRandomizer = new Random();
	
	protected RenewableFile res;
	
	protected StartupLoadQueue slq;
	
	protected DataFieldShort dailyDiaDays;
	
	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	public PlayerManager(){
		super("PlayerManager");
	}
	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	public void initialize(String folderpath, String filepath){
		if(this.initialized)return;
		
		if(this.running){
			Logger.getLogger("Minecraft").info("[KKP] Kan de PlayerManager niet initializen als die aan staat");
		}
		this.folderpath = folderpath;
		if(folderpath == null || filepath == null){
			this.DisableCrash(new NullPointerException("De folder- en/of filepath voor het initializen van de PlayerManager is null"));
			return;
		}
		//FileInputStream d = new FileInputStream();
		if(!folderpath.endsWith(File.separator))folderpath += File.separatorChar;
		this.folderpath = folderpath;
		if(this.res == null)
			try {
				this.res = new RenewableFile(Main.plugin.keypaths[0], 1, "spelersbestand", "DefaultSpelersBestandBody.bin", VersionA, VersionB);
			} catch (Exception e) {
				return;
			}
		this.res.setResourceFile((filepath == null?null:(new SecureBestand(new File(Main.plugin.getDataFolderPath() + filepath), false))),true);
		
		this.initialized = true;
	}
	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	protected void _enableCore() throws Exception{
		if(!this.initialized)throw new Exception("PlayerManager is niet initialized");
		if(this.as == null)this.as = new AutoSaver(this);
		this.as.checkConditions();
		Main.plugin.getServer().getPluginManager().registerEvents(Main.pm, Main.plugin);
		this._loadSpelerBestandPrivate();
		this._loadPlayersPrivate(Main.plugin.getServer().getOnlinePlayers());
		
		try {
			this.dailyDiaDays = new DataFieldShort(new OverwritingFile(this.folderpath, -1, this.folderpath + "dailydia.kkp", DataFieldShort.VersionA, DataFieldShort.VersionB), "De laatste dag sinds de DailyDiaStartTime dat de speler zijn DailyDia's heeft gekregen", Short.MIN_VALUE);
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] Kon de dailyDia-data van de PlayerManager niet initializen:");
			e.printStackTrace();
		}
	}

	@Override
	protected void _disableCore(){
		if(this.as != null){
			this.as.stop();
		}
		this.as = null;
		if(this.saver == null)this.saver = new SaveQueue(this);
		
		Person[] sp = null;
		if(this.loadedPlayers != null){
			sp = new Person[this.loadedPlayers.size()];
			sp = this.loadedPlayers.toArray(sp);
		}
		this.saver.saveAndStop(sp);
		
		try{
			this.dailyDiaDays.SaveBlocking();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon de DataFieldShort van de DailyDia niet bewaren:");
			e.printStackTrace();
		}
		this.dailyDiaDays = null;
		
		this.loadedPlayers = new ArrayList<Person>(0);
		this._online = new ArrayList<Person>(0);
		this.counter = 0;
		this.folderpath = "";
		this.res = null;
		this.saver = null;
		this.slq = null;
	}

	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	public void _createNewSpelersBestandPrivate(){
		Logger.getLogger("Minecraft").info("[KKP] PlayerManager: Nieuw SpelersBestand aanmaken...");
		try {
			KartoffelFile.saveDefaultFile(this.res, "DefaultSpelersBestandBody.bin");
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] PlayerManager: Kon geen nieuw Spelersbestand aanmaken:");
			e.printStackTrace();
			return;
		}
		Logger.getLogger("Minecraft").info("[KKP] PlayerManager: Nieuw SpelersBestand aangemaakt");
	}

	public void createNewSpelersBestand(){
		if(this.preventAction())return;
		this._createNewSpelersBestandPrivate();
	}

	private void _loadSpelerBestandPrivate(){		
		SecureBestand sb = this.res.getResource();
		if(sb == null){
			this._createNewSpelersBestandPrivate();
			sb = this.res.getResource();
		}
		
		if(sb == null){
			this.DisableCrash(new Exception("Omdat de resource null was en createNewSpelersBestand() kennelijk ook niet echt veel hielp, is het inladen van de PlayerManager onderbroken (A)"));
			return;
		}
		
		if(!sb.sessionSys.acquireAccess()){
			this.DisableCrash(new Exception("Het SessionSystem van het SecureBestand geeft geen toegang"));
			return;
		}
		File f = sb.getFile();
		FileInputStream fis = null;
		try{
			if(f == null || !f.exists() || !f.isFile()){
				this._createNewSpelersBestandPrivate();;
				sb.sessionSys.releaseAccess();
				
				sb = this.res.getResource();
				if(sb == null){
					this.DisableCrash(new Exception("Omdat de resource null was en createNewSpelersBestand() kennelijk ook niet echt veel hielp, is het inladen van de PlayerManager onderbroken (B)"));
					return;
				}
				
				if(!sb.sessionSys.acquireAccess()){
					this.DisableCrash(new Exception("Het SessionSystem van het SecureBestand geeft geen toegang"));
					return;
				}
				
				f = sb.getFile();
				if(!f.exists()){
					this.DisableCrash(new Exception("Omdat de resource niet bestaat (\"" + f.getAbsolutePath() + "\") was en createNewSpelersBestand() kennelijk ook niet echt veel hielp, is het inladen van de PlayerManager onderbroken (C)"));
					return;
				}
			}
			fis = new FileInputStream(f);
			byte[] header = new byte[16];
			fis.read(header);
			if(header[0] != VersionA || header[1] != VersionB){
				this.DisableCrash(new Exception("De Bestandsversie van " + f.getAbsolutePath() + " is niet ondersteund"));
				sb.sessionSys.releaseAccess();
				try{
					fis.close();
				}catch(Exception e){}
				return;
			}
	
			int bytecount = 0;
			int i = fis.available();
			while(i > 0){
				bytecount += i;
				fis.skip(i);
				i = fis.available();
			}
			this.counter = (short) (bytecount / 64);
	
		} catch (Exception e) {
			sb.sessionSys.releaseAccess();
			this.DisableCrash(new Exception("Kon het SpelersBestand niet inladen", e));
		}
		try{
			fis.close();
		}catch(Exception e){}
		
		sb.sessionSys.releaseAccess();
	}

	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	public void loadSpelerBestand() throws Exception{
		if(this.preventAction())return;
		this._loadSpelerBestandPrivate();
	}

	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	private void _loadPlayersPrivate(Player[] players){
		this._online.clear();
		this.loadedPlayers.clear();
		this.slq = new StartupLoadQueue(players, this);
		Thread t = new Thread(slq);
		t.start();
	}

	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	public void loadPlayers(Player[] players) {
		if(this.preventAction())return;
		this._loadPlayersPrivate(players);
	}

	protected final void _loadPersonOnlinePrivate(Player pl, boolean checkOnline){
		if(pl == null)return;
		
		/*if(checkOnline && !pl.isOnline()){//Als een speler online komt, is die nog niet gemarkeerd als online
			System.out.println("PlayerManager: Persoon online laden is onderbroken omdat de speler niet online is");
			return;
		}*/
		if(this.loadedPlayers == null)this.loadedPlayers = new ArrayList<Person>();
		if(this._online == null)this._online = new ArrayList<Person>();
		
		Person p = null;
		UUID id = pl.getUniqueId();
		//if(Main.plugin.developermodus && Main.isDeveloper(id)){
		//	p = Person.DEV;
		//}else{
		//System.out.println("PlayerManager: Zoeken in alreeds geladen personen");
		for(int i = 0; i < this.loadedPlayers.size(); i++){
			if(this.loadedPlayers.get(i) != null && id.equals(this.loadedPlayers.get(i).getUUID())){
				p = this.loadedPlayers.get(i);
			}
		}
		//System.out.println("PlayerManager: Gezocht is alreeds geladen personen");
		if(p == null){
			//System.out.println("PlayerManager: De persoon is niet gevonden in alreeds geladen personen, dus er wordt verder gezocht");
			try {
				p = _getSavedPlayerPrivate(pl.getUniqueId());
			} catch (IOException e) {
				Logger.getLogger("Minecraft").warning("[KKP] Kon het SpelersBestand van \"" + pl.getName() + "\" met UUID " + pl.getUniqueId() + " niet inladen: " + e);
				e.printStackTrace();
				return;
			}
			//System.out.println("PlayerManager: Gezocht in bewaarde personen, p = " + ((p == null)?"null":p));
			if(p == null){
				//System.out.println("PlayerManager: Er wordt een nieuw profiel aangemaakt voor de speler \"" + pl.getName() + "\" met UUID " + pl.getUniqueId());
				p = new Person(pl, this);
			}
		}
		if(!this._online.contains(p))this._online.add(p);
		//}
		//System.out.println("PlayerManager: Start-up processen van de inladende persoon worden nu uitgevoerd");
		p.online = true;
		p.configure(pl);
		p.getSpelerOptions().refreshRank();
		//p.ApplyRankTag();
		
		pl.sendMessage("§9Je rank is " + Rank.getRankName(p.getSpelerOptions().getRank()));
		if(p.getSpelerOptions().getAmountDailyDiamonds() > 0 && p.getSpelerOptions().DailyDiamondReady()){
			pl.sendMessage("§eJe dagelijkse diamonds zijn nog niet opgehaald! Krijg ze door §c/getdailydiamonds§f te gebruiken.");
		}
		if(DebugTools.developermodus && Main.isDeveloper(id))pl.sendMessage("Developermodus: true");
		
		if(Main.pulser != null){
			Main.pulser.onPlayerLogin(p);
		}
		if(this.as == null)this.as = new AutoSaver(this);
		this.as.checkConditions();
	}

	protected final void loadPersonOnline(Player pl, boolean checkOnline){
		if(this.preventAction())return;
		this._loadPersonOnlinePrivate(pl, checkOnline);
	}

	/*	protected void setResource(File f){
		if(f != null && !f.getAbsolutePath().startsWith(folderpath)){
			//File newFile = new File(this.folderpath + "spelerbestand");+
			operationMode = true;
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Opgegeven nieuw spelerbestand is niet in de vereiste directory");
			return;
		}
		Spelerbestand s = new Spelerbestand(f, this);
		this.resource.markForDelete();
		this.resource = s;
		try {
			checkFileExists(f);
			
			FileInputStream fis = new FileInputStream(f);
			
			if(fis.read() == versionA && fis.read() == versionB){
				Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Playermanager: Bestandsversie ondersteunt");
			}else{
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Playermanager: Bestandsversie is niet ondersteunt");
				operationMode = true;
				Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Playermanager is nu in operationMode");
			}
			edition = ((fis.read() << 24) | (fis.read() << 16) | (fis.read() << 8) | (fis.read()));
			
			String newpath = f.getAbsolutePath();
			newpath = newpath.substring(folderpath.length());
			FileOutputStream fos = new FileOutputStream(linkingpath);
			BufferedWriter d = new BufferedWriter(new OutputStreamWriter(fos));
			d.write(newpath.toCharArray());
			d.close();
			fos.close();
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Playermanager kon spelerbestand niet inladen");
			operationMode = true;
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Playermanager is nu in operationMode");
		}
	}
	
	protected void writeCompleted(){
		this.setResource(write);
		try{
			
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Kon Spelerbestandspad niet veranderen");
		}
	}*/
	
	protected void loadPersonExtra(UUID id){
		if(id == null)return;
		for(int i = 0; i < loadedPlayers.size(); ++i){
			if(loadedPlayers.get(i).getUUID().equals(id))return;
		}
		Person p;
		try {
			p = this._getSavedPlayer(id);
		} catch (Exception e) {return;}
		if(p != null && !this.loadedPlayers.contains(p))loadedPlayers.add(p);
	}

	public Person[] getOnlinePlayers(){
		if(this._online == null)return new Person[0];
		Person[] abc = new Person[this._online.size()];
		this._online.toArray(abc);
		return abc;
	}

	public Person getPlayer(String name){
		if(this.preventAction())return null;
		{
		Person p = getLoadedPlayer(name);
		if(p != null)return p;
		}
		try{
			return _getSavedPlayer(name);
		}catch(Exception e){}
		return null;
	}

	public Person getPlayer(UUID id){
		if(this.preventAction())return null;
		{
		Person p = getLoadedPlayer(id);
		if(p != null)return p;
		}
		try {
			return _getSavedPlayer(id);
		} catch (IOException e) {
			return null;
		}
	}

	public Person getLoadedPerson(CommandSender a){
		if(this.preventAction())return null;
		//System.out.println("PlayerManager: Een persoon wordt gezocht");
		
		if(a == null){
			//System.out.println("PlayerManager: De gezochte persoon is null");
			return null;
		}		if(a instanceof Player){
			//System.out.println("PlayerManager: De gezochte persoon is een Player");			return getLoadedPlayer((Player)a);		}else if(a instanceof ConsoleCommandSender || a instanceof RemoteConsoleCommandSender){
			//System.out.println("PlayerManager: De gezochte persoon is een ConsoleCommandSender");			return Person.CONSOLE;		}else if(a instanceof BlockCommandSender || a instanceof CommandMinecart){
			//System.out.println("PlayerManager: De gezochte persoon is een BlockCommandSender of een CommandMinecart");
			return Person.BLOCKEXECUTOR;
		}		return null;	}	public Person getLoadedPlayer(Player p){
		if(this.preventAction())return null;
		if(DebugTools.developermodus && Person.DEV.isProfileOf(p)){
			return Person.DEV;
		}		for(int i = 0; i < this.loadedPlayers.size(); ++i){
			if(this.loadedPlayers.get(i) == null)continue;
			if(this.loadedPlayers.get(i).isProfileOf(p))return this.loadedPlayers.get(i);		}		return null;	}
	/*public PlayerManager(InputStream is) throws Exception{
		byte[] data = new byte[is.available()];
		try {
			is.read(data);
		} catch (IOException e) {
			System.out.println("Kan playerbestand niet laden: " + e.getMessage());
		}
		byte aantal = (byte) (data.length / 48);
		p = new Person[aantal];
		for(byte i = 0; i < aantal; i++){
			p[i] = Person.LoadFrom(data, i * 48);
		}
		is.close();
		loaded = true;
	}*/
	
	/*public void checkFileExists(File f) throws Exception{
		if(f == null)return;
		f.createNewFile();
		f.mkdirs();
		
		if(!f.exists()){
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aanmaken...");
			InputStream is = me.vincentk.kartoffelkanaalplugin.Main.class.getResourceAsStream("playerdata.bin");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			{
				byte[] a = new byte[16];
				is.read(a);
				fos.write(a);
			}
			
			byte[] buffer = new byte[64];
			
			while(is.available() > 63){
				is.read(buffer);
				fos.write(buffer);
			}
			
			buffer = new byte[16];
			while(is.available() > 15){
				is.read(buffer);
				fos.write(buffer);
			}
			
			while(is.available() > 0){
				fos.write(is.read());
			}
			System.out.println("Data gekopieerd");
			fos.close();
			is.close();
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Nieuw SpelerBestand aangemaakt");
		}
	}*/
	
	protected Person getLoadedPlayer(UUID id){
		if(this.preventAction())return null;
		if(id == null)return null;
		
		if(DebugTools.developermodus && Main.isDeveloper(id))return Person.DEV;
		for(int i = 0; i < loadedPlayers.size(); i++){
			if(id.equals(loadedPlayers.get(i).getUUID()))return loadedPlayers.get(i);
		}
		return null;
	}

	protected Person getLoadedPlayer(String name){
		if(this.preventAction())return null;
		if(name == null || name.length() == 0)return null;
		if(name.length() > 16){
			try{
				UUID id = UUID.fromString(name);
				return getLoadedPlayer(id);
			}catch(Exception e){
				return null;
			}
		}
		if(DebugTools.developermodus && Person.DEV.getName() != null && Person.DEV.getName().equals(name))return Person.DEV;
		if(Person.CONSOLE.getName().equals(name))return Person.CONSOLE;
		for(int i = 0; i < this.loadedPlayers.size(); i++){
			if(this.loadedPlayers.get(i).getName().equals(name))return this.loadedPlayers.get(i);
		}
		return null;
	}
	private Person _getSavedPlayer(String name) throws IOException{
		if(this.preventAction())return null;
		if(name == null || name.length() == 0)return null;
		if(name.length() > 16){
			try{
				UUID id = UUID.fromString(name);
				return _getSavedPlayer(id);
			}catch(Exception e){
				return null;
			}
		}
		SecureBestand res = this.res.getResource();
		if(!res.sessionSys.acquireAccess())return null;
		
		byte[] compare = NamefromString(name);
		
		InputStream is;
		try{
			is = new FileInputStream(res.getFile());
		}catch(Exception ex){
			res.sessionSys.releaseAccess();
			return null;
		}
		is.skip(16);
		if(is.available() > 16000){
			if(is.available() > 25600){
				Logger.getLogger("Minecraft").warning("Spelerbestand is te groot, spelers met ID >= 400 worden genegeerd");
			}
			System.out.println("[KartoffelKanaalPlugin] Spelerbestand is heel groot, hierdoor wordt het systeem mogelijk zwaar vertraagd bij login");
			System.out.println("[KartoffelKanaalPlugin] We raden je aan het spelerbestand te cleanup'en");
		}
		byte[] id = new byte[16];
		byte[] a = new byte[16];
		byte b;
		short i = 0;
		while(is.available() >= 64 && i < 25600){
			is.read(id);
			is.read(a);
			for(b = 0; b < 16; ++b){
				if(compare[b] != a[b])break;
			}
			if(b == 16){
				byte[] abc = new byte[32];
				is.read(abc);
				is.close();
				res.sessionSys.releaseAccess();
				Person p = null;
				try {
					p = Person.LoadFrom(id, a, abc, i);
				} catch (Exception e) {}
				return p;
			}
			is.skip(32);
			++i;
		}
		is.close();
		res.sessionSys.releaseAccess();
		return null;
	}
	private Person _getSavedPlayerPrivate(UUID id) throws IOException{
		if(id == null)return null;
		
		SecureBestand res = this.res.getResource();
		if(res == null)return null;
		if(!res.sessionSys.acquireAccess()){
			return null;
		}
		
		byte[] compare;
		{
			long uuidm = id.getMostSignificantBits();
			long uuidl = id.getLeastSignificantBits();
			compare = new byte[]{
					(byte) ((uuidm >>> 56) & 0xFF),
					(byte) ((uuidm >>> 48) & 0xFF),
					(byte) ((uuidm >>> 40) & 0xFF),
					(byte) ((uuidm >>> 32) & 0xFF),
					(byte) ((uuidm >>> 24) & 0xFF),
					(byte) ((uuidm >>> 16) & 0xFF),
					(byte) ((uuidm >>>  8) & 0xFF),
					(byte) ( uuidm         & 0xFF),
					
					(byte) ((uuidl >>> 56) & 0xFF),
					(byte) ((uuidl >>> 48) & 0xFF),
					(byte) ((uuidl >>> 40) & 0xFF),
					(byte) ((uuidl >>> 32) & 0xFF),
					(byte) ((uuidl >>> 24) & 0xFF),
					(byte) ((uuidl >>> 16) & 0xFF),
					(byte) ((uuidl >>>  8) & 0xFF),
					(byte) ( uuidl         & 0xFF)
			};
		}	
		FileInputStream is;
		try{
			is = new FileInputStream(res.getFile());
		}catch(Exception ex){
			res.sessionSys.releaseAccess();
			return null;
		}
		
		is.skip(16);
		if(is.available() > 16000){
			if(is.available() > 25600){
				Logger.getLogger("Minecraft").warning("Spelerbestand is te groot, spelers met ID >= 400 worden genegeerd");
			}
			System.out.println("[KartoffelKanaalPlugin] Spelerbestand is heel groot, hierdoor wordt het systeem mogelijk zwaar vertraagd bij login");
			System.out.println("[KartoffelKanaalPlugin] We raden je aan het spelerbestand te cleanup'en");
		}
		byte[] a = new byte[16];
		byte b;
		short i = 0;
		while(is.available() >= 64 && i < 25600){
			is.read(a);
			for(b = 0; b < 16; ++b){
				if(compare[b] != a[b])break;
			}
			if(b == 16){
				byte[] name = new byte[16];
				is.read(name);
				byte[] data = new byte[32];
				is.read(data);
				is.close();
				res.sessionSys.releaseAccess();
				Person p = null;
				try{
					p = Person.LoadFrom(a, name, data, i);
				}catch(Exception e){}
				return p;
			}
			is.skip(48);
			i++;
		}
		is.close();
		res.sessionSys.releaseAccess();
		return null;
		
	}

	private Person _getSavedPlayer(UUID id) throws IOException{
		if(this.preventAction()){
			System.out.println("_getSavedPlayer: preventAction staat aan");
			return null;
		}
		return this._getSavedPlayerPrivate(id);
	}
	
	protected void Save(boolean backup){
		if(this.saver == null)throw new NullPointerException("De SaveQueue van de PlayerManager is null");
		this.saver.add((Person[])this.loadedPlayers.toArray(), backup);
	}
	/*protected void Save(String path){//NOTE: src-values mogen niet veranderen
		ArrayList<Person> array = new ArrayList<Person>();
		ArrayList<Person> unplaced = new ArrayList<Person>();
		array.addAll(online);
		array.addAll(othersLoaded);
		System.out.println("[KartoffelKanaalPlugin] Spelers sorteren...");
		for(int i = 0; i < array.size(); ++i){
			if(array.get(i).getSrcLocation() < 0){
				if(array.get(i).getSrcLocation() != -100){
					unplaced.add(array.get(i));
				}
				array.remove(i);
				--i;
			}
		}
		boolean changed;
		do{
			changed = false;
			for(int t = 0; t < array.size(); ++t){
				for(int i = 1; i < array.size(); ++i){
					if(array.get(i).getSrcLocation() < array.get(i - 1).getSrcLocation()){
						Person cache = array.get(i - 1);
						array.set(i - 1, array.get(i));
						array.set(i, cache);
						changed = true;
					}
				}
			}
		}while(changed);
		System.out.println("[KartoffelKanaalPlugin] Spelers gesorteerd");
		Save(path, (Person[])array.toArray(), (Person[])unplaced.toArray());
	}
	protected void Save(String path, Person[] array, Person[] unplaced){
		boolean previous = this.isServiceFrozen();
		this.freezeOperator.freezeSystemError(r, detail, Thread.getAllStackTraces());true;
		
		
		File main = new File(path + File.separatorCharChar + "playerdata.bin");
		File fallback = new File(this.folder.getAbsolutePath() + File.separatorChar + "fallback.tmp");
		Main.pm.setFilePath(fallback.getAbsolutePath());
		try {
			Files.copy(main.toPath(), fallback.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("[KartoffelKanaalPlugin] Kan spelerbestand niet kopiëren voor fallback");
		}
		
		this.saveCurrent();
		
		Main.pm.setFilePath(main.getAbsolutePath());
		fallback.delete();
		
		
		try{
		File dest = new File(path);
		dest.createNewFile();
		}catch(Exception ex){
			System.out.println("[KartoffelKanaalPlugin] Kan spelerbestandfile niet vinden of aanmaken");
		}
		
		FileOutputStream s;
		try{
			s = new FileOutputStream(path);
		}catch(Exception ex){
			System.out.println("[KartoffelKanaalPlugin] Kan niet schrijven naar spelerbestandfile");
		}
		s.
		
		this.freezeOperator.freezeSystemError(r, detail, Thread.getAllStackTraces());previous;
	}*/
	
	
	
	
/*	protected void setResource(File f){
		if(f != null && !f.getAbsolutePath().startsWith(folderpath)){
			//File newFile = new File(this.folderpath + "spelerbestand");+
			operationMode = true;
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Opgegeven nieuw spelerbestand is niet in de vereiste directory");
			return;
		}
		Spelerbestand s = new Spelerbestand(f, this);
		this.resource.markForDelete();
		this.resource = s;
		try {
			checkFileExists(f);
			
			FileInputStream fis = new FileInputStream(f);
			
			if(fis.read() == versionA && fis.read() == versionB){
				Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Playermanager: Bestandsversie ondersteunt");
			}else{
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Playermanager: Bestandsversie is niet ondersteunt");
				operationMode = true;
				Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Playermanager is nu in operationMode");
			}
			edition = ((fis.read() << 24) | (fis.read() << 16) | (fis.read() << 8) | (fis.read()));
			
			String newpath = f.getAbsolutePath();
			newpath = newpath.substring(folderpath.length());
			FileOutputStream fos = new FileOutputStream(linkingpath);
			BufferedWriter d = new BufferedWriter(new OutputStreamWriter(fos));
			d.write(newpath.toCharArray());
			d.close();
			fos.close();
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Playermanager kon spelerbestand niet inladen");
			operationMode = true;
			Logger.getLogger("Minecraft").info("[KartoffelKanaalPlugin] Playermanager is nu in operationMode");
		}
	}
	
	protected void writeCompleted(){
		this.setResource(write);
		try{
			
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Kon Spelerbestandspad niet veranderen");
		}
	}*/
	
	
	
	/*protected Person getPersonSaveAccess(String name){
		if(name == null)return null;
		if(name.length() > 16){
			try{
				UUID id = UUID.fromString(name);
				return getPersonSaveAccess(id);
			}catch(Exception e){
				return null;
			}
		}
		for(int i = 0; i < loadedPlayers.size(); ++i){
			if(loadedPlayers.get(i).getName().equals(name))return loadedPlayers.get(i);
		}
		Person p;
		try {
			p = this._getSavedPlayer(name);
		} catch (IOException e) {
			return null;
		}
		if(p == null)return null;
		loadedPlayers.add(p);
		return p;
	}
	
	protected void getPersonSaveRelease(Person p){
		if(this.saver != null)this.saver.add(p);
		for(int i = 0; i < loadedPlayers.size(); i++){
			if(loadedPlayers.get(i).equals(p)){
				loadedPlayers.remove(i);
				return;
			}
		}
	}
	*/
	
	protected void saveAllPlayers(boolean backup) throws Exception {
		if(this.saver == null)throw new NullPointerException("De SaveQueue van de PlayerManager is null");
		List<Person> p = new ArrayList<Person>();
		p.addAll(this.loadedPlayers);
		this.saver.add((Person[])p.toArray(), backup);
	}
	
	/*protected void Save(String path){//NOTE: src-values mogen niet veranderen
		ArrayList<Person> array = new ArrayList<Person>();
		ArrayList<Person> unplaced = new ArrayList<Person>();
		array.addAll(online);
		array.addAll(othersLoaded);
		System.out.println("[KartoffelKanaalPlugin] Spelers sorteren...");
		for(int i = 0; i < array.size(); ++i){
			if(array.get(i).getSrcLocation() < 0){
				if(array.get(i).getSrcLocation() != -100){
					unplaced.add(array.get(i));
				}
				array.remove(i);
				--i;
			}
		}
		boolean changed;
		do{
			changed = false;
			for(int t = 0; t < array.size(); ++t){
				for(int i = 1; i < array.size(); ++i){
					if(array.get(i).getSrcLocation() < array.get(i - 1).getSrcLocation()){
						Person cache = array.get(i - 1);
						array.set(i - 1, array.get(i));
						array.set(i, cache);
						changed = true;
					}
				}
			}
		}while(changed);
		System.out.println("[KartoffelKanaalPlugin] Spelers gesorteerd");
		Save(path, (Person[])array.toArray(), (Person[])unplaced.toArray());
	}
	protected void Save(String path, Person[] array, Person[] unplaced){
		boolean previous = this.isServiceFrozen();
		this.freezeOperator.freezeSystemError(r, detail, Thread.getAllStackTraces());true;
		
		
		File main = new File(path + File.separatorCharChar + "playerdata.bin");
		File fallback = new File(this.folder.getAbsolutePath() + File.separatorChar + "fallback.tmp");
		Main.pm.setFilePath(fallback.getAbsolutePath());
		try {
			Files.copy(main.toPath(), fallback.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("[KartoffelKanaalPlugin] Kan spelerbestand niet kopiëren voor fallback");
		}
		
		this.saveCurrent();
		
		Main.pm.setFilePath(main.getAbsolutePath());
		fallback.delete();
		
		
		try{
		File dest = new File(path);
		dest.createNewFile();
		}catch(Exception ex){
			System.out.println("[KartoffelKanaalPlugin] Kan spelerbestandfile niet vinden of aanmaken");
		}
		
		FileOutputStream s;
		try{
			s = new FileOutputStream(path);
		}catch(Exception ex){
			System.out.println("[KartoffelKanaalPlugin] Kan niet schrijven naar spelerbestandfile");
		}
		s.
		
		this.freezeOperator.freezeSystemError(r, detail, Thread.getAllStackTraces());previous;
	}*/
	
	@EventHandler
	public final void onPlayerJoin(PlayerJoinEvent e){
		if(this.preventAction())return;
		//System.out.println("PlayerManager: onPlayerLogin event bezig (preventAction: " + this.preventAction() + ")");
		try{
			this.loadPersonOnline(e.getPlayer(),true);
		}catch(Exception ex){
			Logger.getLogger("Minecraft").warning("[KKP] PlayerManager: Kon een persoon gezonden via een onPlayerLogin-event niet inladen:");
			ex.printStackTrace();
		}
		//System.out.println("PlayerManager: onPlayerLogin event afgelopen");
	}

	@EventHandler
	public final void onPlayerQuit(PlayerQuitEvent e){
		if(this.preventAction())return;
		Person person = getLoadedPlayer(e.getPlayer());
		if(person != null){
			_online.remove(person);
			Main.pulser.onPlayerLeave(person);
			person.online = false;
			if(person.getKartoffelID() != -100){
				try{
					short dest = person.getKartoffelID();
					if(dest >= 0){
						this.saver.add(person);
					}
				}catch(Exception ex){
					Logger.getLogger("Minecraft").warning("[KKP] Speler \"" + e.getPlayer().getName() + "\" is mogelijk niet bewaard wegens een fout: " + ex);
					ex.printStackTrace();
				}
			}
		}
	
	}

	@EventHandler(priority = EventPriority.LOW)
	public final void onPlayerChat(AsyncPlayerChatEvent e){
		if(this.preventAction())return;
		Person p = this.getLoadedPlayer(e.getPlayer());
		if(p != null){
			p.getSpelerOptions().refreshPrefix();
		}
	}

	protected short getAvailableLocation(){
		if(counter == Short.MAX_VALUE){
			Logger.getLogger("Minecraft").warning("[KKP] Maximum aantal personen in SpelerBestand bereikt. Hierdoor zal dataverlies ontstaan van de nieuwste spelers");
			return Short.MAX_VALUE;
		}
		return counter++;//counter moet pas vermeerderd worden na het returnen omdat bij het initialiseren nij de constructor het totale aantal wordt toegekend
	}

	/*protected void Disable(Logger l) {
		this.freezeOperator.freezeSystemError(r, detail, Thread.getAllStackTraces());true;
		try {
			this.saveAllPlayers(false);
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Kon de de Spelers niet bewaren: " + e.getMessage());
		}
	}*/
	
	public void unloadPlayer(Person p){
		if(p == null || p.isCurrentlyUsed())return;
		
		for(int i = 0; i < this._online.size(); i++){
			if(this._online.get(i) == p)this._online.remove(i);
		}
		for(int i = 0; i < this.loadedPlayers.size(); i++){
			if(this.loadedPlayers.get(i) == p)this.loadedPlayers.remove(i);
		}
	}
	
	public void _debugPrintLoadedPlayers(){
		System.out.println("PlayerManager: Aantal loaded persons = " + this.loadedPlayers.size());
		for(int i = 0; i < this.loadedPlayers.size(); i++){
			try{
				if(this.loadedPlayers.get(i) == null){
					System.out.println("PlayerManager: Loaded person " + i + " is null");
					continue;
				}
				System.out.println("------------------------------------------------------------------");
				System.out.println("Loaded person " + i + ": " + this.loadedPlayers.get(i));
				this.loadedPlayers.get(i).printInfo(Main.plugin.getServer().getConsoleSender());
				System.out.println("------------------------------------------------------------------");
				System.out.println("    ");
			}catch(Exception ex){
				System.out.println("PlayerManager: Kon persoon " + i + " niet weergeven");
				ex.printStackTrace();
			}
		}
	}
	public static byte[] NamefromString(String s){
		byte[] a = new byte[16];
		if(s == null)return a;
		for(int i = 0; i < s.length() && i < 16; ++i){
			a[i] = (byte) s.charAt(i);
		}
		return a;
	}
	public static String NamefromBytes(byte[] b){
		StringBuilder name = new StringBuilder(10);
		
		for(int i = 0; i < 16; i++){
			if(b[i] == 0)break;
			name.append((char)b[i]);
		}
		return name.toString();
	}
}
