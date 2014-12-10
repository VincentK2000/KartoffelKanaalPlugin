package KartoffelKanaalPlugin.plugin.kartoffelsystems;

public class KartoffelServiceDisabler implements Runnable {
	protected KartoffelService s;
	
	public KartoffelServiceDisabler(KartoffelService s){
		this.s = s;
	}
	
	public void run(){
		if(s == null)return;
		try {
			s._disableCore();
		} catch (Exception e) {
			this.s.DisableCrash(new Exception("Kon de service niet disablen", e));
		}
	}
}
