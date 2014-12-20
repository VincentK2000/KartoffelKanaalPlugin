package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.StoreTechnics;

public class PNConditionNUMB extends PNCondition{
	protected PNCondition[] arr;
	protected byte min = 0;
	protected byte max = 127;
	
	protected PNConditionNUMB(PNCondition[] children, byte minimum, byte maximum, byte options, boolean invisible, int conditionID, PNTechCondition root){
		super(options, invisible, conditionID, root);
		this.arr = children;
		this.min = minimum;
		this.max = maximum;
	}
	
	protected PNConditionNUMB(PNCondition[] children, byte minimum, byte maximum, byte[] src){
		super(src);
		this.arr = children;
		this.min = minimum;
		this.max = maximum;
	}
	
	@Override
	protected byte getConditionType() {return 4;}
	
	protected boolean calculateValue(){
		if(arr == null)return ((this.options & 0x20) == 0x20);
		boolean atLeastOne = false;
		int count = 0;
		for(int i = 0; i < arr.length; i++){
			if(this.arr[i] == null || this.arr[i].isInvisible())continue;
			atLeastOne = true;
			if(this.arr[i].getConditionValue())count++;
		}
		if(atLeastOne){
			return count >= min && count <= max;
		}else{
			return ((this.options & 0x20) == 0x20);
		}
	}

	@Override
	protected byte[] saveCondition(){
		byte[][] a = new byte[this.arr.length][];
		for(int i = 0; i < arr.length; i++){
			if(arr[i] == null){
				a[i] = new byte[0];
			}else{
				a[i] = arr[i].saveCondition();
				if(a[i] == null)a[i] = new byte[0];
			}
		}
		byte[] solution = StoreTechnics.saveArray(a, 100);
		if(solution == null || solution.length > 500000)solution = new byte[0];
		
		byte[] ans = new byte[solution.length + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		ans[PNCondition.generalInfoLength()    ] = min;
		ans[PNCondition.generalInfoLength() + 1] = max;
		
		System.arraycopy(solution, 0, ans, PNCondition.generalInfoLength() + 2, solution.length);
		
		return ans;
	}

	public static PNConditionNUMB loadFromBytes(byte[] src) {
		if(src == null || src.length < PNCondition.generalInfoLength() || src.length > 500000)return null;
		byte[][] c = StoreTechnics.loadArray(src, 100, 100000, PNCondition.generalInfoLength() + 2);
		PNCondition[] conditions = new PNCondition[c.length];
		for(int i = 0; i < c.length; i++){
			conditions[i] = PNCondition.loadFromBytes(src);
		}
		
		return new PNConditionNUMB(conditions, src[6], src[7], src);
	}

	@Override
	protected PNConditionAND createCopy(int id, PNTechCondition base) {
		return null;
	}

	@Override
	protected int getEstimatedSize() {
		if(arr == null)return PNCondition.generalInfoLength();
		int l = PNCondition.generalInfoLength() + arr.length * 4;
		for(int i = 0; i < arr.length; i++){
			if(arr[i] != null)l += arr[i].getEstimatedSize();
		}
		return l;
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
