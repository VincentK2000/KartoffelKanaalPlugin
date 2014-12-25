package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.Calendar;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
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
		
		return false;
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args) throws Exception {
		ArrayList<String> a = super.autoCompleteObjectCommand(args);
		if(a == null)a = new ArrayList<String>();
		
		return a;
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s) throws Exception {
		return super.autoCompleteSubObjectCH(s);
	}
	
	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PNConditionTimeRanged copyCondition(int ID, PNTechCondition root) throws Exception {
		return new PNConditionTimeRanged(this.starttime, this.endtime, this.options, true, ID, root);
	}
	
	public static PNConditionTimeRanged createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		throw new Exception("Functie nog niet beschikbaar");
	}
}
