package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import KartoffelKanaalPlugin.plugin.StoreTechnics;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

public class PulserFileSaver implements Runnable{
	private Pulser parent;
	private Thread saveThread;
	
	protected PulserFileSaver(Pulser parent){
		this.parent = parent;
	}
	
	@Override
	public void run(){
		this.SaveBlocking();
	}
	
	protected void Save(){
		if(this.parent == null)return;
		Thread a = new Thread(this);
		a.start();
	}
	
	protected void SaveBlocking(){
		if(this.parent == null || this.parent.res == null)return;
		if(this.saveThread != null && this.saveThread.isAlive())return;
		long time = System.currentTimeMillis();//Tijd wordt vroeg opgeschreven om zeker te zijn
		this.saveThread = Thread.currentThread();
		int edition = this.parent.res.getNewFileVersion();
		File f;
		try{
			f = this.parent.res.acquireWriteFile(edition, "");
			f.createNewFile();
			f.mkdirs();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] PulserFileSaver: Kon nieuw bestand voor bewaren niet aanmaken (" + (e == null?"null":e) + ")");
			return;
		}
		
		FileOutputStream fos;
		try{
			fos = new FileOutputStream(f);
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] PulserFileSaver: Kan FileOutputStream niet openen voor Pulser file");
			return;
		}
		try{
			{
				byte[] header = this.parent.header;
				if(header == null || header.length != 16){
					header = new byte[16];
				}
				header[0] = Pulser.VersionA;
				header[1] = Pulser.VersionB;
			
				header[2] = (byte) ((edition >>> 24) & 0xFF);
				header[3] = (byte) ((edition >>> 16) & 0xFF);
				header[4] = (byte) ((edition >>>  8) & 0xFF);
				header[5] = (byte) ( edition         & 0xFF);
				fos.write(header);
			}
			
			PulserNotif[] notifications = this.parent.notifications;
			//System.out.println("Notifications.length = " + (notifications == null?"notifications zijn null":notifications.length));
			{
				byte[][] data = new byte[notifications.length][];
				for(int i = 0; i < notifications.length; i++){
					if(notifications[i] == null)continue;
					data[i] = notifications[i].saveNotif();
					//System.out.println("Data verzameld van notification " + i + ". Length = " + (data == null?"null":data[i].length));
					if(data[i] != null && data[i].length > 20000)data[i] = new byte[0];
				}
				
				fos.write(StoreTechnics.saveArray(data, 1000));
			}
			
			this.parent.res.writeFileFinished(edition);
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon de PulserFile niet bewaren (" + (e == null?"null":e) + ")");
			e.printStackTrace();
		}
		
		try{
			fos.close();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon een FileOutputStream bij de PulserFileSaver niet sluiten");
		}
		
		if(this.parent.lastSaveTime < time)this.parent.lastSaveTime = time;
		
		//System.out.println("PulserBestand bewaard");
	}
}

