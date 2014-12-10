package KartoffelKanaalPlugin.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

public abstract class DataFieldBase {
	public static final byte VersionA = 0x00;
	public static final byte VersionB = 0x00;
	private KartoffelFile res;
	
	protected String description = "NullDescription";
	protected byte[] descriptionBytes = new byte[0];
	private long startTime = -1;
	private long endTime = -1;
	protected final byte correctEntrySize;
	protected final byte[] defaultEntry;
	
	protected boolean changed = false;
	
	protected final short[] recentKartoffelIDs = new short[24];
	protected final short[] kartoffelIDs = new short[32];
	
	public DataFieldBase(KartoffelFile f, String description, byte correctEntrySize, byte[] defaultEntry) throws Exception{
		if(f == null)throw new IllegalArgumentException("De KartoffelFile is null");
		f.VersionA = VersionA;
		f.VersionB = VersionB;
		for(int i = 0; i < kartoffelIDs.length; i++){
			this.kartoffelIDs[i] = 0x7FFF;
		}
		for(int i = 0; i < this.recentKartoffelIDs.length; i++){
			this.recentKartoffelIDs[i] = 0x7FFF;
		}
		this.correctEntrySize = correctEntrySize;
		this.defaultEntry = fitEntry(defaultEntry, correctEntrySize, this.isNumberStructure());
		this.res = f;
		this.setDescription(description);
		this.loadFile();
	}
	
	public void loadFile() throws Exception {
		if(this.res == null)throw new Exception("Res is null");
		SecureBestand sb = this.res.getResource();
		if(sb == null){
			try{
				this.createNewFile(null);
			}catch(Exception e){
				Logger.getLogger("Minecraft").warning("[KKP] Kon geen nieuwe DataField maken:");
				e.printStackTrace();
			}
			sb = this.res.getResource();
		}
		
		if(sb == null){
			throw new Exception("Het SecureBestand van de Resource is null");
		}
		
		FileInputStream fis;
		if(!sb.sessionSys.acquireAccess()){
			throw new Exception("Access was denied by the SessionSystem");
		}
		File f = sb.getFile();
		if(!f.exists()){
			try{
				this.createNewFile(null);
			}catch(Exception e){
				Logger.getLogger("Minecraft").warning("[KKP] Kon geen nieuwe DataField maken:");
				e.printStackTrace();
			}
			sb = this.res.getResource();
			if(sb == null)throw new Exception("De SecureBestand van de Resource is null");
			
			if(!sb.sessionSys.acquireAccess()){
				throw new Exception("Access was denied by the SessionSystem");
			}
			f = sb.getFile();
		}
		if(!f.exists()){
			sb.sessionSys.releaseAccess();
			throw new Exception("De resource file bestond zelfs niet na saveDefault()");
		}
		
		fis = new FileInputStream(f);
		
		try{
			fis.skip(16);
			
			{
				byte[] options = new byte[16];
				fis.read(options);
			}
			
			{
				byte[] start = new byte[8];
				fis.read(start);
				
				this.startTime =
						(((long)start[0] & 0xFF) << 56) |
						(((long)start[1] & 0xFF) << 48) |
						(((long)start[2] & 0xFF) << 40) |
						(((long)start[3] & 0xFF) << 32) |
						(((long)start[4] & 0xFF) << 24) |
						(((long)start[5] & 0xFF) << 16) |
						(((long)start[6] & 0xFF) <<  8) |
						(((long)start[7] & 0xFF)      );
			}
			
			{
				byte[] stop = new byte[8];
				fis.read(stop);
				
				this.endTime =
						(((long)stop[0] & 0xFF) << 56) |
						(((long)stop[1] & 0xFF) << 48) |
						(((long)stop[2] & 0xFF) << 40) |
						(((long)stop[3] & 0xFF) << 32) |
						(((long)stop[4] & 0xFF) << 24) |
						(((long)stop[5] & 0xFF) << 16) |
						(((long)stop[6] & 0xFF) <<  8) |
						(((long)stop[7] & 0xFF)      );
			}
			
			{
				byte[] descrLength = new byte[2];
				fis.read(descrLength);
				
				this.descriptionBytes = new byte[(((int)descrLength[0] & 0xFF) << 8 | ((int)descrLength[1] & 0xFF))];
				fis.read(this.descriptionBytes);
				
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				b.write(this.descriptionBytes);
				try {
					this.description = b.toString("UTF8");
				} catch (UnsupportedEncodingException e) {
					sb.sessionSys.releaseAccess();
					throw new Exception("[KartoffelKanaalPlugin] De Encoding \"UTF8\" werd niet herkend bij de static method loadFromBytes bij een PNTechTextProvRaw");
				}
			}
			
		}catch(Exception e){
			try{
				fis.close();
			}catch(Exception ex){}
			sb.sessionSys.releaseAccess();
			throw new Exception("De Totale Header kon niet gelezen worden", e);
		}
		
		try{
			fis.close();
		}catch(Exception e){}
		sb.sessionSys.releaseAccess();
	}
	
	protected byte[] getSavedBytes(short kartoffelID) throws Exception{
		if(this.res == null)throw new Exception("Res is null");
		SecureBestand sb = this.res.getResource();
		if(sb == null){
			this.createNewFile("");
			sb = this.res.getResource();
		}
		
		if(sb == null){
			throw new Exception("Het SecureBestand van de Resource is null");
		}
		
		FileInputStream fis;
		if(!sb.sessionSys.acquireAccess()){
			throw new Exception("Access was denied by the SessionSystem");
		}
		File f = sb.getFile();
		if(!f.exists()){
			this.createNewFile("");
			sb = this.res.getResource();
			if(sb == null)throw new Exception("De SecureBestand van de Resource is null");
			
			if(!sb.sessionSys.acquireAccess()){
				throw new Exception("Access was denied by the SessionSystem");
			}
			f = sb.getFile();
		}
		if(!f.exists()){
			sb.sessionSys.releaseAccess();
			throw new Exception("De resource file bestond zelfs niet na saveDefault()");
		}
		fis = new FileInputStream(f);
		
		byte entrySize = 0;
		byte[] ans;
		try{
			fis.skip(16);
			
			{
				byte[] options = new byte[16];
				fis.read(options);
				entrySize = options[0];
			}
			
			fis.skip(16);
			
			byte[] descrLength = new byte[2];
			fis.read(descrLength);
				
			fis.skip((((((int)descrLength[0]) & 0xFF) << 8) | (((int)descrLength[1]) & 0xFF)));
			
			ans = new byte[entrySize];
			if(kartoffelID >= ((fis.available() - entrySize) / entrySize)){
				fis.read(ans);
			}else{
				fis.skip(entrySize);
				fis.skip(entrySize * kartoffelID);
				fis.read(ans);
			}
		}catch(Exception e){
			try{
				fis.close();
			}catch(Exception ex){}
			sb.sessionSys.releaseAccess();
			throw new Exception("De data kon niet worden gelezen", e);
		}
		
		try{
			fis.close();
		}catch(Exception e){}
		sb.sessionSys.releaseAccess();
		return ans;
	}
	
	protected void setDescription(String newDescription){
		this.description = newDescription;
		try {
			this.descriptionBytes = this.description.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {}
	}
	
	protected void createNewFile(String descr) throws Exception{
		KartoffelFile kf = this.res;
		if(kf == null)throw new Exception("Res is null");
		int fEdition = this.res.getNewFileVersion();
		File f = kf.acquireWriteFile(fEdition, "Default");
		
		try{
			if(f.exists())f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();
		}catch(Exception e){
			throw new Exception("Kon de file-setup niet in orde maken", e);
		}
		
		FileOutputStream fos;
		
		try{
			fos = new FileOutputStream(f);
		}catch(Exception e){
			throw new Exception("Kon de FileOutputStream niet openen", e);
		}
		
		{
			byte[] header = new byte[16];
			header[0] = VersionA;
			header[1] = VersionB;
			
			header[2] = (byte) ((fEdition >>> 24) & 0xFF);
			header[3] = (byte) ((fEdition >>> 16) & 0xFF);
			header[4] = (byte) ((fEdition >>>  8) & 0xFF);
			header[5] = (byte) ((fEdition       ) & 0xFF);		
			fos.write(header);
		}
		
		{
			byte[] options = new byte[16];
			options[0] = this.correctEntrySize;
			fos.write(options);
		}
		
		{
			byte[] time = new byte[8];
			long t = System.currentTimeMillis();
			time[0] = (byte) ((t >>> 56) & 0xFF);
			time[1] = (byte) ((t >>> 48) & 0xFF); 
			time[2] = (byte) ((t >>> 40) & 0xFF); 
			time[3] = (byte) ((t >>> 32) & 0xFF); 
			time[4] = (byte) ((t >>> 24) & 0xFF); 
			time[5] = (byte) ((t >>> 16) & 0xFF); 
			time[6] = (byte) ((t >>>  8) & 0xFF); 
			time[7] = (byte) ( t         & 0xFF); 
			
			fos.write(time);//StartTime
			fos.write(time);//LaatsteVeranderingTime
		}
		
		{
			byte[] descrBytes = (descr == null)?this.descriptionBytes:descr.getBytes("UTF8");
			if(descrBytes == null)descrBytes = new byte[0];
			{
				byte[] l = new byte[2];
				l[0] = (byte) ((descrBytes.length >>> 8) & 0xFF);
				l[1] = (byte) ( descrBytes.length        & 0xFF);
				fos.write(l);
			}
			
			fos.write(descrBytes);
		}
		
		fos.write(defaultEntry);
		
		try{
			fos.close();
		}catch(Exception e){}
		
		this.res.writeFileFinished(fEdition);
	}
	
	public void SaveBlocking(){
		this.sortEntries();
		this.changed = false;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		SecureBestand readSB = null;
		try{
			if(this.res == null)throw new Exception("Res is null");
			
			//ReadFile in orde maken
			byte readEntrySize;
			readSB = this.res.getResource();
			if(readSB == null){
				this.createNewFile("");
				readSB = this.res.getResource();
			}
			
			if(readSB == null){
				throw new Exception("Het SecureBestand van de Resource is null");
			}
			
			if(!readSB.sessionSys.acquireAccess()){
				throw new Exception("Access was denied by the SessionSystem");
			}
			File readFile = readSB.getFile();
			if(!readFile.exists()){
				this.createNewFile("");
				readSB = this.res.getResource();
				if(this.res == null)throw new Exception("De SecureBestand van de Resource is null");
				
				if(!readSB.sessionSys.acquireAccess()){
					throw new Exception("Access was denied by the SessionSystem");
				}
				readFile = readSB.getFile();
			}
			if(!readFile.exists()){
				readSB.sessionSys.releaseAccess();
				throw new Exception("De Resource-file bestond zelfs niet na saveDefault()");
			}
			fis = new FileInputStream(readFile);
			byte[] time = new byte[8];
			try{
				fis.skip(16);
				
				{
					byte[] options = new byte[16];
					fis.read(options);
					readEntrySize = options[0];
				}
				
				fis.read(time);
				fis.skip(8);
				
				{
					byte[] descrLength = new byte[2];
					fis.read(descrLength);
					
					fis.skip(((((int)descrLength[0]) & 0xFF) << 8) | ((int)descrLength[1] & 0xFF));	
				}
				
				fis.skip(readEntrySize);			
			}catch(Exception e){
				try{
					fis.close();
				}catch(Exception ex){}
				readSB.sessionSys.releaseAccess();
				throw new Exception("De Totale Header kon niet gelezen worden", e);
			}
			
			
			//WriteFile in orde maken
			if(this.res == null){
				try{
					fis.close();
				}catch(Exception e){}
				readSB.sessionSys.releaseAccess();
				throw new Exception("Res is null");
			}
			int fEdition = this.res.getNewFileVersion();
			File writeFile = this.res.acquireWriteFile(fEdition, "");
			
			try{
				if(writeFile.exists())writeFile.delete();
				writeFile.getParentFile().mkdirs();
				writeFile.createNewFile();
			}catch(Exception e){
				try{
					fis.close();
				}catch(Exception ex){}
				readSB.sessionSys.releaseAccess();
				throw new Exception("Kon de file-setup niet in orde maken", e);
			}
			
			try{
				fos = new FileOutputStream(writeFile);
			}catch(Exception e){
				try{
					fis.close();
				}catch(Exception ex){}
				readSB.sessionSys.releaseAccess();
				throw new Exception("Kon de FileOutputStream niet openen", e);
			}
			
			{
				byte[] header = new byte[16];
				header[0] = VersionA;
				header[1] = VersionB;
				
				header[2] = (byte) ((fEdition >>> 24) & 0xFF);
				header[3] = (byte) ((fEdition >>> 16) & 0xFF);
				header[4] = (byte) ((fEdition >>>  8) & 0xFF);
				header[5] = (byte) ((fEdition       ) & 0xFF);		
				fos.write(header);
			}
			
			{
				byte[] options = new byte[16];
				options[0] = this.correctEntrySize;
				fos.write(options);
			}
			
			{
				fos.write(time);//StartTime
				
				long t = System.currentTimeMillis();
				
				time[0] = (byte) ((t >>> 56) & 0xFF);
				time[1] = (byte) ((t >>> 48) & 0xFF); 
				time[2] = (byte) ((t >>> 40) & 0xFF); 
				time[3] = (byte) ((t >>> 32) & 0xFF); 
				time[4] = (byte) ((t >>> 24) & 0xFF); 
				time[5] = (byte) ((t >>> 16) & 0xFF); 
				time[6] = (byte) ((t >>>  8) & 0xFF); 
				time[7] = (byte) ( t         & 0xFF); 
				
				fos.write(time);//LaatsteVeranderingTime
				
				this.endTime = t;
			}
			
			if(this.descriptionBytes == null)this.descriptionBytes = new byte[0];
			
			{
				byte[] l = new byte[2];
				l[0] = (byte) ((this.descriptionBytes.length >>> 8) & 0xFF);
				l[1] = (byte) ( this.descriptionBytes.length        & 0xFF);
				fos.write(l);
			}
			
			fos.write(this.descriptionBytes);
			
			fos.write(this.defaultEntry);
			
			int a = 0;
			int p = -1;
			boolean numbStruct = this.isNumberStructure();
			
			if(readEntrySize == this.correctEntrySize){
				//System.out.println("DataField normaal bewaren...");
				for(int i = 0; i < this.kartoffelIDs.length; i++){
					//System.out.println("Data op index " + i + " is " + this.kartoffelIDs[i]);
					if(this.kartoffelIDs[i] > p && this.kartoffelIDs[i] < 400){
						a = (((this.kartoffelIDs[i] - p) - 1) * this.correctEntrySize);
						//System.out.println("De Data op index " + i + " zal dus geschreven worden. Aantal bytes die er geschreven moeten worden om bij het correcte punt te komen: " + a);
						{
							byte[] buffer = new byte[256];
							while(fis.available() > 255 && a > 255){
								fis.read(buffer);
								fos.write(buffer);
								a -= 256;
							}
							
							buffer = new byte[64];
							while(fis.available() > 63 && a > 63){
								fis.read(buffer);
								fos.write(buffer);
								a -= 64;
							}
							
							buffer = null;
							while(fis.available() > 0 && a > 0){
								fos.write(fis.read());
								a--;
							}
							
							while(a > 0){
								fos.write(this.defaultEntry);
								a -= this.defaultEntry.length;
							}
						}
						//System.out.println("Punt bereikt");
						
						byte[] v = this.getEntryData(i);
						v = fitEntry(v, this.correctEntrySize, numbStruct);
						//System.out.println("De nieuwe Data wordt geschreven en is " + v.length + " bytes lang");
						fos.write(v);
						fis.skip(readEntrySize);
						p = this.kartoffelIDs[i];
					}
				}
				
				byte[] buffer = new byte[256];
				while(fis.available() > 255){
					fis.read(buffer);
					fos.write(buffer);
				}
				
				buffer = new byte[64];
				while(fis.available() > 63){
					fis.read(buffer);
					fos.write(buffer);
				}
				
				buffer = new byte[1];
				while(fis.available() > 0){
					fis.read(buffer);
					fos.write(buffer);
				}
				
			}else{
				Logger.getLogger("Minecraft").warning("[KKP] Er worden DataFields geschreven met een andere entryGrootte dan de lees-DataField!");
				for(int i = 0; i < this.kartoffelIDs.length; i++){
					if(this.kartoffelIDs[i] > p){
						{
							byte[] buffer = new byte[readEntrySize];
							while(p < this.kartoffelIDs[i]){
								fis.read(buffer);
								fos.write(fitEntry(buffer, this.correctEntrySize, numbStruct));
								p++;
							}
							
							while(a > 0){
								fos.write(this.defaultEntry);
								a -= this.defaultEntry.length;
							}
						}
						
						byte[] v = this.getEntryData(i);
						v = fitEntry(v, this.correctEntrySize, numbStruct);
						fos.write(v);
						p = this.kartoffelIDs[i];
					}
				}
				
				while(fis.available() >= readEntrySize){
					byte[] buffer = new byte[readEntrySize];
					fis.read(buffer);
					fos.write(fitEntry(buffer, this.correctEntrySize, numbStruct));
				}
				
				readSB.markForBackup();
			}
			
			try{
				fis.close();
			}catch(Exception e){}
			readSB.sessionSys.releaseAccess();
			
			try{
				fos.close();
			}catch(Exception e){}
			
			this.res.writeFileFinished(fEdition);
		}catch(Exception e){
			try{
				fis.close();
			}catch(Exception ex){}
			try{
				readSB.sessionSys.releaseAccess();
			}catch(Exception ex){}
			
			try{
				fos.close();
			}catch(Exception ex){}
			
			this.changed = true;
			Logger.getLogger("Minecraft").warning("[KKP] Een DataField kon niet bewaard worden:");
			e.printStackTrace();
		}
	}
	
	protected void sortEntries(){
		if(this.kartoffelIDs == null || this.kartoffelIDs.length == 0);
		boolean changed = true;
		while(changed){
			changed = false;
			for(int i = 0; i < this.kartoffelIDs.length - 1; i++){
				if(this.kartoffelIDs[i + 1] < this.kartoffelIDs[i]){
					short id = this.kartoffelIDs[i];
					this.kartoffelIDs[i] = this.kartoffelIDs[i + 1];
					this.kartoffelIDs[i + 1] = id;
					this._swapData(i, i + 1);
					changed = true;
				}
			}
		}
	}
	
	protected int getEntryIndex(short id){
		for(int i = 0; i < this.kartoffelIDs.length; i++){
			if(this.kartoffelIDs[i] == id)return i;
		}
		return this._loadSavedEntry(id);
	}
	
	private int _loadSavedEntry(short id){
		byte[] value;
		try {
			value = this.getSavedBytes(id);
		} catch (Exception e) {
			Logger.getLogger("Minecraft").warning("[KKP] Kon een EntryData (van KartoffelID " + id + ") niet inladen van een datafield:");
			e.printStackTrace();
			return -1;
		}
		return this._loadEntry(id, value);
	}
	
	private int _loadEntry(short id, byte[] value){
		if(id == 0x7FFF)return -1;
		//this.cleanUpOld();
		for(int i = 0; i < this.kartoffelIDs.length; i++){
			if(this.kartoffelIDs[i] == 0x7FFF){
				this._loadEntry(id, i, value);
				return i;
			}
		}
		Logger.getLogger("Minecraft").warning("[KKP] Een Entry bij een DataField kon niet worden geladen, omdat de kartoffelID-slots vol zijn");
		return -1;
	}
	
	private void _loadEntry(short id, int entryIndex, byte[] value){
		if(entryIndex < 0 || entryIndex >= this.kartoffelIDs.length)return;
		this._unloadEntry(entryIndex);
		this._setValue(value, entryIndex);
		this.kartoffelIDs[entryIndex] = id;
		this.markRecent(id);
	}
	
	private void _unloadEntry(int entryIndex){
		if(entryIndex < 0 || entryIndex >= this.kartoffelIDs.length)return;
		this.kartoffelIDs[entryIndex] = 0x7FFF;
		this._setValue(this.defaultEntry.clone(), entryIndex);
	}
	
	protected void markRecent(short kartoffelID){
		if(kartoffelID < 0 || kartoffelID == 0x7FFF)return;
		if(this.recentKartoffelIDs[0] == kartoffelID)return;
		int i;
		for(i = 1; i < this.recentKartoffelIDs.length - 1; i++){
			if(this.recentKartoffelIDs[i] == kartoffelID)break;
		}
		
		for(; i >= 1/*de 1 klopt*/; i--){
			this.recentKartoffelIDs[i] = this.recentKartoffelIDs[i - 1];
		}
		this.recentKartoffelIDs[0] = kartoffelID;
	}
	
	protected void cleanUpOld(){
		if(this.changed)this.SaveBlocking();
		for(int i = 0; i < this.kartoffelIDs.length; i++){
			if(this.kartoffelIDs[i] != 0x7FFF){
				boolean unload = true;
				if(this.kartoffelIDs[i] < 400){
					for(int r = 0; r < this.recentKartoffelIDs.length; r++){
						if(this.kartoffelIDs[i] == this.recentKartoffelIDs[i]){
							unload = false;
							break;
						}
					}
				}
				if(unload){
					this._unloadEntry(i);
				}
			}
		}
	}
	
	protected void notifyChange(){
		this.changed = true;
	}
	
	protected abstract void _swapData(int dataLoc1, int dataLoc2);
	protected abstract void _setValue(byte[] v, int entryIndex);
	
	protected abstract boolean isNumberStructure();
	
	public static byte[] fitEntry(byte[] e, byte entrySize, boolean isNumberStructure){
		if(e == null)return new byte[entrySize];
		if(e.length == entrySize)return e;
		
		if(e.length == 0){
			e = new byte[entrySize];
		}else if(e.length < entrySize){
			byte[] entry = new byte[entrySize];
			if(isNumberStructure){
				System.arraycopy(e, 0, entry, entrySize - e.length, e.length);
			}else{
				System.arraycopy(e, 0, entry, 0, e.length);
			}
			e = entry;
		}else if(e.length > entrySize){
			byte[] entry = new byte[entrySize];
			if(isNumberStructure){
				boolean maxCountBack = false;
				for(int i = entry.length; i > e.length; i++){
					if(e[i] != 0x00){
						maxCountBack = true;
						break;
					}
				}
				
				if(maxCountBack){
					entry[0] = 0x7F;
					for(int i = 1; i < entry.length; i++){
						entry[i] = (byte) 0xFF;
					}
				}else{
					System.arraycopy(e, 0, e.length - entry.length, 0, entry.length);
				}
			}else{
				System.arraycopy(e, 0, entry, 0, entry.length);
			}
			e = entry;
		}
		
		return e;
	}
	
	public abstract byte[] getEntryData(int i);
	
	public long getStartTime(){
		return this.startTime;
	}
	
	public long getLastEditTime(){
		return this.endTime;
	}
}
