package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.List;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;

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
	protected PNConditionConstant createCopy(int id, PNTechCondition base) {
		return new PNConditionConstant(this.options, this.invisible, id, base);
	}

	@Override
	protected int getEstimatedSize() {
		return PNCondition.generalInfoLength();
	}
	
	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		return null;
	}

	@Override
	public List<String> autoCompleteSubObjectCH(String s) throws Exception {
		return null;
	}


	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		return null;
	}
}
