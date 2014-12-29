package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.SettingsManager;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class PNConditionTimeRanged extends PNCondition{
	//8 byte's: starttime
	//8 byte's: endtime
	protected long starttime = 0;
	protected long endtime = 9223372036854775807l;
	protected Calendar c;
	
	protected PNConditionTimeRanged(long starttime, long endtime, byte options, boolean invisible, int conditionID, PNTechCondition root) {
		super(options, invisible, conditionID, root);
		c = Calendar.getInstance();
		this.starttime = starttime;
		this.endtime = endtime;
	}
	
	protected PNConditionTimeRanged(long starttime, long endtime, byte[] src){
		super(src);
		c = Calendar.getInstance();
		this.starttime = starttime;
		this.endtime = endtime;
	}
	
	@Override
	protected byte getConditionType() {return 7;}

	@Override
	protected boolean calculateValue() {
		long a = c.getTimeInMillis();
		return a >= starttime && a <= endtime;
	}

	protected void setTimeRange(long start, long stop){
		this.starttime = start;
		this.endtime = stop;
	}
	
	protected void resetTimeRange(){
		this.starttime = 0;
		this.endtime = 9223372036854775807l;
	}
	
	public long getStartTime(boolean serverTime){
		return (serverTime || Main.sm == null)?this.starttime:Main.sm.convertToClientTime(this.starttime);
	}
	
	public long getEndTime(boolean serverTime){
		return (serverTime || Main.sm == null)?this.endtime:Main.sm.convertToClientTime(this.endtime);
	}
	
	public void setStartTime(long newTime, boolean isServerTime){
		this.starttime = (isServerTime || Main.sm == null)?newTime:Main.sm.convertToServerTime(newTime);
	}
	
	public void setEndTime(long newTime, boolean isServerTime){
		this.endtime = (isServerTime || Main.sm == null)?newTime:Main.sm.convertToServerTime(newTime);		
	}
	
	protected static PNConditionTimeRanged loadFromBytes(byte[] src){
		if(src == null || src.length != PNCondition.generalInfoLength() + 16)return null;
		int s = PNCondition.generalInfoLength();
		
		//incremention moet na waarde-opvraging aangezien de lengte van de general-info wordt gebruikt
		long start = 
				((long)src[s++] & 0xFF) << 56 | 
				((long)src[s++] & 0xFF) << 48 | 
				((long)src[s++] & 0xFF) << 40 | 
				((long)src[s++] & 0xFF) << 32 | 
				((long)src[s++] & 0xFF) << 24 | 
				((long)src[s++] & 0xFF) << 16 | 
				((long)src[s++] & 0xFF) <<  8 | 
				((long)src[s++] & 0xFF);
		long end =
				((long)src[s++] & 0xFF) << 56 | 
				((long)src[s++] & 0xFF) << 48 | 
				((long)src[s++] & 0xFF) << 40 | 
				((long)src[s++] & 0xFF) << 32 | 
				((long)src[s++] & 0xFF) << 24 | 
				((long)src[s++] & 0xFF) << 16 | 
				((long)src[s++] & 0xFF) <<  8 | 
				((long)src[s++] & 0xFF);
		
		return new PNConditionTimeRanged(start, end, src);
	}

	@Override
	protected byte[] saveCondition() {
		byte[] ans = new byte[16 + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		int s = PNCondition.generalInfoLength();
		ans[s++] = (byte)((starttime >>> 56) & 0xFF);//incremention moet na waarde-opvraging aangezien de lengte van de general-info wordt gebruikt
		ans[s++] = (byte)((starttime >>> 48) & 0xFF);
		ans[s++] = (byte)((starttime >>> 40) & 0xFF);
		ans[s++] = (byte)((starttime >>> 32) & 0xFF);
		ans[s++] = (byte)((starttime >>> 24) & 0xFF);
		ans[s++] = (byte)((starttime >>> 16) & 0xFF);
		ans[s++] = (byte)((starttime >>>  8) & 0xFF);
		ans[s++] = (byte)((starttime       ) & 0xFF);
		
		ans[s++] = (byte)((endtime >>> 56) & 0xFF);
		ans[s++] = (byte)((endtime >>> 48) & 0xFF);
		ans[s++] = (byte)((endtime >>> 40) & 0xFF);
		ans[s++] = (byte)((endtime >>> 32) & 0xFF);
		ans[s++] = (byte)((endtime >>> 24) & 0xFF);
		ans[s++] = (byte)((endtime >>> 16) & 0xFF);
		ans[s++] = (byte)((endtime >>>  8) & 0xFF);
		ans[s  ] = (byte)((endtime       ) & 0xFF);
		
		return ans;
	}
	
	@Override
	protected PNConditionTimeRanged createCopy(int id, PNTechCondition root) {
		return new PNConditionTimeRanged(starttime, endtime, this.options, this.invisible, id, root);
	}

	@Override
	protected int getEstimatedSize() {
		return 16 + PNCondition.generalInfoLength();
	}
	
	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		if(args.length == 0)return false;
		String label = args[0].toLowerCase();
		if(label.equals("starttijd")){
			boolean useRawTime = attribSys.hasAttrib("useRawTime");
			if(args.length == 1){
				long startTime = this.getStartTime(useRawTime);
				a.sendMessage("§eDe starttijd is op " + (new Date(startTime)).toString() + " (" + SettingsManager.getTimeRelation(this.starttime) + ").");
				a.sendMessage("§eVerander het met §cstarttijd <over|absoluut> <...>");
			}else if(args.length > 1){
				String[] newArgs = new String[args.length - 2];
				System.arraycopy(args, 2, newArgs, 0, newArgs.length);
				
				long newStartTime = Main.sm.getServerTimeFromArgs(args[1], a, newArgs, useRawTime, false);
				if(newStartTime < 0)return true;
				this.setStartTime(newStartTime, useRawTime);
				
				long startTime = this.getStartTime(useRawTime);
				a.sendMessage("§eDe starttijd is nu op " + (new Date(startTime)).toString() + " (" + SettingsManager.getTimeRelation(this.starttime) + ").");
			}
			
		}else if(label.equals("stoptijd")){
			boolean useRawTime = attribSys.hasAttrib("useRawTime");
			if(args.length == 1){
				long endTime = this.getEndTime(useRawTime);
				a.sendMessage("§eDe stoptijd is op " + (new Date(endTime)).toString() + " (" + SettingsManager.getTimeRelation(this.endtime) + ").");
				a.sendMessage("§eVerander het met §cstoptijd <over|absoluut> <...>");
			}else if(args.length > 1){
				String[] newArgs = new String[args.length - 2];
				System.arraycopy(args, 2, newArgs, 0, newArgs.length);
				
				long newStopTime = Main.sm.getServerTimeFromArgs(args[1], a, newArgs, useRawTime, true);
				if(newStopTime < 0)return true;
				this.setEndTime(newStopTime, useRawTime);
				
				long endTime = this.getEndTime(useRawTime);
				a.sendMessage("§eDe stoptijd is nu op " + (new Date(endTime)).toString() + " (" + SettingsManager.getTimeRelation(this.endtime) + ").");
			}
		}else if(label.equals("tijden")){
			boolean useRawTime = attribSys.hasAttrib("useRawTime");
			if(args.length == 1){
				String timeRelation;
				if(this.starttime < System.currentTimeMillis()){
					timeRelation = SettingsManager.getTimeRelation(this.starttime);
				}else if(System.currentTimeMillis() > this.endtime){
					timeRelation = SettingsManager.getTimeRelation(this.endtime);
				}else{
					timeRelation = "nu actief";
				}
				long startTime = this.getStartTime(useRawTime);
				long endTime = this.getEndTime(useRawTime);
				a.sendMessage("§eDe condition is true vanaf " + (new Date(startTime)).toString() + " tot " + (new Date(endTime)).toString() + " (" + timeRelation + ").");
			}else{
				a.sendMessage("§4Verander de tijden met §cstarttijd§4 en §cstoptijd§4.");
			}
		}
		return false;
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args) throws Exception {
		ArrayList<String> a = super.autoCompleteObjectCommand(args);
		if(a == null)a = new ArrayList<String>();
		if(args.length == 0)return a;
		
		String label = args[0].toLowerCase();
		if(args.length == 1){
			if("starttijd".startsWith(label))a.add("starttijd");
			if("stoptijd".startsWith(label))a.add("stoptijd");
			if("tijden".startsWith(label))a.add("tijden");
		}
		return a;
	}
	
	@Override
	public PNConditionTimeRanged copyCondition(int ID, PNTechCondition root) throws Exception {
		return new PNConditionTimeRanged(this.starttime, this.endtime, this.options, true, ID, root);
	}
	
	public static PNConditionTimeRanged createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		
		throw new Exception("Functie nog niet beschikbaar");
	}
}
