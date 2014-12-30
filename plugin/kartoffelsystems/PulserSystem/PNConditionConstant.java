package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class PNConditionConstant extends PNCondition{
	
	protected PNConditionConstant(byte options, boolean invisible, int conditionID, PNTechCondition root){
		super(options, invisible, conditionID, root);
	}
	
	protected PNConditionConstant(byte[] src){
		super(src);
	}
	
	protected PNConditionConstant(boolean value, boolean invisible, int conditionID, PNTechCondition root){
		super((byte) (0x40 | (value?0x20:0x00)), invisible, conditionID, root);
	}
	
	@Override
	protected byte getConditionType() {return 8;}

	@Override
	protected boolean calculateValue() {
		options |= 0x40;
		return (options & 0x20) == 0x20;
	}
	
	protected static PNConditionConstant loadFromBytes(byte[] src){
		if(src == null || src.length < PNCondition.generalInfoLength())return null;
		return new PNConditionConstant(src);
	}

	@Override
	protected byte[] saveCondition() {
		byte[] ans = new byte[PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		return ans;
	}

	@Override
	protected int getEstimatedSize() {
		return PNCondition.generalInfoLength();
	}
	
	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		String label = args[0];
		if(label.equals("aan") || label.equals("on") || label.equals("true") || label.equals("+")){
			this.options |= 0x20;
			a.sendMessage("§eDe ConditionConstant staat nu op §2aan§e.");
		}else if(label.equals("uit") || label.equals("off") || label.equals("false") || label.equals("-")){
			this.options &= 0xDF;
			a.sendMessage("§eDe ConditionConstant staat nu op §2uit§e.");
		}else{
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a) throws Exception {
		a = super.autoCompleteObjectCommand(args, a);
		
		return a;
	}
	
	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception {
		return super.autoCompleteSubObjectCH(s, a);
	}
	
	@Override
	public PNConditionConstant createCopy(int ID, PNTechCondition root) throws Exception {
		return new PNConditionConstant(this.options, true, ID, root);
	}
	
	public static PNConditionConstant createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		if(params.length == 1){
			params[0] = params[0].toLowerCase();
			boolean value;
			if(params[0].equals("aan") || params[0].equals("on") || params[0].equals("true") || params[0].equals("+")){
				value = true;
			}else if(params[0].equals("uit") || params[0].equals("off") || params[0].equals("false") || params[0].equals("-")){
				value = false;
			}else{
				throw new Exception("De beginstaat kan zijn: aan/on/true/+ of uit/off/false/-");
			}
			options |= 0x40;
			if(value){
				options |= 0x20;
			}else{
				options &= 0xDF;
			}
			return new PNConditionConstant(options, true, ID, root);
		}else{
			throw new Exception("Om een ConditionConstant te maken, moet je de beginstaat specifiëren (aan/uit)");
		}
	}
}
