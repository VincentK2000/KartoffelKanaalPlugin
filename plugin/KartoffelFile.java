package KartoffelKanaalPlugin.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public abstract class KartoffelFile {
	protected int lastEdition = -1;
	protected byte VersionA;
	protected byte VersionB;
	public final String folderPath;
	
	public final int dedicatedResKeyPath;
	
	protected SecureBestand res;
	
	public KartoffelFile(byte verA, byte verB, String folderPath, int dedicatedResKeyPath) throws Exception{
		if(folderPath == null){
			throw new IllegalArgumentException("FolderPath is null");
		}
		{
			File folder = new File(Main.plugin.getDataFolderPath() + folderPath);
			if(!folder.exists() || !folder.isDirectory()){
				folder.mkdirs();
			}
		}
		this.VersionA = verA;
		this.VersionB = verB;
		this.folderPath = folderPath;
		this.dedicatedResKeyPath = dedicatedResKeyPath;
	}
	
	public abstract int getNewFileVersion();
	public abstract File acquireWriteFile(int version, String reason);
	public abstract void writeFileFinished(int newEdition) throws Exception;
	public SecureBestand getResource(){
		return this.res;
	}
	
	public void setResourceFile(SecureBestand s, boolean markOldForDelete){
		SecureBestand old = this.res;
		this.res = s;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(s._sneakFile());
	
			byte[] header = new byte[16];
			fis.read(header);
			if(header[0] != this.VersionA || header[1] != this.VersionB){
				try{
					fis.close();
				}catch(Exception ex){}
				throw new Exception("FileVersion niet ondersteund");
			}
			
			int v = ((int)header[2] & 0xFF) << 24 | ((int)header[3] & 0xFF) << 16 | ((int)header[4] & 0xFF) << 8 | ((int)header[5] & 0xFF);  
			this.lastEdition = v;
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] Fout bij validatie een kartoffel-bestand, PATH = " + (s==null?"null":s._sneakFile().getAbsolutePath()));
		}
		
		try{
			fis.close();
		}catch(Exception e){}
		
		if(this.res != null){
			this.res.setKeyPath(this.dedicatedResKeyPath);
		}
		if(markOldForDelete && old != null && !old.fileEquals(this.res)){
			old.markForDelete();
		}
	}
	
	public static void saveDefaultFile(KartoffelFile kf, String resourcePath) throws Exception{
		if(kf == null || resourcePath == null || resourcePath.length() == 0)throw new IllegalArgumentException("Bij het bewaren van de DefautltFile, waren enkele of meerdere parameters null");
		int fEdition = kf.getNewFileVersion();
		File f = kf.acquireWriteFile(fEdition, "Default");
		FileOutputStream fos = null;
		while(f.exists()){
			fEdition = kf.getNewFileVersion();
			f = kf.acquireWriteFile(fEdition, "Default");
		}
		try{
			f.getParentFile().mkdirs();
			f.createNewFile();
		}catch(Exception e){
			throw new Exception("Kon geen bestand aanmaken om een default bestand te bewaren (of oude verwijderen)", e);
		}
		try {
			fos = new FileOutputStream(f);
		} catch (Exception e) {
			throw new Exception("Kon de OutputStream niet aanmaken om een default bestand te bewaren", e);
		}
		
		InputStream is = null;
		try{
			is = KartoffelKanaalPlugin.plugin.Main.class.getResourceAsStream(resourcePath);
			
			byte[] header = new byte[16];
			header[0] = kf.VersionA;
			header[1] = kf.VersionB;
			
			header[2] = (byte) ((fEdition >>> 24) & 0xFF);
			header[3] = (byte) ((fEdition >>> 16) & 0xFF);
			header[4] = (byte) ((fEdition >>>  8) & 0xFF);
			header[5] = (byte) ((fEdition       ) & 0xFF);
			fos.write(header);
			
			if(is.available() > 0){
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
			}
		}catch(Exception e){
			try{
				fos.close();
			}catch(Exception ex){}
			throw new Exception("Kon de data niet schrijven voor een default bestand te schrijven");
		}
		
		try{
			fos.close();
		}catch(Exception e){}
		
		try{
			is.close();
		}catch(Exception e){}
		
		if(kf.getResource() != null)kf.getResource().markForBackup();
		kf.writeFileFinished(fEdition);
	}
}
