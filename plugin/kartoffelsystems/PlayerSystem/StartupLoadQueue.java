package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import org.bukkit.entity.Player;

public class StartupLoadQueue implements Runnable{
	protected Player[] players;
	protected PlayerManager pm;
	
	public StartupLoadQueue(Player[] online, PlayerManager pm){
		players = online;
		this.pm = pm;
	}
	
	@Override
	public void run(){
		if(players == null || pm == null || players.length == 0)return;
		for(int i = 0; i < players.length; ++i){
			try{
				pm._loadPersonOnlinePrivate(players[i], true);
			}catch(Exception e){}
		}
	}
}
