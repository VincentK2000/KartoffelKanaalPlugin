package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.StoreTechnics;

public class PNConditionXOR extends PNCondition{

	protected PNCondition[] arr;

	protected PNConditionXOR(PNCondition[] children, byte options, boolean invisible, int conditionID, PNTechCondition root){
		super(options, invisible, conditionID, root);
		this.arr = children;
	}
	
	protected PNConditionXOR(PNCondition[] children, byte[] src){
		super(src);
		this.arr = children;
	}
	
	@Override
	protected byte getConditionType() {return 2;}
	
	protected boolean calculateValue(){
		if(arr == null)return ((this.options & 0x20) == 0x20);
		boolean atLeastOne = false;
		boolean value = false;
		for(int i = 0; i < arr.length; i++){
			if(this.arr[i] == null || this.arr[i].isInvisible())continue;
			atLeastOne = true;
			if(arr[i].getConditionValue()){
				if(value){
					return false;
				}
				value = true;
			}
		}
		if(atLeastOne){
			return value;
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
		
		System.arraycopy(solution, 0, ans, PNCondition.generalInfoLength(), solution.length);
		
		return ans;
	}

	public static PNConditionXOR loadFromBytes(byte[] src) {
		if(src == null || src.length < PNCondition.generalInfoLength() || src.length > 500000)return null;
		byte[][] c = StoreTechnics.loadArray(src, 100, 100000, PNCondition.generalInfoLength());
		PNCondition[] conditions = new PNCondition[c.length];
		for(int i = 0; i < c.length; i++){
			conditions[i] = PNCondition.loadFromBytes(src);
		}
		
		return new PNConditionXOR(conditions, src);
	}

	@Override
	protected PNConditionXOR createCopy(int id, PNTechCondition root) {
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
		if(path.startsWith("#")){
			int arrIndex;
			try{
				arrIndex = Integer.parseInt(path.substring(1));
			}catch(NumberFormatException e){
				return null;
			}
			if(arrIndex < 0 || arrIndex >= this.arr.length)return null;
			return this.arr[arrIndex];
		}
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
