package KartoffelKanaalPlugin.plugin.kartoffelsystems;

public class KartoffelServiceEnabler implements Runnable {
	protected KartoffelService s;
	
	public KartoffelServiceEnabler(KartoffelService s){
		this.s = s;
	}
	
	public void run(){
		if(s == null)return;
		try {
			s._enableCore();
		} catch (Exception e) {
			this.s.DisableCrash(new Exception("Kon de service niet enablen", e));
		}
	}
}
