package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;
import java.util.Random;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;

public class PNConditionRandom extends PNCondition{
	protected Random r;
	protected int total;
	protected int positiveAmount;
	
	
	protected PNConditionRandom(int total, int positiveAmount, byte options, boolean invisible, int conditionID, PNTechCondition base){
		super(options, invisible, conditionID, base);
		this.total = total;
		this.positiveAmount = positiveAmount;
	}
	
	protected PNConditionRandom(int total, int positiveAmount, byte[] src){
		super(src);
		this.total = total;
		this.positiveAmount = positiveAmount;
	}
	
	@Override
	protected byte getConditionType() {return 10;}

	@Override
	protected boolean calculateValue() {
		if(r == null)r = new Random();
		int a = r.nextInt(total);
		return a < this.positiveAmount;
	}

	protected static PNConditionRandom loadFromBytes(byte[] src){
		if(src == null || src.length < PNCondition.generalInfoLength() + 8)return null;
		
		int s = PNCondition.generalInfoLength();
		
		int total = src[s++] << 24 | src[s++] << 16 | src[s++] << 8 | src[s++];//de ++ moet pas na de waarde-opvraging gebeuren aangezien de lengte van de generalInfo wordt gebruikt
		int positiveAmount = src[s++] << 24 | src[s++] << 16 | src[s++] << 8 | src[s++];
		
		return new PNConditionRandom(total, positiveAmount, src);
	}
	
	@Override
	protected byte[] saveCondition() {
		byte[] ans = new byte[8 + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		int s = PNCondition.generalInfoLength();
		ans[s++] = (byte)((this.total >>> 24) & 0xFF);//de ++ moet pas na de waarde-opvraging gebeuren aangezien de lengte van de generalInfo wordt gebruikt
		ans[s++] = (byte)((this.total >>> 16) & 0xFF);
		ans[s++] = (byte)((this.total >>>  8) & 0xFF);
		ans[s++] = (byte)((this.total       ) & 0xFF);
		
		ans[s++] = (byte)((this.positiveAmount >>> 24) & 0xFF);
		ans[s++] = (byte)((this.positiveAmount >>> 16) & 0xFF);
		ans[s++] = (byte)((this.positiveAmount >>>  8) & 0xFF);
		ans[s  ] = (byte)( this.positiveAmount         & 0xFF);
		
		return ans;
	}

	@Override
	protected PNCondition createCopy(int id, PNTechCondition root) {
		return new PNConditionRandom(this.total, this.positiveAmount, this.options, this.invisible, id, root);
	}

	@Override
	protected int getEstimatedSize() {
		return 8 + PNCondition.generalInfoLength();
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
	
}
