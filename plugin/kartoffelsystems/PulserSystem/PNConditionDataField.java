package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class PNConditionDataField extends PNCondition{
	byte[] data;
	
	protected PNConditionDataField(byte[] data, byte options, boolean invisible, int conditionID, PNTechCondition root){
		super(options, invisible, conditionID, root);
		this.data = (data.length > 5000)?new byte[0]:data;
	}
	
	protected PNConditionDataField(byte[] data, byte[] src){
		super(src);
		this.data = (data.length > 5000)?new byte[0]:data;
	}
	
	@Override
	protected byte getConditionType() {return 6;}
	
	protected boolean calculateValue(){
		return true;
	}

	@Override
	protected byte[] saveCondition() {
		if(data.length > 5000)return new byte[0];
		
		byte[] ans = new byte[data.length + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		System.arraycopy(data, 0, ans, PNCondition.generalInfoLength(), data.length);
		
		return ans;
	}
	
	public static PNConditionDataField loadFromBytes(byte[] src) {
		if(src == null || src.length < PNCondition.generalInfoLength() || src.length > 500000)return null;
		
		byte[] data = null;
		if(src.length > PNCondition.generalInfoLength()){
			System.arraycopy(src, PNCondition.generalInfoLength(), data, 0, src.length - 6);
		}else{
			data = new byte[0];
		}
		
		return new PNConditionDataField(data, src);
	}

	@Override
	protected PNConditionDataField createCopy(int id, PNTechCondition base) {
		return new PNConditionDataField(this.data, this.options, this.invisible, id, base);
	}

	@Override
	protected int getEstimatedSize() {
		return data.length + PNCondition.generalInfoLength();
	}
	
	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		
		return false;
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args) throws Exception {
		ArrayList<String> a = super.autoCompleteObjectCommand(args);
		if(a == null)a = new ArrayList<String>();
		
		return a;
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
		throw new Exception("Functie nog niet beschikbaar");
	}
	
	public static PNConditionDataField createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		throw new Exception("Functie nog niet beschikbaar");
	}

}
