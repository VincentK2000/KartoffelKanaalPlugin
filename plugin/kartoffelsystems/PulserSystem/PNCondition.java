package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public abstract class PNCondition implements IObjectCommandHandable {
	// DEPRECATED: Dit zorgt misschien wel voor sneller bewaren, maar trager voor laden en kan soms verwarrend zijn//4 byte's: vrij houden, is nl. gebruikt bij PulserNoticationExtended om de length aan te duiden
	
	//1 byte: Type
	//1 byte: Special Data
	//        1 bit: ~ leeg ~
	//        1 bit: closed (de value wordt niet berekend en de default value wordt gebruikt)
	//        1 bit: default value
	//        1 bit: last value
	//        4 bits: leeg
	//4 bytes: ConditionID
	//<...> byte's: Customized Condition Data
	protected byte options;
	protected boolean invisible;
	protected int conditionID;
	
	protected PNTechCondition root;
	
	protected PNCondition(byte options, boolean invisible, int conditionID, PNTechCondition root){
		this.root = root;
		this.options = options;
		this.invisible = invisible;
		this.conditionID = conditionID;
	}
	protected PNCondition(byte[] src){
		if(src == null || src.length < 6){
			this.options = 0x40;
			this.invisible = true;
			this.conditionID = -1;
		}else{
			this.options = src[1];
			this.invisible = (src[0] & 0x80) == 0x80;
			this.conditionID = src[2] << 24 | src[3] << 16 | src[4] << 8 | src[5];
		}
	}
	
	protected abstract byte getConditionType();
	protected static PNCondition loadFromBytes(byte[] src){
		if(src == null || src.length < PNCondition.generalInfoLength())return new PNConditionNLOADED(src);
		
		byte t = (byte) (src[0] & 0x7F);
		//    0: AND**
		//    1: OR**
		//    2: XOR**
		//    3: NOT**
		//    4: NUMB
		//    5: PORT
		//    6: DataField
		//    7: Time
		//    8: Constant
		//    9: ~ leeg ~
		//  10: Random
		
		PNCondition a = null;
		
		if(t == 0){
			a = PNConditionAND.loadFromBytes(src);
		}else if(t == 1){
			a = PNConditionOR.loadFromBytes(src);
		}else if(t == 2){
			a = PNConditionXOR.loadFromBytes(src);
		}else if(t == 3){
			a = PNConditionNOT.loadFromBytes(src);
		}else if(t == 4){
			a = PNConditionNUMB.loadFromBytes(src);
		}else if(t == 5){
			a = PNConditionPORT.loadFromBytes(src);
		}else if(t == 6){
			a = PNConditionDataField.loadFromBytes(src);
		}else if(t == 7){
			a = PNConditionTimeRanged.loadFromBytes(src);
		}else if(t == 8){
			a = PNConditionConstant.loadFromBytes(src);
		}else if(t == 10){
			a = PNConditionRandom.loadFromBytes(src);
		}else{
			Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Pulser: Een Condition is incorrect (het type is onbekend: \"" + t + "\")");
		}
		if(a == null){
			a = new PNConditionNLOADED(src);
		}
		return a;
	}
	protected abstract byte[] saveCondition();
	//return new PNCondition(this.source);
	//}
	
	protected static final int generalInfoLength(){return 6;}
	//return new PNCondition(this.source);
	//}
	
	protected final boolean saveGeneralInfo(byte[] ans){
		if(ans == null || ans.length < 6)return false;
		ans[0] = this.getConditionType();
		if(this.invisible)ans[0] |= 0x80;
		
		ans[1] = this.options;
		
		ans[2] = (byte) ((this.conditionID >>> 24) & 0xFF);
		ans[3] = (byte) ((this.conditionID >>> 16) & 0xFF);
		ans[4] = (byte) ((this.conditionID >>>  8) & 0xFF);
		ans[5] = (byte) ((this.conditionID       ) & 0xFF);
		return true;
	}
	//return new PNCondition(this.source);
	//}
	
	protected abstract int getEstimatedSize();
	protected final boolean getConditionValue(){
		boolean value = true;
		if((options & 0x40) == 0x40){
			value = ((options & 0x20) == 0x20);
		}else{
			value = this.calculateValue();
		}
		
		if(value){
			options |= 0x10;
		}else{
			options &= 0xEF;
		}
		return value;
	}
	protected abstract boolean calculateValue();
	protected abstract PNCondition createCopy(int conditionID, PNTechCondition base);//{
		//return new PNCondition(this.source);
	//}
		//return new PNCondition(this.source);
	//}
	

	public boolean isInvisible(){
		return this.invisible;
	}
	
	public void setInvisible(boolean newValue) throws Exception{
		this.checkDenyChanges();
		this.invisible = newValue;
		this.notifyChange();
	}
	
	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(a == null)return true;
		if(executor == null){
			a.sendMessage("§4ERROR: Executor is null bij PulserNotif-deel");
			return true;
		}
		
		if(args == null || args.length < 1){
			a.sendMessage("§eCondition-deel: §c<" + this.getTopLevelPossibilitiesString() + "> ...");
			return true;
		}
		
		String label = args[0].toLowerCase();
		
		if(label.equals("visibility")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("§4Je hebt geen toegang tot dit commando");
			}
			if(args.length < 2){
				a.sendMessage("§eDe Condition is " + (this.isInvisible()?"§4invisible":"§2visible"));
			}else{
				args[1] = args[1].toLowerCase();
				boolean invisibilityValue;
				if(args[1].equals("visible") || args[1].equals("on") || args[1].equals("+")){
					invisibilityValue = false;
				}else if(args[1].equals("invisibile") || args[1].equals("invis") || args[1].equals("off") || args[1].equals("-")){
					invisibilityValue = true;
				}else{
					a.sendMessage("§4Mogelijke nieuwe waarden voor de staat zijn: §2visible, aan, +§f of §4invisible, off, -");
					return true;
				}
				this.setInvisible(invisibilityValue);
				a.sendMessage("§eDe Condition is nu " + (this.isInvisible()?"§4invisible":"§2visible"));
			}
		}else if(label.equals("value")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("§4Je hebt geen toegang tot dit commando");
			}
			if(args.length < 2){
				a.sendMessage("§eWaardes van de condition-value:");
				a.sendMessage("§eDe value is " + (((this.options & 0x40) == 0x40)?"§4closed (de waarde wordt niet berekend maar de defaultValue wordt gebruikt)":"§2niet-closed (de waarde wordt elke keer berekend)"));
				a.sendMessage("§eDe defaultValue is " + (((this.options & 0x20) == 0x20)?"§2aan":"§4uit") + "§e (" + ((this.options & 0x20) == 0x20) + ")");
				a.sendMessage("§eDe laatste value (kan incorrect zijn als de waarde nog niet is opgevraagd na inladen) is " + (((this.options & 0x10) == 0x10)?"§2aan":"§4uit") + "§e (" + ((this.options & 0x10) == 0x10) + ")"); 
			}else{
				args[1] = args[1].toLowerCase();
				if(args[1].equals("closed")){
					if(args.length == 2){
						a.sendMessage("§eDe value is " + (((this.options & 0x40) == 0x40)?"§4closed (de waarde wordt niet berekend maar de defaultValue wordt gebruikt)":"§2niet-closed (de waarde wordt elke keer berekend)"));	
					}else if(args.length == 3){
						boolean newValue;
						if(args[2].equals("closed") || args[2].equals("on") || args[2].equals("aan")){
							newValue = true;
						}else if(args[2].equals("niet-closed") || args[2].equals("off") || args[2].equals("uit")){
							newValue = false;
						}else{
							a.sendMessage("§4Mogelijke nieuwe waarden voor de staat zijn: §2closed, on, aan§f of §4niet-closed, off, uit");
							return true;
						}
						this.checkDenyChanges();
						if(newValue){
							this.options |= 0x40;
						}else{
							this.options &= 0xBF;
						}
						this.notifyChange();
						a.sendMessage("§eDe value is nu " + (((this.options & 0x40) == 0x40)?"§4closed (de waarde wordt niet berekend maar de defaultValue wordt gebruikt)":"§2niet-closed (de waarde wordt elke keer berekend)"));
					}else{
						a.sendMessage("§eCondition-command: §cvalue last [nieuwe waarde]");
					}
				}else if(args[1].equals("default")){
					if(args.length == 2){
						a.sendMessage("§eDe defaultValue is " + (((this.options & 0x20) == 0x20)?"§2aan":"§4uit") + "§e (" + ((this.options & 0x20) == 0x20) + ")");
					}else if(args.length == 3){
						boolean newValue;
						if(args[2].equals("aan") || args[2].equals("on") || args[2].equals("+")){
							newValue = true;
						}else if(args[2].equals("uit") || args[2].equals("off") || args[2].equals("-")){
							newValue = false;
						}else{
							a.sendMessage("§4Mogelijke nieuwe waarden voor de staat zijn: §2aan, on, +§f of §4uit, off, -");
							return true;
						}
						this.checkDenyChanges();
						if(newValue){
							this.options |= 0x20;
						}else{
							this.options &= 0xDF;
						}
						this.notifyChange();
						a.sendMessage("§eDe defaultValue is nu " + (((this.options & 0x20) == 0x20)?"§2aan":"§4uit") + "§e (" + ((this.options & 0x20) == 0x20) + ")");
					}else{
						a.sendMessage("§eCondition-command: §cvalue default [nieuwe waarde]");
					}
				}else if(args[1].equals("laatste")){
					if(args.length == 2){
						a.sendMessage("§eDe laatste value (kan incorrect zijn als de waarde nog niet is opgevraagd na inladen) is " + (((this.options & 0x10) == 0x10)?"§2aan":"§4uit") + "§e (" + ((this.options & 0x10) == 0x10) + ")"); 
					}else{
						a.sendMessage("§eCondition-command: §cvalue last");
					}
				}else if(args[1].equals("calculate")){
					if(args.length == 2){
						long start = System.currentTimeMillis();
						boolean value = this.getConditionValue();
						long stop = System.currentTimeMillis();
						long duration = stop - start;
						a.sendMessage("§eDe berekende waarde is " + (value?"§2aan":"§4uit") + "§e (" + value + "). Het duurde ongeveer " + duration + " milliseconden om de waarde te berekenen");
					}else{
						a.sendMessage("§eCondition-command: §cvalue calculate");
					}
				}else{
					a.sendMessage("§eCondition-command: §cvalue <closed|default|laatste|calculate> <...>");
				}
			}
		}else if(label.equals("conditionid")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			a.sendMessage("§eConditionID = " + this.conditionID);
		}else if(label.equals("gettype")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			a.sendMessage("§eConditionType = " + this.getConditionType());
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
	public List<String> autoCompleteObjectCommand(String s) throws Exception {
		ArrayList<String> a = new ArrayList<String>();
		s = s.toLowerCase();
		String[] possibilities = this.getTotalTopLevelArgsPossibilities();
		for(int i = 0; i < possibilities.length; i++){
			if(possibilities[i] != null && possibilities[i].startsWith(s)){
				a.add(possibilities[i]);
			}
		}
		return a;
	}	
	public String getTopLevelPossibilitiesString(){
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
		String[] general = new String[]{"visibility","value"};
		String[] local = this.getLocalTopLevelArgsPossibilities();
		if(local == null)local = new String[0];
		
		String[] total = new String[general.length + local.length];
		System.arraycopy(general, 0, total, 0, general.length);
		System.arraycopy(local, 0, total, general.length, local.length);
		return total;
	}
	public abstract String[] getLocalTopLevelArgsPossibilities();
	
	protected void notifyChange(){
		if(this.root != null)this.root.notifyChange();
	}
	public void checkDenyChanges() throws Exception{
		if(this.denyChanges())throw new Exception("Veranderingen zijn niet toegestaan voor de PulserNotif. Controleer read-only");
	}
	public boolean denyChanges(){
		return this.root != null && this.root.notificationBase != null && this.root.notificationBase.denyChanges();
	}
	
	@Override
	public String toString(){
		return "PNCondition[conditionID=" + this.conditionID + ",invisible=" + this.invisible + ",valueClosed=" + ((this.options & 0x40) == 0x40) + ",valueDefault=" + ((this.options & 0x20) == 0x20) + ",valueLast=" + ((this.options & 0x10) == 0x10) + "]";
	}
	
}
