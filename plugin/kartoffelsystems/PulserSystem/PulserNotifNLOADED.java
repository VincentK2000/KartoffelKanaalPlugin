package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import KartoffelKanaalPlugin.plugin.AttribSystem;
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

	protected byte[] saveNotif(){
		return data;
	}

	@Override
	protected void processTick(Person[] online, boolean[] receivers, int tick) {}

	@Override
	protected void sendMessage(Person[] p, boolean[] receivers) {}

	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		return false;
	}

	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		return new String[0];
	}

	@Override
	public boolean activationRequiresCrashTest() {
		return false;
	}

	@Override
	public void doCrashTest(Player pl) throws Exception{
		pl.sendMessage("§4Geen crash-test beschikbaar voor niet geladen notifications");
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s) throws Exception {
		return new ArrayList<String>(0);
	}

}
