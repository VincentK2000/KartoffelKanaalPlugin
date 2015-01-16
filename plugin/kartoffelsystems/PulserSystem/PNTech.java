package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class PNTech implements IObjectCommandHandable{
	protected PulserNotifStandard notificationBase;
	
	private boolean _invisible = true;
	protected int ID = -1;
	
	protected PNTech(boolean invisible, int ID, PulserNotifStandard notificationBase){
		this.notificationBase = notificationBase;
		this._invisible = invisible;
		this.ID = ID;
	}
	protected PNTech(byte[] src){
		if(src == null || src.length < 5)return;
		this._invisible = (src[0] & 0x80) == 0x80;
		this.ID = src[1] << 24 | src[2] << 16 | src[3] << 8 | src[4];
	}
	public int getID(){
		return this.ID;
	}
	public boolean isInvisible(){
		return this._invisible;
	}
	protected void setInvisible(boolean newValue) throws Exception{
		this.checkDenyChanges();
		this._invisible = newValue;
		this.notifyChange();
		if(this.notificationBase != null)this.notificationBase.recheckPrimaryTechnics();
	}
	public abstract String getTypeName();
	public abstract byte getTechType();
	public abstract int getEstimatedSize();
	protected static PNTech loadFromBytes(byte[] src){
		//System.out.println("        PNTech.loadFromBytes: PNTech laden...");
		if(src == null || src.length < PNTech.generalInfoLength()){
			//System.out.println("        PNTech.loadFromBytes: De src is null of de length ervan is kleiner dan de generalInfoLength");
			return new PNTechNLOADED(src);
		}
		byte t = (byte) (src[0] & 0x7F);
		PNTech a = null;
		//System.out.println("        PNTech.loadFromBytes: src.length = " + src.length);
		//System.out.println("        PNTech.loadFromBytes: TechType is " + t);
		if(t == 1){
			a = PNTechTextProv.loadFromBytes(src);
		}else if(t == 2){ 
			a =  PNTechCondition.loadFromBytes(src);
		}else if(t == 3){
			a =  PNTechDataFieldConn.loadFromBytes(src);
		}else if(t == 4){
			a =  PNTechNotifSize.loadFromBytes(src);
		}else if(t == 5){
			a =  PNTechSpecEditAccess.loadFromBytes(src);
		}
		if(a == null){
			a = new PNTechNLOADED(src);
		}
		//System.out.println("        PNTech.loadFromBytes: PNTech geladen...");
		return a;
	}
	protected abstract byte[] saveTech();
	protected static int generalInfoLength(){return 5;}
	
	protected boolean saveGeneralInfo(byte[] ans){
		if(ans == null || ans.length < 5)return false;
		
		ans[0] = this.getTechType();
		if(this._invisible){
			ans[0] |= 0x80;
		}else{
			ans[0] &= 0x7F;
		}
		
		ans[1] = (byte) ((this.ID >>> 24) & 0xFF);
		ans[2] = (byte) ((this.ID >>> 16) & 0xFF);
		ans[3] = (byte) ((this.ID >>>  8) & 0xFF);
		ans[4] = (byte) ( this.ID         & 0xFF);
		return true;
	}

	/*	public static String getTechName(byte techID){
		switch(techID){
			case 1: 
				return "TextProvider";
			case 2:
				return "Condition";
			case 3:
				return "DataFieldConn";
			case 4:
				return "NotifSize";
			case 5:
				return "SpecEditAccess";
			case 0:
				return "!!!PNTechNLOADED!!!";
		}
		return "Onbekend:" + techID;
	}*/
	
	public static PNTech createFromParams(String[] params, int ID, PulserNotif notificationBase) throws Exception {
		if(params == null)throw new Exception("De parameters zijn null");
		if(params.length == 0){
			throw new Exception("De eerste creatie parameter moet het PNTech-type zijn. Die kan zijn: \"TextProvider\",\"Condition\",\"DataFieldConn\",\"NotifSize\",\"SpecEditAccess\"");
		}
		String[] techSpecificParams = new String[params.length - 1];
		System.arraycopy(params, 1, techSpecificParams, 0, techSpecificParams.length);
		
		String techType = params[0].toLowerCase();
		if(techType.equals("textprovider") || techType.equals("1")){
			return PNTechTextProv.createFromParams(techSpecificParams, ID, notificationBase);
		}else if(techType.equals("condition") || techType.equals("2")){
			return PNTechCondition.createFromParams(techSpecificParams, ID, notificationBase);
		}else if(techType.equals("datafieldconn") || techType.equals("3")){
			return PNTechDataFieldConn.createFromParams(techSpecificParams, ID, notificationBase);
		}else if(techType.equals("notifsize") || techType.equals("4")){
			return PNTechNotifSize.createFromParams(techSpecificParams, ID, notificationBase);
		}else if(techType.equals("speceditaccess") || techType.equals("5")){
			return PNTechSpecEditAccess.createFromParams(techSpecificParams, ID, notificationBase);
		}else{
			throw new Exception("Onbekend PNTech-type");
		}
	}
	public abstract PNTech createCopy(int ID, PulserNotifStandard notificationBase) throws Exception;
	
	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		String label = args[0];
		
		if(label.equals("visibility")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("§4Je hebt geen toegang tot dit commando");
			}
			if(args.length < 2){
				a.sendMessage("§eDe Technic is " + (this.isInvisible()?"§4invisible":"§2visible"));
			}else{
				args[1] = args[1].toLowerCase();
				boolean invisibilityValue;
				if(args[1].equals("visible") || args[1].equals("on") || args[1].equals("+")){
					invisibilityValue = false;
				}else if(args[1].equals("invisible") || args[1].equals("invis") || args[1].equals("off") || args[1].equals("-")){
					invisibilityValue = true;
				}else{
					a.sendMessage("§4Mogelijke nieuwe waarden voor de staat zijn: §2visible, aan, +§f of §4invisible, off, -");
					return true;
				}
				this.setInvisible(invisibilityValue);
				a.sendMessage("§eDe Technic is nu " + (this.isInvisible()?"§4invisible":"§2visible"));
			}
		}else if(label.equals("id")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			a.sendMessage("§eID = " + this.ID);
		}else if(label.equals("gettype")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			a.sendMessage("§eTechType = " + this.getTechType() + " (" + this.getTypeName() + ")");
		}else if(label.equals("tostring")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			a.sendMessage("§etostring() = " + this.toString());
		}else{
			return false;
		}
		return true;
	}
	//public abstract void handleLocalCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception;
	
	
	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a) throws Exception {
		String label = args[0];
		if(args.length == 1){
			if("visibility".startsWith(label))a.add("visibility");
		}else if(args.length >= 2){
			if(label.equals("visibility")){
				if(args.length == 2){
					if("visible".startsWith(args[1]))a.add("visible");
					if("invisible".startsWith(args[1]))a.add("invisible");
					return a;
				}
			}
		}
		return a;
	}
	
	@Override
	public IObjectCommandHandable getSubObjectCH(String s) throws Exception {
		return null;
	}
	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception {
		return a;
	}
	
	
	
	/*public String getTopLevelPossibilitiesString(){
		ArrayList<String> al = new ArrayList<String>(1);
		try {
			al = this.autoCompleteObjectCommand(new String[]{""}, al);
		} catch (Exception e) {
			return "Fout_bij_zoeken";
		}
		if(al.size() == 0)return "";
		StringBuilder sb = new StringBuilder(20);
		for(int i = 0; i < al.size() - 1; i++){
			String s = al.get(i);
			if(s == null || s.length() == 0)continue;
			sb.append(s);
			sb.append('|');
		}
		String last = al.get(al.size() - 1);
		if(last != null){
			sb.append(last);
		}
		return sb.toString();	
	}*/
	
	/*public String getTopLevelPossibilitiesString(){
		String[] total = this.getTotalTopLevelArgsPossibilities();
		if(total.length == 0)return "";
		StringBuilder sb = new StringBuilder(20);
		for(int i = 0; i < total.length - 1; i++){
			if(total[i] == null || total[i].length() == 0)continue;
			sb.append(total[i]);
			sb.append('|');
		}
		if(total[total.length - 1] != null){
			sb.append(total[total.length - 1]);
		}
		return sb.toString();
	}
	public final String[] getTotalTopLevelArgsPossibilities(){
		String[] general = new String[]{"visibility"};
		String[] local = this.getLocalTopLevelArgsPossibilities();
		if(local == null)local = new String[0];
		
		String[] total = new String[general.length + local.length];
		System.arraycopy(general, 0, total, 0, general.length);
		System.arraycopy(local, 0, total, general.length, local.length);
		return total;
	}
	public abstract String[] getLocalTopLevelArgsPossibilities();*/
	
	public boolean denyChanges(){
		return this.notificationBase != null && this.notificationBase.denyChanges();
	}
	public void checkDenyChanges() throws Exception{
		if(this.denyChanges())throw new Exception("Veranderingen zijn niet toegestaan voor de PulserNotif. Controleer read-only");
	}
	protected void notifyChange(){
		if(this.notificationBase != null)this.notificationBase.changed = true;
	}
	public String toString(){
		return this.ID + " - " + this.getTypeName();
	}
	
/*	public static String getTechName(byte techID){
		switch(techID){
			case 1: 
				return "TextProvider";
			case 2:
				return "Condition";
			case 3:
				return "DataFieldConn";
			case 4:
				return "NotifSize";
			case 5:
				return "SpecEditAccess";
			case 0:
				return "!!!PNTechNLOADED!!!";
		}
		return "Onbekend:" + techID;
	}*/
}
