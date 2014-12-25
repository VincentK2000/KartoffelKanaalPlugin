package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;


public class PNConditionNLOADED extends PNCondition{
	//NLOADED = NOT LOADED
	byte[] data;

	protected PNConditionNLOADED(byte[] src){
		super(src);
		this.invisible = true;
		this.data = (src.length > 500000)?new byte[0]:src;
	}
	
	protected PNConditionNLOADED(byte[] src, byte options, int conditionID){
		super(options, true, conditionID, null);
		this.data = (src.length > 500000)?new byte[0]:src;
	}
	
	@Override
	protected byte getConditionType() {return 0;}

	protected byte[] saveCondition(){return data;}

	@Override
	protected boolean calculateValue() {
		return (options & 0x20) == 0x20;
	}

	@Override
	protected PNConditionNLOADED createCopy(int conditionID, PNTechCondition base) {
		return new PNConditionNLOADED(data, this.options, this.conditionID);
	}

	@Override
	protected int getEstimatedSize() {
		return (data == null)?0:data.length;
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
	public PNCondition copyCondition(int ID, PNTechCondition root) throws Exception {
		throw new Exception("Je kan geen kopie maken van een PNConditionNLOADED");
	}
	
	public static PNCondition createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		throw new Exception("Je kan geen PNConditionNLOADED aanmaken");
	}
}
