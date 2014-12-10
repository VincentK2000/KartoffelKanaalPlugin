package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.List;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;

public class PNConditionPORT extends PNCondition{
	protected PNCondition port;
	protected PNCondition valuator;
	
	protected PNConditionPORT(PNCondition port, PNCondition valuator, byte options, boolean invisible, int conditionID, PNTechCondition root){
		super(options, invisible, conditionID, root);
		this.port = port;
		this.valuator = valuator;
	}
	
	protected PNConditionPORT(PNCondition port, PNCondition valuator, byte[] src){
		super(src);
		this.port = port;
		this.valuator = valuator;
	}
	
	@Override
	protected byte getConditionType() {return 5;}
	
	protected boolean calculateValue(){
		if(port == null || port.isInvisible() || valuator == null || valuator.isInvisible())return ((this.options & 0x20) == 0x20);
		if(port.getConditionValue()){
			return valuator.getConditionValue();
		}else{
			return ((this.options & 0x20) == 0x20);
		}
	}

	@Override
	protected byte[] saveCondition(){
		byte[] p = null;
		byte[] v = null;
		if(this.port != null)p = this.port.saveCondition();
		if(this.valuator != null)v = this.valuator.saveCondition();
		
		if(p == null)p = new byte[0];
		if(v == null)v = new byte[0];
		
		byte[] ans = new byte[p.length + v.length + 8 + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		int s = PNCondition.generalInfoLength();
		ans[s    ] = (byte) ((p.length >>> 24) & 0xFF);
		ans[s + 1] = (byte) ((p.length >>> 16) & 0xFF);
		ans[s + 2] = (byte) ((p.length >>>  8) & 0xFF);
		ans[s + 3] = (byte) ((p.length       ) & 0xFF);
		System.arraycopy(p, 0, ans, s + 4, p.length);
		
		s += p.length;
		ans[s + 4] = (byte) ((v.length >>> 24) & 0xFF);
		ans[s + 5] = (byte) ((v.length >>> 16) & 0xFF);
		ans[s + 6] = (byte) ((v.length >>>  8) & 0xFF);
		ans[s + 7] = (byte) ((v.length       ) & 0xFF);
		System.arraycopy(v, 0, ans, s + 8, v.length);
		
		return ans;
	}

	public static PNConditionPORT loadFromBytes(byte[] src) {
		if(src == null || src.length < PNCondition.generalInfoLength() + 8 || src.length > 500000)return null;
		int plength = src[ 6] << 24 | src[7] << 16 | src[8] << 8 | src[9];
		if(src.length < PNCondition.generalInfoLength() + 8 + plength)return null;
		
		int vlength = src[10 + plength] << 24 | src[11 + plength] << 16 | src[12 + plength] << 8 | src[13 + plength];
		if(src.length != PNCondition.generalInfoLength() + 8 + plength + vlength)return null;
		
		byte[] p = new byte[plength];
		byte[] v = new byte[vlength];
		
		System.arraycopy(src, PNCondition.generalInfoLength() + 4, p, 0, plength);
		System.arraycopy(src, PNCondition.generalInfoLength() + 8 + plength, 0, 0, vlength);
		
		PNCondition port = PNCondition.loadFromBytes(p);
		PNCondition value = PNCondition.loadFromBytes(v);
		
		return new PNConditionPORT(port, value, src);
	}

	@Override
	protected PNConditionPORT createCopy(int id, PNTechCondition root) {
		return null;
	}

	@Override
	protected int getEstimatedSize() {
		return (this.port == null?0:this.port.getEstimatedSize()) + (this.valuator == null?0:this.valuator.getEstimatedSize()) + 8 + PNCondition.generalInfoLength();
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> autoCompleteSubObjectCH(String s) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
