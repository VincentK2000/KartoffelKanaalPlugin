package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;


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

	@Override
	protected boolean calculateValue() {
		return (options & 0x20) == 0x20;
	}

	@Override
	protected int getEstimatedSize() {
		return (data == null)?0:data.length;
	}

	protected byte[] saveCondition(){return data;}

	public static PNCondition createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		throw new Exception("Je kan geen PNConditionNLOADED aanmaken");
	}

	@Override
	public PNCondition createCopy(int ID, PNTechCondition root) throws Exception {
		throw new Exception("Je kan geen kopie maken van een PNConditionNLOADED");
	}
}
