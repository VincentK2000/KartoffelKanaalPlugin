package KartoffelKanaalPlugin.plugin.kartoffelsystems;

public interface ISessionSystemListener {
	public void onAccessReceived(Thread t);
	public void onAccessReleased();
}
