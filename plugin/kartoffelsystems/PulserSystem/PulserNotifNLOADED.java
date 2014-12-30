package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class PulserNotifNLOADED extends PulserNotif{
	//NLOADED = NOT LOADED
	byte[] data;
	
	protected byte getNotifType(){
		return (byte) 0x80;
	}

	protected PulserNotifNLOADED(byte[] src){
		super(src);
		this.data = (src.length > 500000)?new byte[0]:src;
	}

	@Override
	protected void processTick(Person[] online, boolean[] receivers, int tick) {}

	@Override
	protected void sendMessage(Person[] p, boolean[] receivers) {}

	@Override
	public boolean activationRequiresCrashTest() {
		return false;
	}

	@Override
	public void doCrashTest(Player pl) throws Exception{
		pl.sendMessage("§4Geen crash-test beschikbaar voor niet geladen notifications");
	}

	protected byte[] saveNotif(){
		return data;
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception {
		return a;
	}

}
