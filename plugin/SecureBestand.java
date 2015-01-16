package KartoffelKanaalPlugin.plugin;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.ISessionSystemListener;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.SessionSystem;
import org.bukkit.Server;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import java.util.logging.Logger;

public class SecureBestand implements ISessionSystemListener {
	private final File res;
	public final SessionSystem sessionSys;
	//private ArrayList<UUID> o = new ArrayList<UUID>(1);
	private boolean markedForDelete = false;
	private boolean markedForBackup = false;
	private int edition = 0;
	private byte VersionA = (byte) 0xFF;
	private byte VersionB = (byte) 0xFF;
	
	public SecureBestand(File f, boolean isBackup){
		this.sessionSys = new SessionSystem(this, 7);
		this.markedForBackup = isBackup;
		res = f;
		FileInputStream s = null;
		try{
			s = new FileInputStream(f);
			byte[] header = new byte[16];
			if(s.available() < 16){
				s.close();
				throw new Exception("De header is niet aanwezig");
			}
			s.read(header);
			this.VersionA = header[0];
			this.VersionB = header[1];
			this.edition = ((((int)header[2] & 0xFF) << 24) | (((int)header[3] & 0xFF) << 16) | (((int)header[4] & 0xFF) << 8) | ((int)header[5] & 0xFF));
			s.close();
		}catch(Exception e){
			try{
				s.close();
			}catch(Exception ex){}
		}
	}
	
	/*public void removeAccessKey(UUID key){
		o.remove(key);
		if(o.size() == 0 && this.markedForDelete){
			try{
				res.delete();
			}catch(Exception ex){
				try{
					res.deleteOnExit();
				}catch(Exception e){}
			}
		}
	}
	
	public UUID acquireAccessKey(){
		if(!markedForDelete){
			UUID id = UUID.randomUUID();
			o.add(id);
			return id;
		}
		return null;
	}*/
	
	@Override
	public void onAccessReceived(Thread t) {}

	@Override
	public void onAccessReleased() {
		if(this.markedForDelete){
			this.sessionSys.setDenyNew();
			try{
				res.delete();
			}catch(Exception ex){
				try{
					res.deleteOnExit();
				}catch(Exception e){}
			}
		}
	}
	
	public File getFile(/*UUID key*/){
		if(this.sessionSys.hasThreadAccess(Thread.currentThread())/*o.contains(key)*/){
			return res;
		}
		return null;
	}
	
	public void markForDelete(){
		if(markedForBackup)return;
		markedForDelete = true;
		if(this.sessionSys.isReleased()/*o.size() == 0*/){
			this.sessionSys.setDenyNew();
			try{
				res.delete();
			}catch(Exception ex){
				try{
					res.deleteOnExit();
				}catch(Exception e){}
			}
		}
	}
	
	public boolean isMarkedForDelete(){
		return markedForDelete;
	}
	
	
	public boolean markForBackup(){
		if(markedForDelete){
			if(res.exists()){
				markedForDelete = false;
				Logger.getLogger("Minecraft").info("[KKP] Het spelerbestand " + this.toString() + " is nu gemarkeerd als back-up");
				Main.plugin.getServer().broadcast(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, "[KKP: Het spelerbestand " + this.toString() + " is nu gemarkeerd als back-up]");
			}else{
				return false;
			}
		}
		markedForBackup = true;
		return true;
	}
	
	public boolean isMarkedForBackup(){
		return markedForBackup;
	}
	
	public int getEdition(){
		return edition;
	}
	
	@Override
	public String toString(){
		return "[Spelerbestand, PATH = " + ((this.res == null)?"null":this.res.getAbsolutePath()) + ", EDITION = " + this.edition + ", BACKUP = " + this.markedForBackup + ", MARKEDDELETE = " + this.markedForDelete + "]";
	}
	
	public boolean fileEquals(File f){
		if(this.res == f)return true;
		if(this.res == null || f == null)return false;
		return this.res.getAbsolutePath().equals(f.getAbsolutePath());
	}
	
	public boolean fileEquals(SecureBestand sb){
		if(sb == null)return false;
		return this.fileEquals(sb.res);		
	}
	public boolean isFileAccessable(byte VersionA, byte VersionB){
		return this.VersionA == VersionA && this.VersionB == VersionB && this.isFileAccessable();
	}
	
	public boolean isFileAccessable(){
		return !this.markedForDelete && this.res != null && this.res.exists() && this.res.isFile();
	}
	
	public String getFileName(){
		return (this.res==null?("Onbekend" + (new Random()).nextInt(10000)):this.res.getName());
	}
	
	public void setKeyPath(int i){
		if(i >= 0 && i < Main.plugin.keypaths.length && this.res != null && this.res.getAbsolutePath().startsWith(Main.plugin.getDataFolderPath())){
			Main.plugin.keypaths[i] = this.res.getAbsolutePath().substring(Main.plugin.getDataFolderPath().length());
		}
	}
	
	protected File _sneakFile(){
		return this.res;
	}
}
