package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
//import java.lang.Exception;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.ISessionSystemListener;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.SessionSystem;

public class Person implements ISessionSystemListener{
	protected UUID UniqueID;
	protected String name = "???";
	protected PermissionAttachment pa;
	protected Player player;
	private SpelerOptions a;
	protected final short kartoffelID;
	protected final PlayerManager pm;
	
	protected boolean online = false;
	//private boolean deleteOnSave = false;
	//private ArrayList<Long> accessKeys = new ArrayList<Long>(1);
	public final SessionSystem sessionSys;
	private long _latestAccessTime = 0;
	
	public static final long minLoadTime = 120000/*1200000*/;
	
	public static final Person CONSOLE = new Person(new UUID(0l,1l), "CONSOLE", SpelerOptions.CONSOLE, (short) -100, Main.pm);
	public static Person DEV = new Person(new UUID(0l,5l), "DEVELOPER", SpelerOptions.DEV, (short)-100, Main.pm);
	public static final Person BLOCKEXECUTOR = new Person(new UUID(0l,0l), "BLOCKEXECUTOR", SpelerOptions.BLOCKEXECUTOR, (short)-100, Main.pm);

	
	protected Person(UUID id, String name, SpelerOptions so, short kartoffelID, PlayerManager pm){
		this.sessionSys = new SessionSystem(this, 5);
		if(name.length() == 0 || name == null)name = "???|UUID:" + id.toString(); 
		this.pm = (pm == null)?Main.pm:pm;
		if(!this.pm.loadedPlayers.contains(this))this.pm.loadedPlayers.add(this);
		this.name = name;
		this.UniqueID = id;
		a = (so == null)?SpelerOptions.getDefaultOptions():so;
		a.parent = this;
		this.kartoffelID = Short.valueOf(kartoffelID);
		this._latestAccessTime = System.currentTimeMillis();
	}
	
	protected Person(Player p, PlayerManager pm){
		this.sessionSys = new SessionSystem(this, 5);
		this.pm = (pm == null)?Main.pm:pm;
		if(!this.pm.loadedPlayers.contains(this))this.pm.loadedPlayers.add(this);
		a = SpelerOptions.getDefaultOptions();
		a.parent = this;
		a.setRank((byte) ((p == null)?1:(p.isOp()?70:1)));
		configure(p);
		this.kartoffelID = this.pm.getAvailableLocation();
		this._latestAccessTime = System.currentTimeMillis();
	}
	
	public SpelerOptions getSpelerOptions(){
		return (a==null)?SpelerOptions.getDefaultOptions():a;
	}
	protected void setSpelerOptions(SpelerOptions so){
		a = (so==null)?SpelerOptions.getDefaultOptions():so;
	}
	public static Person LoadFrom(byte[] uuiddata, byte[] namedata, byte[] data, short srcid) throws Exception{
		if(namedata == null || uuiddata == null || data == null)return null;
		if(namedata.length != 16 || uuiddata.length != 16 || data.length != 32)return null;
		//if(a.length < 64)throw new Exception("Array voor input van een player uit playerbestand te kort");
		UUID id = null;
		StringBuilder name = new java.lang.StringBuilder(10);
		{
			long UUIDMost = (long)(
				(((long)uuiddata[ 0]) & 0xFF) << 56 | 
				(((long)uuiddata[ 1]) & 0xFF) << 48 | 
				(((long)uuiddata[ 2]) & 0xFF) << 40 |
				(((long)uuiddata[ 3]) & 0xFF) << 32 | 
				(((long)uuiddata[ 4]) & 0xFF) << 24 | 
				(((long)uuiddata[ 5]) & 0xFF) << 16 | 
				(((long)uuiddata[ 6]) & 0xFF) <<  8 | 
				(((long)uuiddata[ 7]) & 0xFF)
				);
			long UUIDLeast = (long)(
				(((long)uuiddata[ 8]) & 0xFF) << 56 | 
				(((long)uuiddata[ 9]) & 0xFF) << 48 | 
				(((long)uuiddata[10]) & 0xFF) << 40 |
				(((long)uuiddata[11]) & 0xFF) << 32 | 
				(((long)uuiddata[12]) & 0xFF) << 24 | 
				(((long)uuiddata[13]) & 0xFF) << 16 | 
				(((long)uuiddata[14]) & 0xFF) <<  8 | 
				(((long)uuiddata[15]) & 0xFF)
			);
			
			id = new UUID(UUIDMost,UUIDLeast);
			
			for(int i = 0; i < 15; i++){
				if(namedata[i] == 0x00)break;
				name.append((char)namedata[i]);
			}
		}
		return new Person(id, name.toString(), new SpelerOptions(data), srcid, Main.pm);
	}
	public static Person RecoverFromPSF(PersonSaveFormat psf){
		if(psf == null || !PersonSaveFormat.isCorrect(psf))return null;
		//NOTE: De originele data wordt gebruikt, de psf.name en de psf.UUID worden genegeerd
		byte[] uuid = new byte[16];
		byte[] name = new byte[16];
		byte[] data = new byte[32];
		System.arraycopy(psf.data, 0, uuid, 0, 16);
		System.arraycopy(psf.data, 16, name, 0, 16);
		System.arraycopy(psf.data, 32, data, 0, 32);
		try {
			return LoadFrom(uuid, name, data, psf.dest);
		} catch (Exception e) {
			return null;
		}
	}
	public final byte[] getSaveArray() throws IOException{
		byte[] buffer = new byte[64];
		{
			long uuid = this.UniqueID.getMostSignificantBits();
			buffer[ 0] = (byte) ((uuid >>> 56) & 0xFF);
			buffer[ 1] = (byte) ((uuid >>> 48) & 0xFF);
			buffer[ 2] = (byte) ((uuid >>> 40) & 0xFF);
			buffer[ 3] = (byte) ((uuid >>> 32) & 0xFF);
			buffer[ 4] = (byte) ((uuid >>> 24) & 0xFF);
			buffer[ 5] = (byte) ((uuid >>> 16) & 0xFF);
			buffer[ 6] = (byte) ((uuid >>>  8) & 0xFF);
			buffer[ 7] = (byte) ( uuid         & 0xFF);
			
			uuid = this.UniqueID.getLeastSignificantBits();		
			buffer[ 8] = (byte) ((uuid >>> 56) & 0xFF);
			buffer[ 9] = (byte) ((uuid >>> 48) & 0xFF);
			buffer[10] = (byte) ((uuid >>> 40) & 0xFF);
			buffer[11] = (byte) ((uuid >>> 32) & 0xFF);
			buffer[12] = (byte) ((uuid >>> 24) & 0xFF);
			buffer[13] = (byte) ((uuid >>> 16) & 0xFF);
			buffer[14] = (byte) ((uuid >>>  8) & 0xFF);
			buffer[15] = (byte) ( uuid         & 0xFF);
		}
		{
			if(this.name == null)this.name = "";
			int end = this.name.length();
			if(end > 16){
				end = 16;
				Logger.getLogger("Minecraft").warning("[KKP] Bij het saven van een profiel is de naam (" + this.name +") afgekapt naar 16 characters");
			}
				buffer[i + 16] = (byte)this.name.charAt(i);
			}
		}
		
		byte[] data = this.getSpelerOptions().getData();
		for(int i = 0; i < 32; i++){
			buffer[i + 32] = data[i];
		}
		return buffer;
	
	@Override
	public String toString(){
		return (this.UniqueID == null?"nullUniqueID":this.UniqueID.toString()) + ":" + (this.name == null?"nullName":this.name);
	}
	
	public boolean isProfileOf(Player p){
		if(p == null)return false;
		if(this.UniqueID == null){
			Logger.getLogger("Minecraft").warning("[KKP] Person: Het UUID van een Person is null");
			return false;
		}
		
		if(p.getUniqueId().equals(this.UniqueID)){
			this.name = p.getName();
			return true;
		}
		return false;
	}
	
	public void refreshBukkitPermissions(){
		Rank.setBukkitPermissions(this.getSpelerOptions().getRank(), this);
	}
	
	public void sendMessage(String m){
		if(this == CONSOLE){
			Logger.getLogger("Minecraft").info("[KKP] " + m);
		}else{
			if(player != null)player.sendMessage(m);
		}
	}
	
	public static String getPersonalPrefix(UUID id){
		if(id == null)return "";
		
		//Laurens:
		if(id.getMostSignificantBits() == -1495606991996763821L && id.getLeastSignificantBits() == -4952185009421044715L)return "[Youtube]";
		
		//Jelle:
		if(id.getMostSignificantBits() == 646058613273347619L && id.getLeastSignificantBits() == -8526719427494299834L)return "[Youtube]";
		
		//Ik (Vincent):
		if(id.getMostSignificantBits() == -6815922346184850659L && id.getLeastSignificantBits() == -4613213530685400101L)return "[Dev]";
		
		//Thomas:
		if(id.getMostSignificantBits() == 1964234760505215656L && id.getLeastSignificantBits() == -8997581284896420049L)return "[SlothKing]";
		
		//Jaydey:
		if(id.getMostSignificantBits() == 2629709164696322931L && id.getLeastSignificantBits() == -6055477513425670297L)return "[Cheater]";
		
		//Ben:
		if(id.getMostSignificantBits() == -4255981295660744184l && id.getLeastSignificantBits() == -4950278195741411073l)return "[Panda]";
		
		return "";
	}
	
	public UUID getUUID(){
		return new UUID(this.UniqueID.getMostSignificantBits(), this.UniqueID.getLeastSignificantBits());
	}
	
	public String getName(){
		return new String(this.name);
	}
	public void configure(Player pl){
		this.UniqueID = pl.getUniqueId();
		this.name = pl.getName();
		this.player = pl;
		this.getSpelerOptions().refreshRankRequirments();
		this.getSpelerOptions().refreshRank();
	}
	public void printInfo(CommandSender a){
		SpelerOptions o = this.getSpelerOptions();
		a.sendMessage("�eProfiel van " + this.name);
		a.sendMessage("�e------------------------------");
		a.sendMessage("�eUUID: " + this.UniqueID.toString());
		a.sendMessage("�eNaam: " + this.name);
		a.sendMessage("�eKartoffelID: " + this.getKartoffelID());
		a.sendMessage("�eRank: " + Rank.getRankDisplay(o.getRank()));
		a.sendMessage("�eDonateurrank: " + Rank.getRankDisplay(o.getDonatorRank()));
		a.sendMessage("�eLaatste DailyDia day: " + this.getSpelerOptions().getLatestDailyDiamondDay());
		a.sendMessage("�ePersonal Prefix: \"" + Person.getPersonalPrefix(this.UniqueID) + "\"");
		a.sendMessage("�eOnline: " + this.online);
	}
	protected void printInfo(SpelerOptions executor, CommandSender a){
		if(a == null)return;
		if(executor == null){
			try{
				a.sendMessage("ERROR: Onbekende executor");
			}catch(Throwable t){}
			return;
		}
		if(executor.getRank() < 70){
			a.sendMessage("Je hebt de rank Admin nodig");
			return;
		}
		printInfo(a);
	}
	public short getKartoffelID(){
		return this.kartoffelID;
	}
	
	protected void setOnline(boolean state){
		this.online = state;
		this.checkLoadConditions();
	}
	
	/*public long acquireAccessKey(){
		long ans = this.pm.keyRandomizer.nextLong();
		this.accessKeys.add(ans);
		return ans;
	}
	
	public void removeAccessKey(long l){
		this.latestAccessTime = System.currentTimeMillis();
		this.accessKeys.remove(l);
		this.checkLoadConditions();
	}*/
	
	public long getLatestAccessTime(){
		if(this.sessionSys.isReleased()){
			return this._latestAccessTime;
		}else{
			return System.currentTimeMillis();
		}
	}
	
	protected void onSaveComplete(long snapshotTime){
		this.getSpelerOptions().latestSaved = snapshotTime;
		if(this.useFinished()){
			if(this.isChanged()){
				this.pm.saver.add(this);
			}else{
				this.pm.unloadPlayer(this);
			}
		}else if(!this.isCurrentlyUsed()){
			this.pm.as.start();
		}
	}
	
	@Override
	public void onAccessReleased(){
		this.checkLoadConditions();
	}
	
	@Override
	public void onAccessReceived(Thread t) {}
	
	public void checkLoadConditions(){
		if(this.online || !this.sessionSys.isReleased()/*|| this.accessKeys.size() > 0 || this.isCurrentlyUsed()*/)return;
		if(this.useFinished()){
			if(this.isChanged()){
				if(this.pm.as != null)this.pm.as.start();
			}
		}
	}
	
	public boolean isChanged(){
		return this.getSpelerOptions().isChanged();
	}
	
	public boolean isCurrentlyUsed(){
		if(this.kartoffelID < 0)return true;
		return this.online || !this.sessionSys.isReleased()/*this.accessKeys.size() > 0*/;
	}
	
	public boolean useFinished(){
		return !this.isCurrentlyUsed() && (System.currentTimeMillis() - this.getLatestAccessTime()) >= Person.minLoadTime;
	}
	
	public boolean checkCorrectAutoSaveConditions(){
		return this.isChanged() && this.getSpelerOptions().getTimeSinceLatestSave() >= 300000;
	}
}