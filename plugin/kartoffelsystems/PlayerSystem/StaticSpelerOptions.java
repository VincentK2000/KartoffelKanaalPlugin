package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaticSpelerOptions extends SpelerOptions {

	public StaticSpelerOptions(byte[] a) {
		super(a);
	}
	
	@Override
	public void setRank(byte r, SpelerOptions executor, CommandSender a, boolean notifyAffected){
		if(a == null)return;
		a.sendMessage("§4Je kan de rank niet veranderen van deze persoon");
	}
	
	@Override
	public void setDonatorRank(byte r, SpelerOptions executor, CommandSender a, boolean notifyAffected){
		if(a == null)return;
		a.sendMessage("§4Je kan de donateurrank niet veranderen van deze persoon");
	}
	
	@Override
	public void giveDailyDiamonds(Player p){
		if(p == null)return;
		p.sendMessage("§4De SpelerOptions is een StaticSpelerOptions");
	}
	
	@Override
	protected void setOption(byte adress, boolean on, SpelerOptions executor, CommandSender a, boolean notifyAffectedPerson){
		if(a == null)return;
		a.sendMessage("§4Je kan geen options veranderen van deze persoon");
	}
	
	@Override
	public void setPermission(byte adress, boolean on, boolean isStatic, SpelerOptions executor, CommandSender a, boolean notifyAffectedPerson){
		if(a == null)return;
		a.sendMessage("§4Je kan geen permissions veranderen van deze persoon");
	}
	
	@Override
	protected void setLatestDailyDiamondDay(short day){}
	@Override
	public void setRank(byte i){};
	@Override
	public void setStaticValue(byte a, boolean b, boolean c){};
	@Override
	public void setType(byte a, boolean b){};
	@Override
	public void refreshRankRequirments(){}
	@Override
	protected void setSwitchWithoutUpdate(byte adress, boolean on, boolean staticpart){}
	@Override
	protected void setSwitches(int index, byte switches, boolean on){}
	@Override
	public void setPermission(byte adress, boolean value, boolean isStatic, boolean update){}
	@Override
	public void refreshRank(){}
	@Override
	public void refreshPermProperties(){}
	@Override
	protected void setPermissionLevel(byte b) {}
	@Override
	protected void refreshPrefix() {}
	
	@Override
	public boolean isChanged(){return false;}
	
}
