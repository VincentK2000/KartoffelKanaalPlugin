package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

public class ThreadChecker{}

//package me.vincentk.kartoffelkanaalplugin;

//import java.util.logging.Logger;

/*public final class ThreadChecker implements Runnable{
	private Thread active;
	private SaveQueue p;
	private PersonSaveFormat psf;

	
	public ThreadChecker(final SaveQueue parent, final PersonSaveFormat psf) throws Exception{
		if(parent == null || psf == null)throw new Exception("Invalid parameters");
		this.p = parent;
		this.psf = psf;
	}
	
	@Override
	public void run(){
		if(psf == null)return;
		active = new Thread(psf);
		Logger l = Logger.getLogger("Minecraft");
		if(l == null){
			l = Logger.getAnonymousLogger();
		}
		
		active.start();
		try {
			Thread.sleep(5000);
			l.warning("[KartoffelKanaalPlugin] Persoon \"" + String.valueOf(psf.name) + "\" bewaren duurt al 5 seconden..." );
			Thread.sleep(3000);
			l.warning("[KartoffelKanaalPlugin] Persoon \"" + String.valueOf(psf.name) + "\" bewaren duurt al 8 seconden...");
			Thread.sleep(2000);
			l.warning("[KartoffelKanaalPlugin] Persoon \"" + String.valueOf(psf.name) + "\" bewaren duurt al 10 seconden...");
			l.warning("[KartoffelKanaalPlugin] Nog 5 seconden left om persoon \"" + String.valueOf(psf.name) + "\" met UUID = " + psf.id.toString() + " te bewaren");
			Thread.sleep(5000);
			l.warning("[KartoffelKanaalPlugin] Kon de persoon \"" + String.valueOf(psf.name) + "\" met UUID = " + psf.id.toString() + " niet bewaren binnen 15 seconden. Process wordt beëindigt");
			active.interrupt();
			this.error(new Exception("Kon de persoon niet bewaren binnen de 15 seconden"));
		} catch (InterruptedException e) {}
		
	}
	
	protected void stop(){
		if(active != null && active.isAlive())active.interrupt();
	}
	public void error(Throwable e){
		if(psf == null)return;
		byte[] data = psf.data;
		psf.triesLeft--;
		/*String n;
		{
			byte[] name = new byte[16];
			System.arraycopy(data, 16, name, 0, 16);
			n = PlayerManager.NamefromBytes(name);
		}
		UUID id;
		{
			Long UUIDMost = (long)(data[0] << 56 | data[1] << 48 | data[2] << 40 |
					data[3] << 32 | data[4] << 24 | data[5] << 16 | data[6] << 8 | data[7]);
			Long UUIDLeast = (long)(data[8] << 56 | data[9] << 48 | data[10] << 40 |
					data[11] << 32 | data[12] << 24 | data[13] << 16 | data[14] << 8 | data[15]);
			id = new UUID(UUIDMost,UUIDLeast);
		}*//*
		if(this.p != null){
			System.out.println("[KartoffelKanaalPlugin] Failed on saving person \"" + String.valueOf(psf.name) + "\", UUID = " + psf.id + " | Tries left: " + psf.triesLeft);
			if(e != null)System.out.println("[KartoffelKanaalPlugin] Reason: " + String.valueOf(e.getMessage()));
			if(psf.triesLeft > 0){
				p.add(psf);
			}
			p.next(this);
		}else{
			System.out.println("[KartoffelKanaalPlugin] Failed on saving person \"" + String.valueOf(psf.name) + "\", UUID = " + psf.id.toString());
			if(e != null)System.out.println("[KartoffelKanaalPlugin] Reason: " + String.valueOf(e.getMessage()));
		}
	}
}*/
