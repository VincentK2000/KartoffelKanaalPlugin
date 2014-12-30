package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

public class PNConditionNOT extends PNCondition{
	PNCondition c;
	
	protected PNConditionNOT(PNCondition child, byte options, boolean invisible, int conditionID, PNTechCondition root){
		super(options, invisible, conditionID, root);
		this.c = child;
	}
	
	protected PNConditionNOT(PNCondition child, byte[] src){
		super(src);
		this.c = child;
	}
	
	@Override
	protected byte getConditionType() {return 3;}
	
	protected boolean calculateValue(){
		if(c == null || c.isInvisible()){
			return ((this.options & 0x20) == 0x20);
		}else{
			return !c.getConditionValue();
		}
	}

	@Override
	protected int getEstimatedSize() {
		return ((this.c == null)?0:this.c.getEstimatedSize()) + PNCondition.generalInfoLength();
	}

	public static PNConditionNOT loadFromBytes(byte[] src) {
		if(src == null || src.length < PNCondition.generalInfoLength() || src.length > 500000)return null;
		
		byte[] conditiondata = new byte[src.length - PNCondition.generalInfoLength()];
		System.arraycopy(src, PNCondition.generalInfoLength(), conditiondata, 0, conditiondata.length);
		PNCondition c = PNCondition.loadFromBytes(conditiondata);
		
		return new PNConditionNOT(c, src);
	}

	@Override
	protected byte[] saveCondition() {
		if(c == null)return new byte[0];
		byte[] condition = c.saveCondition();
		if(condition == null || condition.length == 0)return new byte[0];
		
		byte[] ans = new byte[condition.length + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		System.arraycopy(condition, 0, ans, PNCondition.generalInfoLength(), condition.length);
		
		return ans;
	}
	
	public static PNConditionNOT createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		throw new Exception("Functie nog niet beschikbaar");
	}

	@Override
	public PNConditionNOT createCopy(int ID, PNTechCondition root) throws Exception {
		return new PNConditionNOT(this.c.createCopy(601, root), this.options, true, ID, root);//TODO Generate subCondition dynamically
	}
}
