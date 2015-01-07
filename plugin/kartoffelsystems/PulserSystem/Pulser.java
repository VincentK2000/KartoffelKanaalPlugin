package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.DataFieldInt;
import KartoffelKanaalPlugin.plugin.DataFieldShort;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.OverwritingFile;
import KartoffelKanaalPlugin.plugin.RenewableFile;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.KartoffelService;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class Pulser extends KartoffelService implements Runnable, IObjectCommandHandable{
	protected Lock fileLock;
	protected boolean writeOnExit = true;

	public final static byte VersionA = 0;
	public final static byte VersionB = 0;

	RenewableFile res;
	protected byte[] header;
	private PulserFileSaver saver;
	private PulserFileLoader loader;
	public DataFieldInt timesTicked;
	public DataFieldShort receiveNotifications;
	
	private int _timeout = -1;
	private long lastChangeTime = 0;
	protected long lastSaveTime = 1;

	public int getTimeout(){
		return this._timeout;
	}
	
	public void setTimeout(int newTimeout){
		this._timeout = (newTimeout < 30000)?30000:newTimeout;
		if(Main.sm != null)Main.sm.notifyChange();
	}
	
	private static Thread t;
	final static String abonneermessage = 
	"[" +
		"{text:\"Volg de Owner: \",color:green,extra:[" +
			
			"{text:Youtube,color:blue," +
				"hoverEvent:{action:show_item,value:\"{id:322,tag:{display:{" +
					"Name:\\\"KartoffelKanaal\\\"," +
					"Lore:[" +
						"\\\"Leuke video's gemaakt door de Owner\\\"," +
						"\\\"(incl. servertours, serversurvival,...)\\\"" +
					"]}}}\"}," +
				"clickEvent:{action:open_url,value:\"https://www.youtube.com/user/KartoffelKanaal\"}},{text:\", \"" +
				"}," +
					
			"{text:Twitch,color:blue," +
				"hoverEvent:{action:show_item,value:\"{id:322,tag:{display:{" +
					"Name:\\\"KartoffelKanaal\\\"," +
					"Lore:[" +
						"\\\"Livestreams door de Owner\\\"" +
						"]}}}\"}," +
				"clickEvent:{action:open_url,value:\"http://www.twitch.tv/KartoffelKanaal\"}}]" +
			"}," +
					
				
			
		"{text:\" | Anderen: \",color:gray,extra:[" +
			
			"{text:ELGamer," +
				"hoverEvent:{action:show_item,value:\"{id:322,tag:{display:{" +
					"Name:\\\"Laurens Wolfert\\\"," +
					"Lore:[" +
						"\\\"Engelstalige videos\\\"" +
					"]}}}\"}," +					
				"clickEvent:{action:open_url,value:\"https://www.youtube.com/user/wolfert66\"}" +
			"}" +
				
			", " +
			
			"{text:Merlijn," +
				"hoverEvent:{action:show_item,value:\"{id:322,tag:{display:{" +
					"Name:\\\"The King of Redstone (Merlijn)\\\"," +
					"Lore:[" +
						"\\\"Redstone videos\\\"" +
					"]}}}\"}," +
				"clickEvent:{action:open_url,value:\"https://www.youtube.com/channel/UCRaVPcUcH0VkWDCnMP0bXSQ\"}" +
			"}" +
				
			", " + 
				
			"{text:Jelle," +
			"hoverEvent:{action:show_item,value:\"{id:322,tag:{display:{" +
				"Name:\\\"Jelle van den Aakster\\\"," +
				"Lore:[" +
					"\\\"- Skywars\\\"" +
					"\\\"- Andere Minecraft PvP mini-games\\\"" +
				"]}}}\"}," +
			"clickEvent:{action:open_url,value:\"https://www.youtube.com/channel/UCJ2zukd7RcVYfDLzFNiJMnw\"}" +
			"}" +
		"]}" +
	"]";
		
	final static String doneermessage = 
		"{text:\"Steun de server door te doneren: \",color:light_purple," +
			"clickEvent:{action:run_command,value:\"/donateur\"}," +
			"extra:[{text:\"Meer info\",color:blue," +
				"hoverEvent:{action:show_text,value:\"Klik voor meer info\"}" +
			"}]" +
		"}";
	
	public static final PulserNotif AbonneerNotification = new PulserNotifStandard(new PNTech[]{new PNTechTextProvRaw(abonneermessage, false, 100, null)}, false, (byte)0x30, (byte)0, 1, 0);
	public static final PulserNotif DoneerNotification = new PulserNotifStandard(new PNTech[]{new PNTechTextProvRaw(doneermessage, false, 101, null)}, false, (byte)0x30, (byte)1, 3, 0);
	/*public static final PulserNotif TestNotification = new PulserNotifStandard(new PNTech[]{
				new PNTechTextProvFormattedVideo(
					new String[]{
						"Nieuwe Spawn! Server Tour #5",
						"WHfLvR_XK0s",
						"KartoffelKanaal",
						"KartoffelKanaal"
					},
					false, 301, null
				),
				
				new PNTechTextProvFormattedVideo(
					new String[]{
						"Laurens' Server Play EP25: Spawn Town!",
						"7SpCJyW7hMM",
						"ELGamer",
						"wolfert66"
					},
					false, 302, null
				),
				
				new PNTechCondition(
					new PNConditionNOT(
						new PNConditionConstant(
							false, false, 304, null
						),
						(byte) 0x00, false, 305, null
					),
					false, 303, null
				)
			}, false, (byte)0x00, (byte) 100, 0, 0);
	*/
	public PulserNotif[] notifications = new PulserNotif[16]/*{Pulser.AbonneerNotification, Pulser.DoneerNotification}*/;
	private ReentrantLock NotificationLock = new ReentrantLock();

	public static int tickcount = 0;
	
	//static boolean enabled = true;
	
	public Pulser(){
		super("Pulser");
		try {
			this.res = new RenewableFile(Main.plugin.keypaths[4], 5, "PulserFile", Main.plugin.keypaths[5], VersionA, VersionB);
		} catch (Exception e) {
			this.DisableCrash(new Exception("Kon de Res niet aanmaken", e));
		}
		
	}
	
	public void run(){
		if(this.preventAction()){
			Logger.getLogger("Minecraft").warning("[KKP] Pulser.ticker kan niet runnen omdat preventAction aan is");
			return;
		}
		if(this._timeout < 30000){
			Logger.getLogger("Minecraft").warning("[KKP] Pulser.ticker kan niet runnen omdat timeout < 30000. Timeout = " + this._timeout);
			return;
		}
		try{
			Logger.getLogger("Minecraft").info("[KKP] Pulser.ticker is nu bezig");
			while(this.running){
				if(this.preventAction()){
					Logger.getLogger("Minecraft").info("[KKP] Pulser.ticker is gestopt omdat preventAction aan is");
					throw new InterruptedException("preventAction is aan");
				}
				this.executeTick(tickcount++);
				Thread.sleep(this._timeout);
			}
			Logger.getLogger("Minecraft").info("[KKP] Pulser.ticker is gestopt omdat de Pulser-service niet meer actief is");
		}catch(Throwable e){
			if(!(e instanceof InterruptedException)){
				Logger.getLogger("Minecraft").warning("[KKP] Pulser.ticker is gestopt vanwege een foutmelding (niet InterruptedException):");
				e.printStackTrace();
			}
		}
		Logger.getLogger("Minecraft").info("[KKP] Pulser.ticker is gestopt");
	}

	public void start(){
		if(this.preventAction())return;
		if(t != null && t.isAlive())return;
		t = new Thread(this);
		t.start();
	}
		
	public void stop(){
		if(t != null && t.isAlive()){
			t.interrupt();
		}
	}
		
	//public static int numberOfAvailablePlayers(Player without){
	//	return (without == null)?vulnerableVoorPulser.size():vulnerableVoorPulser.size() - 1;
	//	/*int aantalSpelers = Main.plugin.getServer().getOnlinePlayers().length;
	//	if(without != null){
	//		aantalSpelers--;
	//	}
	//	for(Player p : Main.plugin.getServer().getOnlinePlayers()){
	//		if(p == without)continue;
	//		if(without.getUniqueId() == )
	//		/*if(geenNotificatie(p.getName())){
	//			aantalSpelers--;
	//		}*//*
	//	}
	//	return aantalSpelers;*/
	//}
	public void executeTick(int tickcount){	
		try {
			NotificationLock.tryLock(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
		
		if(notifications == null)notifications = new PulserNotif[0];
		if(Main.pm == null)return;
		Person[] online = Main.pm.getOnlinePlayers();
		short[] receiveData = new short[online.length];
		if(this.receiveNotifications != null){
			for(int i = 0; i < online.length; i++){
				if(online[i] != null)receiveData[i] = this.receiveNotifications.getValue(online[i].getKartoffelID());
			}
		}
		boolean[] receivers = new boolean[online.length];
		for(int i = 0; i < notifications.length && i < 16; i++){
			if(this.notifications[i] == null || this.notifications[i].invisible){
				continue;
			}
			short mask = (short) (0x8000 >>> i);
			for(int r = 0; r < receiveData.length; r++){
				receivers[r] = ((receiveData[r] & mask) == mask);
			}
			this.notifications[i].processTick(online, receivers, tickcount);
		}
		if(this.timesTicked != null){
			for(int i = 0; i < online.length; i++){
				if(online[i] != null)this.timesTicked.add(online[i].getKartoffelID(), 1);
			}
		}
		
		try{
			NotificationLock.unlock();
		}catch(Exception e){}
	}
	public void checkTickConditions(Person without){
		if(Main.pm == null || Main.pm.preventAction()){
			this.stop();
			return;
		}
		Person[] online = Main.pm.getOnlinePlayers();
		if(online == null || online.length == 0){
			this.stop();
			return;
		}
		for(int i = 0; i < online.length; i++){
			if(online[i] != null && online[i] != without && online[i].getSpelerOptions().getSwitch((byte) 0x50, false)){
				this.start();
				return;
			}
		}
		this.stop();
	}
	
	protected void repopulateMessageReceivers(){
		
	}

	private void _loadNotifications(){
		if(loader == null)loader = new PulserFileLoader(this);
		loader.loadFile();
	}

	public void loadNotifications(){
		if(this.preventAction())return;
		this._loadNotifications();
	}
	protected void Save(boolean checkChanged){
		if(checkChanged && !this.isChanged())return;
		if(saver == null)saver = new PulserFileSaver(this);
		saver.Save();
	}

	protected void SaveBlocking(boolean checkChanged){
		if(checkChanged && !this.isChanged()){
			Logger.getLogger("Minecraft").info("[KKP] PulserBestand wordt niet bewaard omdat er geen veranderingen waren");
			return;
		}
		if(saver == null)saver = new PulserFileSaver(this);
		Logger.getLogger("Minecraft").info("[KKP] PulserBestand bewaren...");
		saver.SaveBlocking();
		Logger.getLogger("Minecraft").info("[KKP] PulserBestand bewaard");
	}

	public void onPlayerLogin(Person p){
		if(this.preventAction() || p == null)return;
		//if(p.getSpelerOptions().getSwitch((byte) 0x50, false))Pulser.vulnerableVoorPulser.add(p);
		//if(p.getSpelerOptions().getSwitch((byte) 0x60, false))Pulser.receiveNews.add(p);
		//if(p.getSpelerOptions().getSwitch((byte) 0x70, false))Pulser.receiveAdvertisement.add(p);
		
		this.checkTickConditions(null);
	}
	
	public void onPlayerLeave(Person p){
		if(this.preventAction() || p == null)return;
		this.checkTickConditions(p);
	}

	public void initialize(int interval){
		if(this.initialized)return;
		
		if(this.running){
			Logger.getLogger("Minecraft").info("[KKP] Kan de Pulser niet initializen als die aan staat");
		}
		if(this.res == null)
			try {
				this.res = new RenewableFile(Main.plugin.keypaths[4], 5, "PulserFile", Main.plugin.keypaths[5], VersionA, VersionB);
			} catch (Exception e) {
				this.DisableCrash(new Exception("Kon Pulser niet initializen omdat de Res niet kon worden aangemaakt"));
				return;
			}
		this._timeout = interval;
		this.initialized = true;
	}
	
	@Override
	protected void _enableCore() throws Exception {
		this.lastSaveTime = System.currentTimeMillis() - 1;
		this._loadNotifications();
		try {
			this.timesTicked = new DataFieldInt(new OverwritingFile(Main.plugin.keypaths[4] + "stats" + File.separatorChar, -1, Main.plugin.keypaths[4] + "stats" + File.separatorChar + "timesTicked.kkp", DataFieldInt.VersionA, DataFieldInt.VersionB), "Het totaal aantal keren dat een persoon getickt door de pulser", 0);
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] Kon de timesTicked van de pulser niet initializen:");
			e.printStackTrace();
		}
		try {
			this.receiveNotifications = new DataFieldShort(new OverwritingFile(Main.plugin.keypaths[4], -1, Main.plugin.keypaths[4] + "notificationReceivements.kkp", DataFieldInt.VersionA, DataFieldInt.VersionB), "Binaire data die aangeeft wie welke notificaties ontvangt", (short) 0xFFFF);
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] Kon de receiveNotifications van de pulser niet initializen:");
			e.printStackTrace();
		}
		this.checkTickConditions(null);
	}

	@Override
	protected void _disableCore() throws Exception {
		try{
			this.receiveNotifications.SaveBlocking();
		}catch(Exception ex){
			Logger.getLogger("Minecraft").warning("[KKP] Kon de DataFieldShort van de receiveNotifications niet bewaren:");
			ex.printStackTrace();
		}
		this.receiveNotifications = null;
		try{
			this.timesTicked.SaveBlocking();
		}catch(Exception ex){
			Logger.getLogger("Minecraft").warning("[KKP] Kon de DataFieldInt van de timesTicked niet bewaren:");
			ex.printStackTrace();
		}
		this.timesTicked = null;
		this.stop();
		try{
			this.SaveBlocking(true);
		}catch(Exception ex){
			Logger.getLogger("Minecraft").warning("[KKP] Kon PulserFile niet bewaren:");
			ex.printStackTrace();
		}
	}
	
	public boolean isChanged(){
		return this.lastChangeTime >= this.lastSaveTime;
	}
	
	public void notifyChange(){
		this.lastChangeTime = System.currentTimeMillis();
	}
	
	public static IObjectCommandHandable getObjectCommandHandable(IObjectCommandHandable root, String path) throws Exception {
		if(path == null)throw new Exception("Couldn't search for object because the path was null");
		if(root == null)throw new Exception("Couldn't search for object because the root was null");
		if(path.startsWith("/"))path = path.substring(1);
		String[] parts = path.split("/");
		StringBuilder currentPath = new StringBuilder(path.length());
		IObjectCommandHandable result = root;
		int end = parts.length;
		if(parts.length > 0 && parts[parts.length - 1].length() == 0)end = parts.length - 1;
		for(int i = 0; i < end; i++){
			try{
				result = result.getSubObjectCH(parts[i]);
			}catch(Exception e){
				throw new Exception("Fout bij het vinden van object \"" + parts[i] + "\" van \"" + currentPath.toString() + "\": " + e.getMessage(), e);
			}
			if(result == null){
				throw new Exception("Kon het object \"" + parts[i] + "\" van \"" + currentPath.toString() + "\" niet vinden");
			}
			currentPath.append(parts[i]);
			currentPath.append('/');
		}
		return result;
	}

	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		return false;
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a) throws Exception {
		return a;
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		path = path.toLowerCase();
		int pointIndex = path.indexOf((int)'.');
		if(pointIndex == -1)throw new Exception("No domain specified");
		String domain = path.substring(0, pointIndex);
		String item = path.substring(pointIndex + 1, path.length());
		if(domain.equals("notif") || domain.equals("notifications")){
			if(item.startsWith("#")){
				int notifIndex;
				try{
					notifIndex = Integer.parseInt(item.substring(1));
				}catch(NumberFormatException e){
					throw new Exception("Oncorrecte index \"" + item.substring(1) + "\". De index moet op zijn minst al een geheel getal zijn...");
				}
				if(notifIndex < 0 || notifIndex >= this.notifications.length)throw new Exception("Oncorrecte index. De index moet minimum 0 en - in dit geval - maximum " + (this.notifications.length - 1) + " zijn");
				return this.notifications[notifIndex];
			}else{
				throw new Exception("Momenteel kan er enkel een notificatie van de Pulser worden gezocht op index");
			}
		}else{
			throw new Exception("Geldige domeinen: \"notif\"/\"notifications\"");
		}
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception {
		s = s.toLowerCase();
		if("notifications.#".startsWith(s))a.add("notifications.#");
		return a;
	}

	public static String formatListToString(ArrayList<String> list, String separator){
		if(list == null || list.size() == 0 || separator == null)return "";
		StringBuilder sb = new StringBuilder(20);
		for(int i = 0; i < list.size() - 1; i++){
			String s = list.get(i);
			if(s == null || s.length() == 0)continue;
			sb.append(separator);
		}
		String last = list.get(list.size() - 1);
		if(last != null){
			sb.append(last);
		}
		return sb.toString();
	}
	
	/*public static <T> T[] operateArrayCommand(Person executor, CommandSender a, String operationName, String[] operationArgs, AttribSystem attribSys, T[] originalArray){
		operationName = operationName.toLowerCase();
		if(operationName.equals("add")){
			if(operationArgs.length == 0){
				a.sendMessage("§cadd <mode> ...");
				a.sendMessage("§eMogelijke modes: create, copy");
			}else if(operationArgs.length >= 1){
				String modeSelection = operationArgs[0].toLowerCase();
				if(modeSelection.equals("create")){
					if(originalArray instanceof PNTech[]){
						
					}else if(originalArray instanceof PulserNotif[]){
						
					}else{
						a.sendMessage("§4Creatie van dat type object is niet ondersteund");
						return originalArray;
					}
				}else if(modeSelection.equals("copy")){
					if(!(originalArray instanceof PNTech[] || originalArray instanceof PulserNotif[])){
						a.sendMessage("§4Copie§ring van dat type object is niet ondersteund");
						return originalArray;
					}
					if(operationArgs.length != 2){
						a.sendMessage("§cadd copy <copy van path>");
						return originalArray;
					}else{
						String path = operationArgs[1];
						IObjectCommandHandable objCH;
						try{
							objCH = Pulser.getObjectCommandHandable(Main.pulser, path);
						}catch(Exception e){
							a.sendMessage("§4Kon het van-object niet vinden: " + e);
							return originalArray;
						}
						if(objCH == null){
							a.sendMessage("§4Kon het van-object niet vinden");
							return originalArray;
						}
						
						//if(!((Object)(objCH) instanceof Class<?>)){
						//	a.sendMessage("§4Het van-object is niet van het juiste type");
						//	return originalArray;
						//}
						if(originalArray instanceof PNTech[]){
							
						}else if(originalArray instanceof PulserNotif[]){
							
						}
					}						
				}else if(modeSelection.equals("copyinstance")){
					if(operationArgs.length != 2){
						a.sendMessage("§cadd copyinstance <copy van path>");
						return originalArray;
					}else{
						String path = operationArgs[1];
						IObjectCommandHandable objCH;
						try{
							objCH = Pulser.getObjectCommandHandable(Main.pulser, path);
						}catch(Exception e){
							a.sendMessage("§4Kon het van-object niet vinden: " + e);
							return originalArray;
						}
						if(objCH == null){
							a.sendMessage("§4Kon het van-object niet vinden");
							return originalArray;
						}
						T instanceToCopy;
						try{
							instanceToCopy = (T)objCH;
						}catch(Exception e){
							a.sendMessage("§4Couldn't catch object to the right type");
							return originalArray;
						}
						if(originalArray == null){
							originalArray = new T[1];
							originalArray[0] = instanceToCopy;
						}else{
							int freeSpot;
							for(freeSpot = 0; freeSpot < originalArray.length)
						}
					}
				}
			}					
		}else if(operationName.equals("remove")){
		
		}
		return originalArray;
	}*/
}
