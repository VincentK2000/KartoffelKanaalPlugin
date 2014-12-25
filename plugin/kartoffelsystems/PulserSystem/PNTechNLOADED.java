package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;

public class PNTechNLOADED extends PNTech{
//NLOADED = Not loaded
//Dit is geen Tech voor een PulserNotification, maar een vervanger voor een niet-virtuele of ongeladen data
	protected byte[] data;
	
	protected PNTechNLOADED(byte[] src){
		super(src);
		//System.out.println("        PNTech.loadFromBytes: PNTechNLoaded wordt constructed met een grote van " + src.length);
		this.data = (src.length > 500000)?new byte[0]:src;
	}
	
	@Override
	public byte getTechType() {return 0;}

	protected byte[] saveTech(){return data;}
	
	@Override
	public boolean isInvisible(){
		return true;
	}

	@Override
	public int getEstimatedSize() {
		return data.length;
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
	
	public static PNTechCondition createFromParams(String[] params, int ID, PulserNotifStandard notificationBase) throws Exception {
		throw new Exception("Je kan geen PNTechNLOADED aanmaken");
	}
	
	@Override
	public PNTech copyTech(int ID, PulserNotifStandard notificationBase) throws Exception{
		throw new Exception("Je kan geen kopie maken van een PNTechNLOADED");
	}
	
	@Override
	public String getTypeName(){
		return "!!!NLOADED!!!";
	}
}
