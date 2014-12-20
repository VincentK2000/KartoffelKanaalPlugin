package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;

public class PNTechCondition extends PNTech{
	private PNCondition root;
	
	protected PNTechCondition(PNCondition root, boolean invisible, int ID, PulserNotifStandard notificationBase){
		super(invisible, ID, notificationBase);
		this.root = root;
		if(this.root != null)this.root.root = this;
	}
	
	protected PNTechCondition(PNCondition root, byte[] src){
		super(src);
		this.root = root;
		if(this.root != null)this.root.root = this;
	}
	
	protected boolean preventMessage(){
		if(this.root == null)return false;
		//System.out.println("De value berekenen van de rootcondition...");
		boolean b = this.root.getConditionValue();
		//System.out.println("De value is " + b);
		return !b;
	}
	
	@Override
	public byte getTechType() {return 2;}
	
	protected static PNTechCondition loadFromBytes(byte[] src){
		if(src == null || src.length < PNTech.generalInfoLength())return null;
		
		byte[] conditionData = new byte[src.length - PNTech.generalInfoLength()];
		
		System.arraycopy(src, PNTech.generalInfoLength(), conditionData, 0, conditionData.length);
		
		return new PNTechCondition(PNCondition.loadFromBytes(conditionData), src);
	}
	
	protected byte[] saveTech(){
		if(root == null)return new byte[0];
		
		byte[] conditionArr = root.saveCondition();
		
		byte[] ans = new byte[conditionArr.length + PNTech.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		System.arraycopy(conditionArr, 0, ans, PNTech.generalInfoLength(), conditionArr.length);
		
		return ans;
	}
	
	@Override
	public int getEstimatedSize() {
		return PNTech.generalInfoLength() + root.getEstimatedSize();
	}
	
	protected PNCondition getBaseCondition(){
		return this.root;
	}
	
	protected void setBaseCondition(PNCondition c){
		this.root = c;
		if(this.root != null){
			this.root.root = this;
		}
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		if(path.equals("root") || path.equals("base") || path.equals("condition"))return this.root;
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s) throws Exception {
		ArrayList<String> a = super.autoCompleteSubObjectCH(s);
		if(a == null)a = new ArrayList<String>(1);
		
		s = s.toLowerCase();
		if("root".startsWith(s))a.add("root");
		if("base".startsWith(s))a.add("base");
		if("condition".startsWith(s))a.add("condition");
		return a;
	}
	
	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		return new String[0];
	}
}
