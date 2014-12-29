package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import KartoffelKanaalPlugin.plugin.SecureBestand;

public class PulserFileLoader {
	private Pulser parent;
	//2 byte's: version
	//14 byte's: leeg
	
	//... byte's:
		//4 byte's: PulserItem length
			//... byte's: PulserItem data
	
	protected PulserFileLoader(Pulser parent){
		this.parent = parent;
		if(parent == null)throw new IllegalArgumentException("Pulser is null");
	}
	
	protected void loadFile(){
		//System.out.println("Pulser File laden...");
		if(this.parent == null || this.parent.res == null || this.parent.res.getResource() == null)throw new IllegalArgumentException("Pulser of de resource daarvan is null");
		SecureBestand res = this.parent.res.getResource();
		res.sessionSys.acquireAccess();
		
		boolean closed = false;
		File f = null;
		try{
			f = res.getFile();
		}catch(Exception e){
			if(this.parent.res != null && this.parent.res.getResource() != null)this.parent.res.getResource().markForBackup();
			Logger.getLogger("Minecraft").warning("[KKP] Kon PulserFile niet krijgen van de res");
			res.sessionSys.releaseAccess();
			return;
		}
		if(f == null || !f.exists()){
			Logger.getLogger("Minecraft").warning("[KKP] De PulserFile lijkt niet te bestaan...");
			this.parent.notifications = new PulserNotif[]{Pulser.AbonneerNotification, Pulser.DoneerNotification/*, Pulser.TestNotification*/};
			this.parent.notifyChange();
			res.sessionSys.releaseAccess();
			return;
		}
		
		FileInputStream fis;
		try{
			fis = new FileInputStream(f);
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon de InputStream naar het PulserFile niet openen in de PulserFileLoader");
			res.sessionSys.releaseAccess();
			return;
		}
		try{
			if(fis.available() < 16){
				fis.close();
				closed = true;
				throw new Exception("Er was onvoldoende data om de header in te lezen");
			}
			byte[] header = new byte[16];
			fis.read(header);
			parent.header = header;
			if(header[0] != Pulser.VersionA || header[1] != Pulser.VersionB){
				this.parent.writeOnExit = false;
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] De version van de gegeven PulserFile klopt niet. Veranderingen zullen niet worden aangebracht in de PulserFile");
				Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Aangezien het PulserFile niet geladen kon worden, zullen de geladen Notifications niet veranderd worden waardoor de Pulser de oude Notifications in acht zal blijven nemen");
				fis.close();
				closed = true;
				throw new Exception("De versie is niet ondersteund");
			}
			
			
			ArrayList<PulserNotif> notifs = new ArrayList<PulserNotif>();
			
			while(fis.available() >= 4){
				int a = ((int)fis.read() & 0xFF) << 24 | ((int)fis.read() & 0xFF) << 16 | ((int)fis.read() & 0xFF) << 8 | ((int)fis.read() & 0xFF);
				//System.out.println("Pulser File: Notification inladen van " + a + " bytes...");
				if(fis.available() < a){
					//System.out.println("Pulser File: Onvoldoende bytes available");
					break;
				}
				if(a > 20000){
					fis.skip(a);
					Logger.getLogger("Minecraft").warning("[KKP] Een PulserNotification wordt niet geladen omdat die groter dan >20000 bytes (20KB) is");
					continue;
				}
				byte[] src = new byte[a];
				fis.read(src);
				
				notifs.add(PulserNotif.loadFromBytes(src));
			}
			PulserNotif[] abc = new PulserNotif[notifs.size()];
			notifs.toArray(abc);
			this.parent.notifications = abc;
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon het Pulser-bestand niet inladen: " + e);
		}


		try{
			fis.close();
		}catch(Exception e){
			if(!closed){
				Logger.getLogger("Minecraft").warning("[KKP] Kon de FileInputStream niet sluiten, hierdoor kan mogelijk corruptie voorkomen");
			}
		}
		res.sessionSys.releaseAccess();
		//System.out.println("Pulser File geladen");
	}
}
